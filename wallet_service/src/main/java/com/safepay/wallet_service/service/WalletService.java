package com.safepay.wallet_service.service;

import com.safepay.wallet_service.dto.*;
import com.safepay.wallet_service.entity.Transaction;
import com.safepay.wallet_service.entity.Wallet;
import com.safepay.wallet_service.entity.WalletHold;
import com.safepay.wallet_service.exception.InsufficientFundsException;
import com.safepay.wallet_service.exception.NotFoundException;
import com.safepay.wallet_service.repository.TransactionRepository;
import com.safepay.wallet_service.repository.WalletHoldRepository;
import com.safepay.wallet_service.repository.WalletRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletHoldRepository walletHoldRepository;
    private final TransactionRepository transactionRepository;
    private final MongoTemplate mongoTemplate;

    public WalletService(
            WalletRepository walletRepository,
            WalletHoldRepository walletHoldRepository,
            TransactionRepository transactionRepository,
            MongoTemplate mongoTemplate
    ) {
        this.walletRepository = walletRepository;
        this.walletHoldRepository = walletHoldRepository;
        this.transactionRepository = transactionRepository;
        this.mongoTemplate = mongoTemplate;
    }


    public WalletResponse createWallet(CreateWalletRequest request) {
        try {
            Wallet wallet = new Wallet(request.getUserId(), request.getCurrency());
            Wallet saved = walletRepository.save(wallet);
            return toResponse(saved);
        } catch (DuplicateKeyException ex) {
            throw new IllegalStateException("Wallet already exists for user");
        }
    }

    public WalletResponse credit(CreditRequest request) {

        if (transactionRepository.existsByReferenceId(request.getReferenceId())) {
            Wallet wallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new NotFoundException("Wallet not found"));
            return toResponse(wallet);
        }

        Query query = new Query(
                Criteria.where("userId").is(request.getUserId())
                        .and("currency").is(request.getCurrency())
        );

        Update update = new Update()
                .inc("balance", request.getAmount())
                .inc("availableBalance", request.getAmount())
                .set("updatedAt", LocalDateTime.now());

        Wallet updated = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Wallet.class
        );

        if (updated == null) {
            throw new NotFoundException("Wallet not found");
        }

        transactionRepository.save(
                new Transaction(
                        updated.getId(),
                        "CREDIT",
                        request.getAmount(),
                        "SUCCESS",
                        request.getReferenceId()
                )
        );

        return toResponse(updated);
    }


    public WalletResponse debit(DebitRequest request) {

        if (transactionRepository.existsByReferenceId(request.getReferenceId())) {
            Wallet wallet = walletRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new NotFoundException("Wallet not found"));
            return toResponse(wallet);
        }

        Query query = new Query(
                Criteria.where("userId").is(request.getUserId())
                        .and("currency").is(request.getCurrency())
                        .and("availableBalance").gte(request.getAmount())
        );

        Update update = new Update()
                .inc("balance", -request.getAmount())
                .inc("availableBalance", -request.getAmount())
                .set("updatedAt", LocalDateTime.now());

        Wallet updated = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Wallet.class
        );

        if (updated == null) {
            throw new InsufficientFundsException("Insufficient balance");
        }

        transactionRepository.save(
                new Transaction(
                        updated.getId(),
                        "DEBIT",
                        request.getAmount(),
                        "SUCCESS",
                        request.getReferenceId()
                )
        );

        return toResponse(updated);
    }



    public HoldResponse placeHold(HoldRequest request) {

        Query query = new Query(
                Criteria.where("userId").is(request.getUserId())
                        .and("currency").is(request.getCurrency())
                        .and("availableBalance").gte(request.getAmount())
        );

        Update update = new Update()
                .inc("availableBalance", -request.getAmount())
                .set("updatedAt", LocalDateTime.now());

        Wallet wallet = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Wallet.class
        );

        if (wallet == null) {
            throw new InsufficientFundsException("Not enough balance");
        }

        WalletHold hold = new WalletHold(
                wallet.getId(),
                request.getHoldReference(),
                request.getAmount(),
                LocalDateTime.now().plusMinutes(10)
        );

        walletHoldRepository.save(hold);

        return new HoldResponse(
                hold.getHoldReference(),
                hold.getAmount(),
                hold.getStatus()
        );
    }



    @Transactional
    public WalletResponse captureHold(CaptureRequest request) {

        WalletHold hold = walletHoldRepository.findByHoldReference(request.getHoldReference())
                .orElseThrow(() -> new NotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold already processed");
        }

        Query query = new Query(Criteria.where("_id").is(hold.getWalletId()));

        Update update = new Update()
                .inc("balance", -hold.getAmount())
                .set("updatedAt", LocalDateTime.now());

        Wallet wallet = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Wallet.class
        );

        hold.setStatus("CAPTURED");
        walletHoldRepository.save(hold);

        return toResponse(wallet);
    }



    public HoldResponse releaseHold(String holdReference) {

        WalletHold hold = walletHoldRepository.findByHoldReference(holdReference)
                .orElseThrow(() -> new NotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold already processed");
        }

        Query query = new Query(Criteria.where("_id").is(hold.getWalletId()));

        Update update = new Update()
                .inc("availableBalance", hold.getAmount())
                .set("updatedAt", LocalDateTime.now());

        mongoTemplate.updateFirst(query, update, Wallet.class);

        hold.setStatus("RELEASED");
        walletHoldRepository.save(hold);

        return new HoldResponse(
                hold.getHoldReference(),
                hold.getAmount(),
                hold.getStatus()
        );
    }


    public WalletResponse getWallet(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));
        return toResponse(wallet);
    }


    private WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getAvailableBalance()
        );
    }
}

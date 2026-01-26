package com.safepay.wallet_service.repository;

import com.safepay.wallet_service.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByWalletId(String walletId);

    List<Transaction> findByWalletIdAndType(String walletId, String type);
}

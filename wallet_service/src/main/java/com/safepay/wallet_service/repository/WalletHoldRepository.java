package com.safepay.wallet_service.repository;

import com.safepay.wallet_service.entity.WalletHold;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletHoldRepository extends MongoRepository<WalletHold, String> {

    Optional<WalletHold> findByHoldReference(String holdReference);

    List<WalletHold> findByStatusAndExpiresAtBefore(String status, LocalDateTime time);

    List<WalletHold> findByWalletIdAndStatus(String walletId, String status);
}

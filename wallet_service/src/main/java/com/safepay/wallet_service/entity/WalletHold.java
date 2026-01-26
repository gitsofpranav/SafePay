package com.safepay.wallet_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "wallet_holds")
public class WalletHold {

    @Id
    private String id;  

    @Indexed
    private String walletId;  

    @Indexed(unique = true)
    private String holdReference;

    private Long amount;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public WalletHold() {
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }

    public WalletHold(String walletId, String holdReference, Long amount, LocalDateTime expiresAt) {
        this.walletId = walletId;
        this.holdReference = holdReference;
        this.amount = amount;
        this.expiresAt = expiresAt;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getHoldReference() {
        return holdReference;
    }

    public void setHoldReference(String holdReference) {
        this.holdReference = holdReference;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}

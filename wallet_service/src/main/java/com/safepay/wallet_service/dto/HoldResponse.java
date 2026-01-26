package com.safepay.wallet_service.dto;

public class HoldResponse {

    private final String holdReference;
    private final Long amount;
    private final String status;

    public HoldResponse() {
        this.holdReference = null;
        this.amount = null;
        this.status = null;
    }

    public HoldResponse(String holdReference, Long amount, String status) {
        this.holdReference = holdReference;
        this.amount = amount;
        this.status = status;
    }

    public String getHoldReference() {
        return holdReference;
    }

    public Long getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}

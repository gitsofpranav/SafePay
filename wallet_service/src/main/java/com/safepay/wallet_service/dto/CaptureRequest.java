package com.safepay.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;

public class CaptureRequest {

    @NotBlank(message = "holdReference is required")
    private String holdReference;

    @NotBlank(message = "referenceId is required for idempotency")
    private String referenceId;

    public CaptureRequest() {}

    public String getHoldReference() {
        return holdReference;
    }

    public void setHoldReference(String holdReference) {
        this.holdReference = holdReference;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}

package com.safepay.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferRequest {
    
    private String senderName;
    private String receiverName;
    private Double amount;
}

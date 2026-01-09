package com.safepay.transaction_service.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String id;

    @NotBlank
    private String senderName;

    @NotBlank
    private String receiverName;

    @Positive(message = "Amount must be positive")
    private Double amount;

    @CreatedDate
    private LocalDateTime timestamp;

    private String status = "PENDING";
}

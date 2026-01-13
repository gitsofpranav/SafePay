package com.safepay.notification_service.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    private String id;

    private String senderId;
    private String recieverId;
    // @Positive(message = "Amount must be positive")
    private Double amount;

    @CreatedDate
    private LocalDateTime timestamp;

     private String status;

}

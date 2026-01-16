package com.safepay.reward_service.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "transactions")
@CompoundIndex(
    name = "sender_receiver_timestamp_idx",
    def = "{ 'senderId': 1, 'receiverId': 1, 'timestamp': -1 }"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String id;

    @Indexed
    private String senderId;

    @Indexed
    private String receiverId;

   
    private Double amount;

    @Indexed
    private LocalDateTime timestamp;

    @Indexed
    private String status; 

    
}

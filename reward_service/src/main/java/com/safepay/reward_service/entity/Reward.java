package com.safepay.reward_service.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "reward")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    
    @Id
    private String id;
    private String userId;
    private Double points;
    private LocalDateTime sentAt;
    @Indexed(unique = true)
    private String transactionId;

}

package com.safepay.reward_service.kafka;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safepay.reward_service.entity.Reward;
import com.safepay.reward_service.entity.Transaction;
import com.safepay.reward_service.repository.RewardRepository;


@Component
public class RewardConsumer {
     private final RewardRepository rewardRepository;
    private final ObjectMapper mapper;

    public RewardConsumer(RewardRepository rewardRepository, ObjectMapper mapper) {
        this.rewardRepository = rewardRepository;

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "txn-initiated", groupId = "reward-group")
    public void consumerTransaction(Transaction transaction){
        try {
            if(rewardRepository.existsByTransactionId(transaction.getId())){
                System.out.println("⚠️ Reward already exists for transaction: " + transaction.getId());
                return;
            }
            Reward reward = new Reward();
            reward.setUserId(transaction.getSenderId());
            reward.setPoints(transaction.getAmount() * 100);
            reward.setSentAt(LocalDateTime.now());
            reward.setTransactionId(transaction.getId());

            rewardRepository.save(reward);
            System.out.println("✅ Reward saved: " + reward);
        }catch (Exception e){
            System.err.println("❌ Failed to process transaction " + transaction.getId() + ": " + e.getMessage());
            throw e; // Let Spring Kafka handle the retry
        }
    }
}

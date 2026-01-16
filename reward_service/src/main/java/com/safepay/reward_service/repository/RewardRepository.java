package com.safepay.reward_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.safepay.reward_service.entity.Reward;

@Repository
public interface RewardRepository extends MongoRepository<Reward,String> {
    List<Reward> findByUserId(String userId);

    boolean existsByTransactionId(String transactionId);
}

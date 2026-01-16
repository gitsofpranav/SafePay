package com.safepay.reward_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safepay.reward_service.entity.Reward;
import com.safepay.reward_service.repository.RewardRepository;

@Service
public class RewardServiceImpl {
    
    @Autowired
    private RewardRepository rewardRepository;

     public Reward sendReward(Reward reward) {
        reward.setSentAt(LocalDateTime.now());
        return rewardRepository.save(reward);
    }

    public List<Reward> getAllReward(){
        return rewardRepository.findAll();
    }

    public List<Reward> getRewardsByUserId(String userId) {
        return rewardRepository.findByUserId(userId);
    }
}


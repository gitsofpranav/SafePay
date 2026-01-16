package com.safepay.reward_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safepay.reward_service.entity.Reward;
import com.safepay.reward_service.service.RewardServiceImpl;

@RestController
@RequestMapping("/api/rewards/")
public class RewardController {
    
    @Autowired
    private RewardServiceImpl rewardServiceImpl;

     @GetMapping
    public List<Reward> getAllRewards() {
        return rewardServiceImpl.getAllReward();
    }

    @GetMapping("/user/{userId}")
    public List<Reward> getRewardsByUserId(@PathVariable String userId) {
        return rewardServiceImpl.getRewardsByUserId(userId);
    }
}

package com.safepay.notification_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.safepay.notification_service.entity.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification,String>{
    List<Notification> findByUserId(String userId);
}

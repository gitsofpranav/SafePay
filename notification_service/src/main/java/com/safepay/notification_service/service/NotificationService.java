package com.safepay.notification_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safepay.notification_service.entity.Notification;
import com.safepay.notification_service.repository.NotificationRepository;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification sendNotification(Notification notification){
          notification.setSentAt(LocalDateTime.now());
          return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationByuserId(String id){
        return notificationRepository.findByUserId(id);
    }

}

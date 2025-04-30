package com.twd.BfiTradingApplication.repository;

import com.twd.BfiTradingApplication.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdAndIsReadFalse(Integer userId);
    List<Notification> findByUserId(Integer userId);
    void deleteByIdAndUserId(Integer id, Integer userId);
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);


    List<Notification> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Integer userId, LocalDateTime start, LocalDateTime end);
}
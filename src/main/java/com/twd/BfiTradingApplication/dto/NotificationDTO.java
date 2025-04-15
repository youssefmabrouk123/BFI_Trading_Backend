package com.twd.BfiTradingApplication.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Integer id;
    private Integer userId;
    private String type;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;

}
package com.gtel.srpingtutorial.model.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OTPData {
    private String otp;
    private int attemptCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean verified;
    private int dailyCount;
    private LocalDateTime lastSentAt;
}

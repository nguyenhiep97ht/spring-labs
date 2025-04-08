package com.gtel.srpingtutorial.config;

import com.gtel.srpingtutorial.service.OTPService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerConfig {
    private final OTPService otpService;

    public SchedulerConfig(OTPService otpService) {
        this.otpService = otpService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 0h
    public void resetDailyOTPCounts() {
        otpService.resetDailyCounts();
    }
}

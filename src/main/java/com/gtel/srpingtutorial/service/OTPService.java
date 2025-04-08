package com.gtel.srpingtutorial.service;
import com.gtel.srpingtutorial.model.data.OTPData;
import org.redisson.api.RFuture;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {
    private final RedissonClient redissonClient;

    @Value("${otp.expiration-time}")
    private int otpExpirationTime;

    @Value("${otp.resend-wait-time}")
    private int resendWaitTime;

    @Value("${otp.max-attempts}")
    private int maxAttempts;

    @Value("${otp.max-daily}")
    private int maxDaily;

    public OTPService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public String generateOTP(String phoneNumber) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        RMapCache<String, OTPData> otpCache = redissonClient.getMapCache("otpCache");
        OTPData otpData = otpCache.get(normalizedPhone);

        if (otpData != null && otpData.getLastSentAt() != null) {
            if (otpData.getLastSentAt().plusSeconds(resendWaitTime).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Please wait before requesting a new OTP");
            }
        }

        if (otpData != null && otpData.getDailyCount() >= maxDaily) {
            throw new RuntimeException("Maximum OTP requests reached for today");
        }

        String otp = String.format("%06d", (int) (Math.random() * 1000000));

        OTPData newOtpData = new OTPData();
        newOtpData.setOtp(otp);
        newOtpData.setAttemptCount(0);
        newOtpData.setCreatedAt(LocalDateTime.now());
        newOtpData.setExpiresAt(LocalDateTime.now().plusSeconds(otpExpirationTime));
        newOtpData.setVerified(false);

        if (otpData != null) {
            newOtpData.setDailyCount(otpData.getDailyCount() + 1);
        } else {
            newOtpData.setDailyCount(1);
        }

        newOtpData.setLastSentAt(LocalDateTime.now());

        otpCache.put(normalizedPhone, newOtpData, otpExpirationTime, TimeUnit.SECONDS);

        // TODO: Implement actual SMS sending here
        System.out.println("Sending OTP " + otp + " to " + normalizedPhone);

        return otp;
    }

    public boolean verifyOTP(String phoneNumber, String otp) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        RMapCache<String, OTPData> otpCache = redissonClient.getMapCache("otpCache");
        OTPData otpData = otpCache.get(normalizedPhone);

        if (otpData == null) {
            throw new RuntimeException("OTP khong tim thay hoac het han");
        }

        if (otpData.isVerified()) {
            throw new RuntimeException("OTP da duoc su dung");
        }

        if (otpData.getAttemptCount() >= maxAttempts) {
            otpCache.remove(normalizedPhone);
            throw new RuntimeException("Qua so lan qui dinh, vui long tao moi otp");
        }

        if (otpData.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpCache.remove(normalizedPhone);
            throw new RuntimeException("OTP het han");
        }

        if (!otpData.getOtp().equals(otp)) {
            otpData.setAttemptCount(otpData.getAttemptCount() + 1);
            otpCache.put(normalizedPhone, otpData);
            throw new RuntimeException("OTP khong hop le");
        }

        otpData.setVerified(true);
        otpCache.put(normalizedPhone, otpData);

        return true;
    }

    public void updatePassword(String phoneNumber, String password) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        RMapCache<String, OTPData> otpCache = redissonClient.getMapCache("otpCache");
        OTPData otpData = otpCache.get(normalizedPhone);

        if (otpData == null || !otpData.isVerified()) {
            throw new RuntimeException("OTP not verified");
        }

        System.out.println("Updating password for " + normalizedPhone);

        otpCache.remove(normalizedPhone);
    }

    private String normalizePhoneNumber(String phoneNumber) {
        String digits = phoneNumber.replaceAll("[^0-9]", "");

        if (digits.startsWith("0")) {
            return "84" + digits.substring(1);
        } else if (digits.startsWith("84")) {
            return digits;
        } else if (digits.startsWith("+84")) {
            return digits.substring(1);
        } else {
            return "84" + digits;
        }
    }

    public void resetDailyCounts() {
        RMapCache<String, OTPData> otpCache = redissonClient.getMapCache("otpCache");

        
        RFuture<Iterator<String>> keysFuture = (RFuture<Iterator<String>>) otpCache.keySet().iterator();
        keysFuture.whenComplete((iterator, exception) -> {
            if (exception != null) {
                return;
            }

            while (iterator.hasNext()) {
                String key = iterator.next();
                OTPData otpData = otpCache.get(key);
                if (otpData != null) {
                    otpData.setDailyCount(0);
                    otpCache.put(key, otpData); 
                }
            }
        });
    }
}

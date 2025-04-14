package com.gtel.srpingtutorial.service;
import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.data.OTPData;
import com.gtel.srpingtutorial.utils.ERROR_CODE;
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
    public static int otpExpirationTime;

    @Value("${otp.resend-wait-time}")
    public static int resendWaitTime;

    @Value("${otp.max-attempts}")
    public static int maxAttempts;

    @Value("${otp.max-daily}")
    public static int maxDaily;

    public OTPService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public String generateOTP(String phoneNumber) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        RMapCache<String, OTPData> otpCache = redissonClient.getMapCache("otpCache");
        OTPData otpData = otpCache.get(normalizedPhone);

        if (otpData != null && otpData.getLastSentAt() != null) {
            if (otpData.getLastSentAt().plusSeconds(resendWaitTime).isAfter(LocalDateTime.now())) {
                throw new ApplicationException(ERROR_CODE.INVALID_REQUEST,"vui long cho cung cap OTP");
            }
        }

        if (otpData != null && otpData.getDailyCount() >= maxDaily) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST,"Dat so luong toi da otp trong ngay");
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

        // gui otp qua system out, update email tai day
        System.out.println("gui OTP " + otp + " den " + normalizedPhone);

        return otp;
    }

    public boolean verifyOTP(String phoneNumber, String otp) {
        String normalizedPhone = normalizePhoneNumber(phoneNumber);

        RMapCache<String, OTPData> otpCache = redissonClient.getMapCache("otpCache");
        OTPData otpData = otpCache.get(normalizedPhone);

        if (otpData == null) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP khong tim thay hoac het han");
        }

        if (otpData.isVerified()) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP da duoc su dung");
        }

        if (otpData.getAttemptCount() >= maxAttempts) {
            otpCache.remove(normalizedPhone);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST,"Qua so lan qui dinh, vui long tao moi otp");
        }

        if (otpData.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpCache.remove(normalizedPhone);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST,"OTP het han");
        }

        if (!otpData.getOtp().equals(otp)) {
            otpData.setAttemptCount(otpData.getAttemptCount() + 1);
            otpCache.put(normalizedPhone, otpData);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP khong hop le");
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
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST,"OTP chua xac thuc");
        }

        System.out.println("cap nhat mat khau thanh cong " + normalizedPhone);

        otpCache.remove(normalizedPhone);
    }

    public String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST,"So dien thoai khong hop le");
        }
        String digits = phoneNumber.replaceAll("[^0-9]", "");
        // Kiểm tra độ dài tối thiểu/tối đa
        if (digits.length() < 9 || digits.length() > 15) {
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST,"So dien thoai phai tu 9 den 15 chu so");
        }

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

package com.gtel.srpingtutorial;

import com.gtel.srpingtutorial.model.data.OTPData;
import com.gtel.srpingtutorial.service.OTPService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class OTPServiceTest {
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RMapCache<String, OTPData> otpCache;

    private OTPService otpService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        otpService = new OTPService(redissonClient);
        OTPService.otpExpirationTime = 60; // 60 seconds
        OTPService.resendWaitTime = 30; // 30 seconds
        OTPService.maxAttempts = 3;
        OTPService.maxDaily = 5;

        Mockito.when(redissonClient.<String, OTPData>getMapCache("otpCache")).thenReturn(otpCache);
    }

    // ---------------------- Test cases cho normalizePhoneNumber ----------------------
    @Test
    public void testNormalizePhoneNumber_StartWith0() {
        Assertions.assertEquals("84123456789", otpService.normalizePhoneNumber("0123456789"));
    }

    @Test
    public void testNormalizePhoneNumber_StartWith84() {
        Assertions.assertEquals("84123456789", otpService.normalizePhoneNumber("84123456789"));
    }

    @Test
    public void testNormalizePhoneNumber_StartWithPlus84() {
        Assertions.assertEquals("84123456789", otpService.normalizePhoneNumber("+84123456789"));
    }

    @Test
    public void testNormalizePhoneNumber_WithSpecialChars() {
        Assertions.assertEquals("84123456789", otpService.normalizePhoneNumber("+84-123-456-789"));
    }

    @Test
    public void testNormalizePhoneNumber_NoCountryCode() {
        Assertions.assertEquals("84123456789", otpService.normalizePhoneNumber("123456789"));
    }

    // ---------------------- Test cases cho generateOTP ----------------------
    @Test
    public void testGenerateOTP_FirstTime() {
        Mockito.when(otpCache.get("84123456789")).thenReturn(null);
        String otp = otpService.generateOTP("0123456789");
        Assertions.assertNotNull(otp);
        Mockito.verify(otpCache).put(ArgumentMatchers.eq("84123456789"), ArgumentMatchers.any(OTPData.class), ArgumentMatchers.eq(60L), ArgumentMatchers.eq(TimeUnit.SECONDS));
    }

    @Test
    public void testGenerateOTP_ResendBeforeWaitTime() {
        OTPData existingOtpData = new OTPData();
        existingOtpData.setLastSentAt(LocalDateTime.now().minusSeconds(10)); // 10s trước (resendWaitTime = 30s)
        Mockito.when(otpCache.get("84123456789")).thenReturn(existingOtpData);

        Assertions.assertThrows(RuntimeException.class, () -> otpService.generateOTP("0123456789"));
    }

    @Test
    public void testGenerateOTP_ExceedMaxDaily() {
        OTPData existingOtpData = new OTPData();
        existingOtpData.setDailyCount(5); // maxDaily = 5
        Mockito.when(otpCache.get("84123456789")).thenReturn(existingOtpData);

        Assertions.assertThrows(RuntimeException.class, () -> otpService.generateOTP("0123456789"));
    }

    // ---------------------- Test cases cho verifyOTP ----------------------
    @Test
    public void testVerifyOTP_Success() {
        OTPData otpData = new OTPData();
        otpData.setOtp("123456");
        otpData.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        Mockito.when(otpCache.get("84123456789")).thenReturn(otpData);

        Assertions.assertTrue(otpService.verifyOTP("0123456789", "123456"));
        Assertions.assertTrue(otpData.isVerified());
    }

    @Test
    public void testVerifyOTP_InvalidOTP() {
        OTPData otpData = new OTPData();
        otpData.setOtp("123456");
        otpData.setAttemptCount(0);
        otpData.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        Mockito.when(otpCache.get("84123456789")).thenReturn(otpData);

        Assertions.assertThrows(RuntimeException.class, () -> otpService.verifyOTP("0123456789", "000000"));
        Assertions.assertEquals(1, otpData.getAttemptCount());
    }

    @Test
    public void testVerifyOTP_ExceedMaxAttempts() {
        OTPData otpData = new OTPData();
        otpData.setOtp("123456");
        otpData.setAttemptCount(3); // maxAttempts = 3
        otpData.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        Mockito.when(otpCache.get("84123456789")).thenReturn(otpData);

        Assertions.assertThrows(RuntimeException.class, () -> otpService.verifyOTP("0123456789", "000000"));
        Mockito.verify(otpCache).remove("84123456789");
    }

    // ---------------------- Test cases cho updatePassword ----------------------
    @Test
    public void testUpdatePassword_Success() {
        OTPData otpData = new OTPData();
        otpData.setVerified(true);
        Mockito.when(otpCache.get("84123456789")).thenReturn(otpData);

        otpService.updatePassword("0123456789", "newPassword");
        Mockito.verify(otpCache).remove("84123456789");
    }

    @Test
    public void testUpdatePassword_NotVerified() {
        OTPData otpData = new OTPData();
        otpData.setVerified(false);
        Mockito.when(otpCache.get("84123456789")).thenReturn(otpData);

        Assertions.assertThrows(RuntimeException.class, () -> otpService.updatePassword("0123456789", "newPassword"));
    }
}




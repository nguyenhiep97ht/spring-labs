package com.gtel.srpingtutorial;

import com.gtel.srpingtutorial.model.data.OTPData;
import com.gtel.srpingtutorial.service.OTPService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OTPServiceExceptionTest {
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RMapCache<String, OTPData> otpCache;
    @InjectMocks
    private OTPService otpService;

    // ---------------------- Test cases cho normalizePhoneNumber (ngoại lệ) ----------------------
    @Test
    public void testNormalizePhoneNumber_EmptyString() {
        Assertions.assertThrows(RuntimeException.class, () -> otpService.normalizePhoneNumber(""));
    }

    @Test
    public void testNormalizePhoneNumber_InvalidCharacters() {
        Assertions.assertThrows(RuntimeException.class, () -> otpService.normalizePhoneNumber("abc"));
    }

    @Test
    public void testNormalizePhoneNumber_TooShort() {
        Assertions.assertThrows(RuntimeException.class, () -> otpService.normalizePhoneNumber("0123"));
    }

    @Test
    public void testNormalizePhoneNumber_TooLong() {
        Assertions.assertThrows(RuntimeException.class, () -> otpService.normalizePhoneNumber("0123456789123456"));
    }

    // ---------------------- Test cases cho generateOTP (ngoại lệ) ----------------------
    @Test
    public void testGenerateOTP_InvalidPhoneNumber() {
        Assertions.assertThrows(RuntimeException.class, () -> otpService.generateOTP("abc"));
    }

    // ---------------------- Test cases cho verifyOTP (ngoại lệ) ----------------------
    @Test
    public void testVerifyOTP_PhoneNumberNotInCache() {
        Mockito.when(otpCache.get("84123456789")).thenReturn(null);
        Assertions.assertThrows(RuntimeException.class, () -> otpService.verifyOTP("0123456789", "123456"));
    }
}

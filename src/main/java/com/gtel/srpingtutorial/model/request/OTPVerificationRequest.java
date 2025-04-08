package com.gtel.srpingtutorial.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OTPVerificationRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 6, message = "OTP 6 ki tu")
    private String otp;
}

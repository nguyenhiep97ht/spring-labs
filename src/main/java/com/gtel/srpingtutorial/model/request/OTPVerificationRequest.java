package com.gtel.srpingtutorial.model.request;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class OTPVerificationRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 6, message = "OTP 6 ki tu")
    private String otp;
}

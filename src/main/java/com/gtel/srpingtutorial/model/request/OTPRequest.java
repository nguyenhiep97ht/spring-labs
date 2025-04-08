package com.gtel.srpingtutorial.model.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OTPRequest {
    @Pattern(regexp = "(\\+84|0|84)([0-9]{9,10})", message = "Invalid phone number format")
    private String phoneNumber;
}

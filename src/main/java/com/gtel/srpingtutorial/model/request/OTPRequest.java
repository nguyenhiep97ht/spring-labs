package com.gtel.srpingtutorial.model.request;


import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class OTPRequest {
    @Pattern(regexp = "(\\+84|0|84)([0-9]{9,10})", message = "Invalid phone number format")
    private String phoneNumber;
}

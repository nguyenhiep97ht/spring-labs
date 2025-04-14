package com.gtel.srpingtutorial.model.request;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class PasswordUpdateRequest {
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Mật khẩu ít nhất 8 chữ số, bắt buộc phải có chữ và ít nhất 1 số, 1 kí tự đặc biệt, một chữ viết hoa")
    private String password;

    @Pattern(regexp = "(\\+84|0|84)([0-9]{9,10})", message = "Invalid phone number format")
    private String phoneNumber;
}

package com.gtel.srpingtutorial.api;

import com.gtel.srpingtutorial.model.request.OTPRequest;
import com.gtel.srpingtutorial.model.request.OTPVerificationRequest;
import com.gtel.srpingtutorial.model.request.PasswordUpdateRequest;
import com.gtel.srpingtutorial.service.OTPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final OTPService otpService;

    public AuthController(OTPService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOTP(@RequestBody OTPRequest request) {
        try {
            otpService.generateOTP(request.getPhoneNumber());
            return ResponseEntity.ok().body("OTP gui thanh cong");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody OTPVerificationRequest request) {
        try {
            otpService.verifyOTP(request.getPhoneNumber(), request.getOtp());
            return ResponseEntity.ok().body("Xac thuc otp thanh cong");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody PasswordUpdateRequest request) {
        try {

            otpService.updatePassword(request.getPhoneNumber(), request.getPassword());
            return ResponseEntity.ok().body("Dang ky thanh cong");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

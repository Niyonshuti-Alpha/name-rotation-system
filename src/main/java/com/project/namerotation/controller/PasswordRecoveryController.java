package com.project.namerotation.controller;

import com.project.namerotation.service.PasswordRecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class PasswordRecoveryController {

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createResponse("error", "Email is required"));
        }

        String message = passwordRecoveryService.initiatePasswordRecovery(email);
        
        return ResponseEntity.ok(createResponse("success", message));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(createResponse("error", "Email and code are required"));
        }

        boolean isValid = passwordRecoveryService.verifyCode(email, code);
        
        if (isValid) {
            return ResponseEntity.ok(createResponse("success", "Code verified successfully"));
        } else {
            return ResponseEntity.ok(createResponse("error", "Invalid or expired verification code"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");
        
        if (email == null || code == null || newPassword == null) {
            return ResponseEntity.badRequest().body(createResponse("error", "All fields are required"));
        }

        boolean success = passwordRecoveryService.resetPassword(email, code, newPassword);
        
        if (success) {
            return ResponseEntity.ok(createResponse("success", "Password reset successfully"));
        } else {
            return ResponseEntity.ok(createResponse("error", "Failed to reset password"));
        }
    }

    private Map<String, Object> createResponse(String status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        return response;
    }
}
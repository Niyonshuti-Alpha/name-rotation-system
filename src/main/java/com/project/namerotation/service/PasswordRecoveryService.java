package com.project.namerotation.service;

import com.project.namerotationsystem.model.User;
import com.project.namerotationsystem.model.VerificationCode;
import com.project.namerotation.repository.UserRepository;
import com.project.namerotation.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordRecoveryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public String initiatePasswordRecovery(String email) {
        try {
            System.out.println("🔄 Starting password recovery for: " + email);
            
            // Check if user exists with this exact email
            Optional<User> user = userRepository.findByEmail(email);
            
            if (user.isPresent()) {
                System.out.println("✅ User found, generating verification code...");
                
                // Generate 6-digit code
                String code = generateVerificationCode();
                
                // Delete any existing codes for this email
                verificationCodeRepository.deleteByEmail(email);
                System.out.println("✅ Deleted existing codes for email");
                
                // Create new verification code (15 minutes expiration)
                LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
                VerificationCode verificationCode = new VerificationCode(email, code, expiresAt);
                
                // Save the verification code
                VerificationCode savedCode = verificationCodeRepository.save(verificationCode);
                System.out.println("✅ Verification code saved with ID: " + savedCode.getId());
                
                // Send email with verification code
                sendVerificationEmail(email, code);
                
                System.out.println("✅ Verification code sent to: " + email);
                
                return "If the email exists in our system, a verification code has been sent";
            } else {
                System.out.println("❌ User not found for email: " + email);
                // Still return success message for security (don't reveal if email exists)
                return "If the email exists in our system, a verification code has been sent";
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR in initiatePasswordRecovery: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Password recovery failed: " + e.getMessage(), e);
        }
    }

    private void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Password Recovery Verification Code");
            message.setText(
                "Dear User,\n\n" +
                "You have requested to reset your password for the Name Rotation System.\n\n" +
                "Your verification code is: " + verificationCode + "\n\n" +
                "This code will expire in 15 minutes.\n\n" +
                "If you did not request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Name Rotation System Team"
            );
            
            mailSender.send(message);
            System.out.println("📧 Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to: " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    // ... keep your existing verifyCode() and resetPassword() methods unchanged ...
    @Transactional
    public boolean verifyCode(String email, String code) {
        try {
            System.out.println("🔄 Verifying code for: " + email);
            Optional<VerificationCode> validCode = verificationCodeRepository
                .findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(
                    email, code, LocalDateTime.now()
                );
            
            if (validCode.isPresent()) {
                // Mark code as used
                VerificationCode vc = validCode.get();
                vc.setUsed(true);
                verificationCodeRepository.save(vc);
                System.out.println("✅ Code verified successfully");
                return true;
            }
            
            System.out.println("❌ Invalid or expired code");
            return false;
        } catch (Exception e) {
            System.err.println("❌ ERROR in verifyCode: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean resetPassword(String email, String code, String newPassword) {
        try {
            System.out.println("🔄 Resetting password for: " + email);
            // Verify the code was used for this email
            Optional<VerificationCode> validCode = verificationCodeRepository
                .findByEmailAndCodeAndUsedTrue(email, code);
            
            if (validCode.isPresent() && LocalDateTime.now().isBefore(validCode.get().getExpiresAt())) {
                // Find user and update password
                Optional<User> user = userRepository.findByEmail(email);
                if (user.isPresent()) {
                    User u = user.get();
                    u.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(u);
                    
                    // Delete used codes
                    verificationCodeRepository.deleteByEmail(email);
                    System.out.println("✅ Password reset successfully");
                    return true;
                }
            }
            
            System.out.println("❌ Password reset failed - invalid code or user not found");
            return false;
        } catch (Exception e) {
            System.err.println("❌ ERROR in resetPassword: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
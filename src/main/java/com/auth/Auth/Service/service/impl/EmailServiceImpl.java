package com.auth.Auth.Service.service.impl;

import com.auth.Auth.Service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Verify Your Email - TODO App");
            
            String verificationUrl = frontendUrl + "/verify-email?token=" + token;
            String emailBody = String.format("""
                Hello!
                
                Thank you for registering with TODO App. Please verify your email address by clicking the link below:
                
                %s
                
                This link will expire in 24 hours.
                
                If you didn't create an account, please ignore this email.
                
                Best regards,
                TODO App Team
                """, verificationUrl);
            
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("Verification email sent successfully to: " + to);
            System.out.println("Verification URL: " + verificationUrl);
        } catch (Exception e) {
            System.err.println("Failed to send verification email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Reset Your Password - TODO App");
            
            String resetUrl = frontendUrl + "/reset-password?token=" + token;
            String emailBody = String.format("""
                Hello!
                
                You requested to reset your password. Click the link below to reset it:
                
                %s
                
                This link will expire in 1 hour.
                
                If you didn't request this, please ignore this email.
                
                Best regards,
                TODO App Team
                """, resetUrl);
            
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("Password reset email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to TODO App!");
            
            String emailBody = String.format("""
                Hello %s!
                
                Welcome to TODO App! Your account has been successfully verified and activated.
                
                You can now log in and start organizing your tasks.
                
                Best regards,
                TODO App Team
                """, username);
            
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("Welcome email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to " + to + ": " + e.getMessage());
            // Don't throw exception for welcome email as it's not critical
        }
    }
}

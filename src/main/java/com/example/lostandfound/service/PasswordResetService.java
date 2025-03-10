package com.example.lostandfound.service;

import ch.qos.logback.core.encoder.Encoder;
import com.example.lostandfound.entity.PasswordResetToken;
import com.example.lostandfound.entity.User;
import com.example.lostandfound.repository.PasswordResetTokenRepository;
import com.example.lostandfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Method to send password reset link to the user's email
    public void sendPasswordResetLink(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            // Generate a password reset token
            String token = UUID.randomUUID().toString();

            // Create the password reset token entity and associate it with the user
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);  // Associate the token with the user
            resetToken.setCreatedAt(LocalDateTime.now());
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));  // Token expires in 1 hour
            tokenRepository.save(resetToken);

            // Send the reset email with the token link
            String resetUrl = "http://localhost:8080/reset-password?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset Request");
            message.setText("Click the link to reset your password: " + resetUrl);
            mailSender.send(message);
        } else {
            // Optionally handle the case where the email does not exist
            throw new RuntimeException("No user found with the given email.");
        }
    }

    // Validate the token to check if it exists and is within its expiry time
    public boolean validateToken(String token) {
        PasswordResetToken tokenRecord = tokenRepository.findByToken(token);
        return tokenRecord != null && tokenRecord.getExpiryDate().isAfter(LocalDateTime.now());  // Check if the token is still valid
    }

    // Reset the user's password
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken tokenRecord = tokenRepository.findByToken(token);
        if (tokenRecord != null && validateToken(token)) {
            User user = tokenRecord.getUser();  // Get the associated user
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
            tokenRepository.delete(tokenRecord);  // Invalidate the token after password reset
        } else {
            throw new RuntimeException("Invalid or expired token.");
        }
    }
}

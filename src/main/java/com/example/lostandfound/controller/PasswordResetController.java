package com.example.lostandfound.controller;

import com.example.lostandfound.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    // Show Forgot Password Form
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "/user/forgot-password";
    }

    // Handle Forgot Password Form Submission
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        passwordResetService.sendPasswordResetLink(email);
        model.addAttribute("message", "If an account with that email exists, we have sent a reset link to your inbox.");
        return "/user/forgot-password";
    }

    // Show Reset Password Form
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!passwordResetService.validateToken(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "error";
        }
        model.addAttribute("token", token);
        return "/user/reset-password";
    }

    // Handle Reset Password Form Submission
    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "/user/reset-password";
        }
        passwordResetService.resetPassword(token, password);
        model.addAttribute("message", "Password reset successfully. You can now login.");
        return "/user/login";
    }
}

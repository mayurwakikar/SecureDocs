package com.example.SecureDocs.controller;

import com.example.SecureDocs.model.PasswordResetToken;
import com.example.SecureDocs.model.User;
import com.example.SecureDocs.repository.DocumentRepository;
import com.example.SecureDocs.repository.PasswordResetTokenRepository;
import com.example.SecureDocs.repository.UserRepository;
import com.example.SecureDocs.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String userEmail, Model model) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

            PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
            tokenRepository.save(resetToken);

            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            String emailBody = "To reset your password, click the link below:\n" + resetLink;

            emailService.sendEmail(user.getEmail(), "Password Reset Request", emailBody);
            model.addAttribute("message", "A password reset link has been sent to your email.");
        } else {
            model.addAttribute("error", "No user found with that email address.");
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isPresent() && tokenOptional.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "login";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isPresent() && tokenOptional.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            PasswordResetToken resetToken = tokenOptional.get();
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            tokenRepository.delete(resetToken);
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "login";
        }
    }

    @GetMapping("/forgot-password-security")
    public String showForgotPasswordSecurityPage() {
        return "forgot-password-security";
    }

    @PostMapping("/forgot-password-security")
    public String processForgotPasswordSecurity(@RequestParam("email") String userEmail, @RequestParam("answer") String answer, Model model) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getSecurityAnswer().equalsIgnoreCase(answer)) {
                model.addAttribute("user", user);
                return "reset-password-security";
            } else {
                model.addAttribute("error", "Incorrect answer. Please try again.");
                return "forgot-password-security";
            }
        } else {
            model.addAttribute("error", "No user found with that email.");
            return "forgot-password-security";
        }
    }

    @PostMapping("/reset-password-security")
    public String processResetPasswordSecurity(@RequestParam("userId") Long userId, @RequestParam("newPassword") String newPassword, Model model) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return "redirect:/login";
        } else {
            model.addAttribute("error", "User not found. Please try the process again.");
            return "forgot-password-security";
        }
    }
}
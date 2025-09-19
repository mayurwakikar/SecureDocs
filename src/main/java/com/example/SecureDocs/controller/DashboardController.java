package com.example.SecureDocs.controller;

import com.example.SecureDocs.model.User;
import com.example.SecureDocs.repository.DocumentRepository;
import com.example.SecureDocs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDateTime;
import java.util.Collections;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        model.addAttribute("documents", Collections.emptyList());

        User user = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            model.addAttribute("username", user.getUsername());
            model.addAttribute("documents", documentRepository.findByUser(user));
        }

        return "dashboard";
    }
}
package com.example.SecureDocs.service;



import com.example.SecureDocs.model.User;
import com.example.SecureDocs.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");

        // Check if the user already exists in the database
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    // User exists, update details if necessary
                    user.setUsername(username);
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                },
                () -> {
                    // User does not exist, create a new one
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setRole("USER");
                    newUser.setProvider("google");
                    newUser.setRegistrationDate(LocalDateTime.now());
                    newUser.setLastLogin(LocalDateTime.now());
                    userRepository.save(newUser);
                }
        );

        return oAuth2User;
    }
}
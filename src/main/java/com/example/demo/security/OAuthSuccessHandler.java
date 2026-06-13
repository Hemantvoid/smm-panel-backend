package com.example.demo.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthSuccessHandler
implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepo;

    @Override
    public void onAuthenticationSuccess(

            HttpServletRequest request,

            HttpServletResponse response,

            Authentication authentication

    ) throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User)
                authentication.getPrincipal();

        String email =
                oauthUser.getAttribute(
                        "email"
                );

        User user = userRepo
                .findByEmail(email)
                .orElseGet(() -> {

                    User u = new User();

                    u.setEmail(email);

                    u.setUsername(email);

                    u.setProvider("google");

                    u.setRole("ROLE_USER");

                    u.setBalance(0);

                    return userRepo.save(u);
                });

        String token =
                JwtUtil.generateToken(
                        user.getUsername(),
                        user.getRole()
                );

        response.sendRedirect(

                "http://localhost:5173/oauth-success?token="

                        + token +

                        "&role=" +

                        user.getRole()
        );
    }
}
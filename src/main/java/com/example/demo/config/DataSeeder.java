package com.example.demo.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            // =====================================
            // ADMIN
            // =====================================
            if (
                userRepository
                    .findByUsername("admin")
                    .isEmpty()
            ) {

                User admin = new User();

                admin.setUsername("admin");

                admin.setPassword(
                    passwordEncoder.encode(
                        "admin123"
                    )
                );

                admin.setRole(
                    "ROLE_ADMIN"
                );

                admin.setBalance(100000);

                userRepository.save(admin);

                System.out.println(
                    "✅ Admin user created"
                );
            }

            // =====================================
            // NORMAL USER
            // =====================================
            if (
                userRepository
                    .findByUsername("user")
                    .isEmpty()
            ) {

                User user = new User();

                user.setUsername("user");

                user.setPassword(
                    passwordEncoder.encode(
                        "user123"
                    )
                );

                user.setRole(
                    "ROLE_USER"
                );

                user.setBalance(5000);

                userRepository.save(user);

                System.out.println(
                    "✅ Test user created"
                );
            }

        };
    }
}
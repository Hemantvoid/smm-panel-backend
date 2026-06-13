package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.JwtFilter;
import com.example.demo.security.OAuthSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    
    @Autowired
    private OAuthSuccessHandler
    oauthSuccessHandler;
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder()	{
    	return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // 🔓 PUBLIC
            		.requestMatchers(
            			    "/auth/login",
            			    "/auth/register",
            			    "/auth/test",
            			    "/services/public",
            			    "/admin/settings/public",
            			    "/auth/forgot-password",
            			    "/auth/verify-otp",
            			    "/auth/reset-password",
            			    "/swagger-ui/**",
            		        "/v3/api-docs/**",
            		        "/swagger-ui.html"
            			).permitAll()

                // 🔒 ADMIN ONLY
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 👤 USER + ADMIN
                .requestMatchers("/orders/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/wallet/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/transactions/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/support/**").hasAnyRole("USER", "ADMIN")

                // बाकी सब
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());

        return http.oauth2Login(oauth ->

        	    oauth.successHandler(
        	        oauthSuccessHandler
        	    )

        	).build();
    }
    
}
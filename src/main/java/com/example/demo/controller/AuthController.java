package com.example.demo.controller;
import java.util.HashMap;
import java.util.Map;
import com.example.demo.model.Provider;
import java.time.LocalDateTime;
import java.util.Random;

import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.VerifyOtpRequest;
import com.example.demo.dto.ResetPasswordRequest;

import com.example.demo.model.PasswordResetOtp;
import com.example.demo.repository.PasswordResetOtpRepository;
import com.example.demo.repository.ProviderRepository;
import com.example.demo.service.EmailService;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.demo.dto.LoginRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;
@Tag(
	    name = "Authentication",
	    description = "Login, Registration, Password Reset and User APIs"
	)
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private PasswordResetOtpRepository otpRepo;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ProviderRepository providerRepository;

//	@Autowired
//	private BCryptPasswordEncoder encoder;
//	
	@PostMapping("/test")
	public String test() {
	    return passwordEncoder.encode("1234");
	}
	
    @Autowired
    private UserRepository userRepo;
    
    @Operation(
    	    summary = "Register User",
    	    description = "Create a new user account"
    	)
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("ROLE_USER");

        userRepo.save(user);

        return "User registered";
    }
    
    @Operation(
    	    summary = "Login User",
    	    description = "Authenticate user and return JWT token"
    	)
    @PostMapping("/login")
    public Map<String, String> login(

            @RequestBody
            LoginRequest req

    ) {

        System.out.println(
                "USERNAME => "
                + req.getUsername()
        );
        User dbUser = userRepo
                .findByUsername(
                        req.getUsername()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        boolean matched =
                passwordEncoder.matches(
                        req.getPassword(),
                        dbUser.getPassword()
                );

        System.out.println(
                "MATCHED => "
                + matched
        );

        if (!matched) {

            throw new RuntimeException(
                    "Invalid credentials"
            );
        }

        String token =
                JwtUtil.generateToken(
                        dbUser.getUsername(),
                        dbUser.getRole()
                );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "token",
                token
        );

        response.put(
                "role",
                dbUser.getRole()
        );

        return response;
    }
    @Operation(
    	    summary = "Current User",
    	    description = "Returns logged in user details"
    	)
    @GetMapping("/me")
    public Map<String, Object> me(
            Authentication auth
    ) {

        if (auth == null) {
            throw new RuntimeException(
                    "Unauthorized"
            );
        }

        User user = userRepo
                .findByUsername(
                        auth.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        Map<String, Object> res =
                new HashMap<>();

        res.put(
                "username",
                user.getUsername()
        );

        res.put(
                "role",
                user.getRole()
        );

        res.put(
                "balance",
                user.getBalance()
        );

        return res;
    }
    
    @PostMapping("/change-password")
    public String changePassword(

            Authentication auth,

            @RequestBody
            ChangePasswordRequest req

    ) {

        if (auth == null) {

            throw new RuntimeException(
                    "Unauthorized"
            );
        }

        User user = userRepo
                .findByUsername(
                        auth.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        if (!passwordEncoder.matches(
                req.getCurrentPassword(),
                user.getPassword()
        )) {

            throw new RuntimeException(
                    "Current password incorrect"
            );
        }

        if (req.getNewPassword()
                .length() < 6) {

            throw new RuntimeException(
                    "Password must be at least 6 characters"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        req.getNewPassword()
                )
        );

        userRepo.save(user);

        return "Password changed successfully";
    }
    @Operation(
    	    summary = "Forgot Password",
    	    description = "Send OTP to registered email"
    	)
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody
            ForgotPasswordRequest req
    ) {

        User user = userRepo
                .findByEmail(
                        req.getEmail()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email not found"
                        )
                );

        String otp =
                String.valueOf(
                        100000 +
                        new Random().nextInt(900000)
                );

        PasswordResetOtp resetOtp =
                otpRepo.findByEmail(
                        req.getEmail()
                ).orElse(
                        new PasswordResetOtp()
                );

        resetOtp.setEmail(
                req.getEmail()
        );

        resetOtp.setOtp(
                otp
        );

        resetOtp.setExpiryTime(
                LocalDateTime.now()
                        .plusMinutes(10)
        );

        otpRepo.save(
                resetOtp
        );

        emailService.sendOtp(
                req.getEmail(),
                otp
        );

        return "OTP sent successfully";
    }
    @Operation(
    	    summary = "Verify OTP",
    	    description = "Verify password reset OTP"
    	)
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestBody
            VerifyOtpRequest req
    ) {

        PasswordResetOtp otp =
                otpRepo.findByEmail(
                        req.getEmail()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"
                        )
                );

        if (!otp.getOtp().equals(
                req.getOtp()
        )) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        if (
            otp.getExpiryTime()
               .isBefore(
                   LocalDateTime.now()
               )
        ) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        return "OTP verified";
    }
    @Operation(
    	    summary = "Reset Password",
    	    description = "Reset account password"
    	)
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody
            ResetPasswordRequest req
    ) {

        PasswordResetOtp otp =
                otpRepo.findByEmail(
                        req.getEmail()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"
                        )
                );

        if (!otp.getOtp().equals(
                req.getOtp()
        )) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        if (
            otp.getExpiryTime()
               .isBefore(
                   LocalDateTime.now()
               )
        ) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        User user =
                userRepo.findByEmail(
                        req.getEmail()
                )
                .orElseThrow();

        user.setPassword(
                passwordEncoder.encode(
                        req.getNewPassword()
                )
        );

        userRepo.save(
                user
        );

        otpRepo.delete(
                otp
        );

        return "Password reset successful";
    }
    @PutMapping("/admin/providers/{id}/toggle")
    public Provider toggleProvider(
            @PathVariable Long id
    ) {

        Provider provider =
                providerRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Provider not found"
                                )
                        );

        provider.setStatus(
                !provider.isStatus()
        );

        return providerRepository.save(
                provider
        );
    }
    @PostMapping("/generate-api-key")
    public Map<String,String> generateApiKey(
            Authentication auth
    ) {

        User user = userRepo
                .findByUsername(auth.getName())
                .orElseThrow();

        String apiKey =
                UUID.randomUUID().toString()
                        .replace("-", "");

        user.setApiKey(apiKey);

        userRepo.save(user);

        return Map.of(
                "apiKey",
                apiKey
        );
    }
    
    @GetMapping("/api-key")
    public Map<String,String> getApiKey(
            Authentication auth
    ) {

        User user = userRepo
                .findByUsername(auth.getName())
                .orElseThrow();

        return Map.of(
                "apiKey",
                user.getApiKey()
        );
    }
}
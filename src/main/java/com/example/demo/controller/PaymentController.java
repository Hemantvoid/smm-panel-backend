package com.example.demo.controller;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public String createOrder(@RequestParam double amount) throws Exception {
        return paymentService.createOrder(amount);
    }

    // 🔥 THIS WAS MISSING
    @PostMapping("/verify")
    public String verifyPayment(@RequestBody PaymentRequest request,
                                Authentication auth) throws Exception {

        String username = auth.getName();

        return paymentService.verifyPayment(request, username);
    }
}
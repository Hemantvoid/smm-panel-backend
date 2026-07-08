package com.example.demo.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.service.PaymentService;

@RestController
@RequestMapping("/payment")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public Map<String, Object> createOrder(@RequestParam Double amount,
                                           Principal principal) {

        return paymentService.createOrder(amount, principal.getName());

    }

    @PostMapping("/verify")
    public String verify(@RequestBody PaymentRequest request,
                         Principal principal) {

        return paymentService.verifyPayment(
                request.getClientTxnId(),
                principal.getName());

    }

}

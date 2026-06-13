package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.OrderEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.service.OrderService;

@CrossOrigin(origins = "http://localhost:5173") // ⚠️ change in production
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    // 🔥 PLACE ORDER
    @PostMapping
    public OrderResponse placeOrder(@RequestBody OrderRequest request) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        String username = auth.getName();

        OrderEntity order = service.placeOrder(request, username);
        return OrderMapper.toResponse(order);
    }

    // 🔥 GET ORDERS (ADMIN / USER)
    @GetMapping
    public Page<OrderResponse> getOrders(Pageable pageable) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        String username = auth.getName();

        String role = auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");

        return service.getOrders(username, role, pageable);
    }
}
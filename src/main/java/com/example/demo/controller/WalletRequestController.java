package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.WalletRequest;
import com.example.demo.repository.WalletRequestRepository;

@RestController
@RequestMapping("/wallet/request")
@CrossOrigin("*")
public class WalletRequestController {

    @Autowired
    private WalletRequestRepository repo;

    @PostMapping
    public WalletRequest create(
            @RequestBody WalletRequest request,
            Authentication auth
    ) {

        if(repo.existsByUtr(request.getUtr())) {
            throw new RuntimeException("UTR already used");
        }

        request.setUsername(auth.getName());
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());

        return repo.save(request);
    }

    @GetMapping("/my")
    public List<WalletRequest> myRequests(
            Authentication auth
    ) {

        return repo.findByUsername(
                auth.getName()
        );
    }
}
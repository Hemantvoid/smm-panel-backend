package com.example.demo.controller;

import com.example.demo.model.Transaction;
import org.springframework.security.core.Authentication;
import com.example.demo.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository repo;

    @GetMapping
    public List<Transaction> getAll() {
        return repo.findAll();
    }
    
    @GetMapping("/user")
    public List<Transaction> getUserTransactions(
            Authentication auth
    ) {

        String username =
                auth.getName();

        return repo.findByUsername(
                username
        );
    }
}
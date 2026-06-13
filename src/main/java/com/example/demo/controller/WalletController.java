package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TransactionRepository transactionRepo;
    

    // 🔥 ADD MONEY
    @PostMapping("/add")
    public String addBalance(@RequestParam double amount) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBalance(user.getBalance() + amount);
        userRepo.save(user);

        Transaction txn = new Transaction();
        txn.setUsername(username);
        txn.setAmount(amount);
        txn.setType("CREDIT");
        txn.setStatus("SUCCESS");
        txn.setDescription("Balance added");

        transactionRepo.save(txn);

        return "Balance added successfully";
    }
    @GetMapping("/balance")
    public double getBalance() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        System.out.println("USER: " + username); // debug

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getBalance();
    }
    @GetMapping("/user")
    public List<Transaction> getMyTransactions() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return transactionRepo.findByUsername(username);
    }
}

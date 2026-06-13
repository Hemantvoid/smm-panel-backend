package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.model.WalletRequest;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WalletRequestRepository;

@RestController
@RequestMapping("/admin/wallet")
public class AdminWalletController {

    @Autowired
    private WalletRequestRepository requestRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @GetMapping("/requests")
    public List<WalletRequest> allRequests() {
        return requestRepo.findAll();
    }

    @PutMapping("/{id}/approve")
    public WalletRequest approve(
            @PathVariable Long id
    ) {

        WalletRequest request =
                requestRepo.findById(id)
                        .orElseThrow();

        if ("APPROVED".equals(request.getStatus())) {
            throw new RuntimeException(
                    "Already approved"
            );
        }

        User user =
                userRepo.findByUsername(
                        request.getUsername()
                ).orElseThrow();

        user.setBalance(
                user.getBalance()
                        + request.getAmount()
        );

        userRepo.save(user);

        Transaction txn =
                new Transaction();

        txn.setUsername(
                request.getUsername()
        );

        txn.setAmount(
                request.getAmount()
        );

        txn.setType("CREDIT");

        txn.setStatus("SUCCESS");

        txn.setDescription(
                "Wallet Topup"
        );

        transactionRepo.save(txn);

        request.setStatus(
                "APPROVED"
        );

        return requestRepo.save(
                request
        );
    }

    @PutMapping("/{id}/reject")
    public WalletRequest reject(
            @PathVariable Long id
    ) {

        WalletRequest request =
                requestRepo.findById(id)
                        .orElseThrow();

        request.setStatus(
                "REJECTED"
        );

        return requestRepo.save(
                request
        );
    }
}
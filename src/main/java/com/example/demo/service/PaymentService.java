package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.WalletRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WalletRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private WalletRequestRepository walletRequestRepo;


    // =========================================================
    // CREATE MANUAL PAYMENT REQUEST
    // =========================================================

    public WalletRequest createPaymentRequest(
            String username,
            double amount,
            String utr
    ) {

        // -----------------------------------------------------
        // Validate amount
        // -----------------------------------------------------

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }


        // -----------------------------------------------------
        // Validate UTR
        // -----------------------------------------------------

        if (utr == null || utr.trim().isEmpty()) {
            throw new RuntimeException("UTR is required");
        }


        // -----------------------------------------------------
        // Get user
        // -----------------------------------------------------

        User user = userRepo.findByUsername(username)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );


        // -----------------------------------------------------
        // Create request
        // -----------------------------------------------------

        WalletRequest request = new WalletRequest();

        request.setUsername(user.getUsername());
        request.setAmount(amount);
        request.setUtr(utr.trim());
        request.setStatus("PENDING");


        return walletRequestRepo.save(request);
    }


    // =========================================================
    // GET USER PAYMENT REQUESTS
    // =========================================================

    public List<WalletRequest> getUserRequests(
            String username
    ) {

        return walletRequestRepo
                .findByUsernameOrderByIdDesc(username);
    }


    // =========================================================
    // GET ALL PAYMENT REQUESTS - ADMIN
    // =========================================================

    public List<WalletRequest> getAllRequests() {

        return walletRequestRepo.findAllByOrderByIdDesc();
    }


    // =========================================================
    // APPROVE PAYMENT REQUEST - ADMIN
    // =========================================================

    @Transactional
    public String approveRequest(Long requestId) {

        WalletRequest request =
                walletRequestRepo.findById(requestId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Payment request not found"
                                )
                        );


        // -----------------------------------------------------
        // Prevent duplicate approval
        // -----------------------------------------------------

        if ("APPROVED".equalsIgnoreCase(
                request.getStatus()
        )) {

            throw new RuntimeException(
                    "Payment request already approved"
            );
        }


        // -----------------------------------------------------
        // Don't approve rejected request
        // -----------------------------------------------------

        if ("REJECTED".equalsIgnoreCase(
                request.getStatus()
        )) {

            throw new RuntimeException(
                    "Rejected request cannot be approved"
            );
        }


        // -----------------------------------------------------
        // Get user
        // -----------------------------------------------------

        User user =
                userRepo.findByUsername(
                        request.getUsername()
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );


        // -----------------------------------------------------
        // CREDIT WALLET
        // -----------------------------------------------------

        double oldBalance =
                user.getBalance();

        double newBalance =
                oldBalance + request.getAmount();

        user.setBalance(newBalance);

        userRepo.save(user);


        // -----------------------------------------------------
        // UPDATE REQUEST STATUS
        // -----------------------------------------------------

        request.setStatus("APPROVED");

        walletRequestRepo.save(request);


        return "Payment request approved and wallet credited";
    }


    // =========================================================
    // REJECT PAYMENT REQUEST - ADMIN
    // =========================================================

    @Transactional
    public String rejectRequest(Long requestId) {

        WalletRequest request =
                walletRequestRepo.findById(requestId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Payment request not found"
                                )
                        );


        // -----------------------------------------------------
        // Prevent changing approved request
        // -----------------------------------------------------

        if ("APPROVED".equalsIgnoreCase(
                request.getStatus()
        )) {

            throw new RuntimeException(
                    "Approved request cannot be rejected"
            );
        }


        // -----------------------------------------------------
        // Already rejected
        // -----------------------------------------------------

        if ("REJECTED".equalsIgnoreCase(
                request.getStatus()
        )) {

            throw new RuntimeException(
                    "Payment request already rejected"
            );
        }


        // -----------------------------------------------------
        // UPDATE STATUS
        // -----------------------------------------------------

        request.setStatus("REJECTED");

        walletRequestRepo.save(request);


        return "Payment request rejected";
    }
}

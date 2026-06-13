package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.WalletRequest;

public interface WalletRequestRepository extends JpaRepository<WalletRequest, Long> {
	
	List<WalletRequest> findByUsername(String username);

    boolean existsByUtr(String utr);
    
    List<WalletRequest> findByStatus(String status);
}


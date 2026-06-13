package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;


public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(
		    String email
		);
	Optional<User> findByApiKey(String apiKey);
}

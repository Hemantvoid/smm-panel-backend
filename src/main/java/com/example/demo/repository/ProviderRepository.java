package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Provider;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
	Optional<Provider> findByName(String Name);
}

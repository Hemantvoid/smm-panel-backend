package com.example.demo.repository;

import com.example.demo.model.Provider;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long>{
	List<ServiceEntity> findByProviderId(Long providerId);
	List<ServiceEntity> findByActiveTrue();
	List<ServiceEntity> findByNameContainingIgnoreCase(String name);
	Optional<ServiceEntity> findByProviderAndProviderServiceId(
		    Provider provider,
		    Integer providerServiceId
		);
}
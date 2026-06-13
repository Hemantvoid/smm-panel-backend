package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ServiceProviderMapping;

public interface ServiceProviderMappingRepository extends JpaRepository<ServiceProviderMapping, Long> {
	
	List<ServiceProviderMapping> 
	findByServiceIdAndActiveTrueOrderByPriorityAsc(Long serviceId);
	Optional<ServiceProviderMapping> findTopByServiceIdAndActiveTrueOrderByPriorityAsc(Long serviceId);

}

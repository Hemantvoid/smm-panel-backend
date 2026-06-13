package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.demo.model.ServiceEntity;
import com.example.demo.repository.ServiceRepository;

@Service
public class ServiceService{
	
	@Autowired
	private ServiceRepository repo;
	
	public ServiceEntity addService(ServiceEntity service) {
		return repo.save(service);
	}
	public List<ServiceEntity> getAllServices(){
		return repo.findAll();
	}
	public ServiceEntity updateMargin(Long id, Double margin) {

	    ServiceEntity s = repo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Service not found"));

	    s.setMargin(margin);

	    // 🔥 auto sell price calculation
	    if (s.getCostPrice() != null && margin != null) {
	    	s.setSellPrice(s.getCostPrice() + (s.getCostPrice() * margin / 100));
	    }

	    return repo.save(s);
	}
	
}


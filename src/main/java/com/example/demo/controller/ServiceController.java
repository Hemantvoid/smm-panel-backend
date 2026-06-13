package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.service.ServiceService;

import jakarta.validation.Valid;

import com.example.demo.model.ServiceEntity;
import com.example.demo.repository.ServiceRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping
public class ServiceController{
	
	@Autowired
	private ServiceService service;
	
	@Autowired
	private ServiceRepository serviceRepo;
	
	@PostMapping("/admin/services")
	public ServiceEntity addService(@Valid @RequestBody ServiceEntity serviceEntity) {
		return service.addService(serviceEntity);
	}
	@GetMapping("/services")
	public List<ServiceEntity> getServices(){
		return service.getAllServices();
	}
	@GetMapping("/public")
	public List<ServiceEntity> publicServices() {

	    return serviceRepo
	            .findByActiveTrue();
	}
}
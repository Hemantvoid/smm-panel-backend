package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ServiceEntity;
import com.example.demo.repository.ServiceRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/services")
public class PublicServiceController {

    @Autowired
    private ServiceRepository serviceRepo;

    @GetMapping("/public")
    public List<ServiceEntity> getPublicServices() {

        return serviceRepo.findByActiveTrue();
    }
}
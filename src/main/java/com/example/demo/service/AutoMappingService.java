package com.example.demo.service;

import com.example.demo.model.ServiceEntity;
import com.example.demo.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AutoMappingService {

    @Autowired
    private ServiceRepository serviceRepo;

    // =========================
    // SINGLE AUTO MAP
    // =========================
    public ServiceEntity autoMap(Long serviceId, Double margin) {

        ServiceEntity base = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        List<ServiceEntity> all = serviceRepo.findAll();

        // =========================
        // FILTER LOGIC (STRICT)
        // =========================
        List<ServiceEntity> candidates = all.stream()
                .filter(s -> s.getProvider() != null) // only provider services
                .filter(s -> !s.getId().equals(base.getId())) // not self
                .filter(s -> s.getCategory() != null && base.getCategory() != null)
                .filter(s -> s.getCategory().equalsIgnoreCase(base.getCategory())) // SAME CATEGORY ONLY
                .filter(s -> s.getCostPrice() != null && s.getCostPrice() > 1) // remove garbage cheap
                .filter(s -> s.getProviderServiceId() != null)
                .toList();

        if (candidates.isEmpty()) {
            throw new RuntimeException("No matching provider services found");
        }

        // =========================
        // PICK BEST (LOWEST COST)
        // =========================
        ServiceEntity best = candidates.stream()
                .min(Comparator.comparing(ServiceEntity::getCostPrice))
                .orElseThrow(() -> new RuntimeException("No valid provider found"));

        // =========================
        // APPLY MAPPING
        // =========================
     // APPLY MAPPING
        base.setProvider(best.getProvider());
        base.setProviderServiceId(best.getProviderServiceId());

        // 🔥 USE PROVIDER COST
        double cost = base.getCostPrice();

        // PRICING
        double sell;

        if (margin != null && margin > 0) {
            sell = cost + (cost * margin / 100);
        } else {
            if (cost < 20) {
                sell = cost + 5;
            } else if (cost < 100) {
                sell = cost + 3;
            } else {
                sell = cost + 2;
            }
        }

        base.setSellPrice(sell);

        return serviceRepo.save(base);
    }

    // =========================
    // AUTO MAP ALL
    // =========================
    public void autoMapAll(Double margin) {

        List<ServiceEntity> allServices = serviceRepo.findAll();

        for (ServiceEntity service : allServices) {
            try {
                autoMap(service.getId(), margin);
            } catch (Exception e) {
                System.out.println("❌ Failed for service: " + service.getId());
            }
        }
    }
}
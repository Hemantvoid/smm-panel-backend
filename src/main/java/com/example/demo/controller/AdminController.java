package com.example.demo.controller;

import com.example.demo.model.ApiLog;
import com.example.demo.model.OrderEntity;
import com.example.demo.model.Provider;
import com.example.demo.model.ServiceEntity;
import com.example.demo.repository.ApiLogRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProviderRepository;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.service.AdminService;
import com.example.demo.service.AutoMappingService;
import com.example.demo.service.OrderSyncService;
import com.example.demo.service.ServiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private ProviderRepository providerRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private OrderSyncService syncService;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private AutoMappingService autoMappingService;
    
    @Autowired
    private ApiLogRepository apiLogRepo;
    

    // =========================
    // ➕ ADD PROVIDER
    // =========================
    @PostMapping("/providers")
    public Provider addProvider(@RequestBody Provider provider) {

        // 🔥 sanitize input (IMPORTANT)
        provider.setApiUrl(
            provider.getApiUrl().trim().replaceAll("\\s+", "")
        );

        provider.setApiKey(
            provider.getApiKey().trim()
        );

        provider.setName(
            provider.getName().trim()
        );

        return providerRepo.save(provider);
    }

    // =========================
    // 📡 GET ALL PROVIDERS
    // =========================
    @GetMapping("/providers")
    public List<Provider> getProviders() {
        return providerRepo.findAll();
    }
    
    @PutMapping("/services/reset-pricing")
    public String resetPricing() {

        List<ServiceEntity> services = serviceRepo.findAll();

        for (ServiceEntity s : services) {
            if (s.getCostPrice() != null) {
                s.setSellPrice(s.getCostPrice());
            }
        }

        serviceRepo.saveAll(services);

        return "Pricing reset";
    }

    // =========================
    // 🔄 SYNC PROVIDER SERVICES
    // =========================
    @PostMapping("/provider/services/sync")
    public String syncServices(@RequestParam Long providerId) {
        syncService.syncProviderServices(providerId);
        return "Services synced successfully";
    }

    // =========================
    // 📦 GET ALL SERVICES
    // =========================
    @GetMapping("/services")
    public List<ServiceEntity> getServices() {
        return serviceRepo.findAll();
    }

    // =========================
    // ✏️ UPDATE MARGIN (MAIN CONTROL)
    // =========================
    @PutMapping("/services/{id}/margin")
    public ServiceEntity updateMargin(
            @PathVariable Long id,
            @RequestParam Double margin
    ) {
        return serviceService.updateMargin(id, margin);
    }

    // =========================
    // ✏️ UPDATE SERVICE (LIMITS ONLY)
    // =========================
    @PutMapping("/services/{id}")
    public ServiceEntity updateService(
            @PathVariable Long id,
            @RequestBody ServiceEntity updated
    ) {
        ServiceEntity s = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        s.setMin(updated.getMin());
        s.setMax(updated.getMax());

        return serviceRepo.save(s);
    }

    // =========================
    // ❌ DELETE SERVICE
    // =========================
    @DeleteMapping("/providers/{id}")
    public String deleteProvider(@PathVariable Long id) {

        Provider provider = providerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        // Pending orders check
        List<OrderEntity> pendingOrders = orderRepo.findByProviderAndStatusIn(
                provider,
                List.of("PENDING", "PROCESSING", "UNKNOWN")
        );

        if (!pendingOrders.isEmpty()) {
            throw new RuntimeException(
                    "Provider has pending orders. Delete not allowed."
            );
        }

        // Delete services
        serviceRepo.deleteByProvider(provider);

        // Delete provider
        providerRepo.delete(provider);

        return "Provider deleted successfully";
    }

    // =========================
    // 📊 DASHBOARD
    // =========================
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return adminService.getDashboard();
    }

    // =========================
    // 📦 ALL ORDERS (ADMIN)
    // =========================
    @GetMapping("/orders")
    public List<OrderEntity> getAllOrders() {
        return orderRepo.findAll();
    }
    @GetMapping("/provider/{id}/services")
    public List<ServiceEntity> getProviderServices(@PathVariable Long id) {
        return serviceRepo.findByProviderId(id);
    }
    
    @PutMapping("/services/{id}/auto-map")
    public ServiceEntity autoMap(
            @PathVariable Long id,
            @RequestParam Double margin
    ) {
        return autoMappingService.autoMap(id, margin);
    }
    
    @PutMapping("/services/auto-map-all")
    public String autoMapAll(@RequestParam Double margin) {
        autoMappingService.autoMapAll(margin);
        return "All services mapped";
    }
    
    @GetMapping("/api-logs")
    public List<ApiLog> getApiLogs() {

        return apiLogRepo
                .findTop100ByOrderByRequestTimeDesc();
    }
}

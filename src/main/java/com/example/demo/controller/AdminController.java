package com.example.demo.controller;

import org.springframework.transaction.annotation.Transactional;
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
import com.example.demo.model.ServiceProviderMapping;
import com.example.demo.repository.ServiceProviderMappingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
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

    @Autowired
    private ServiceProviderMappingRepository mappingRepo;
    

    // =========================
    // ➕ ADD PROVIDER
    // =========================
    
@PostMapping("/providers")
public Provider addProvider(@RequestBody Provider provider) {

    provider.setApiUrl(
        provider.getApiUrl().trim().replaceAll("\\s+", "")
    );

    provider.setApiKey(provider.getApiKey().trim());
    provider.setName(provider.getName().trim());

    // Newly added providers should be active
    provider.setStatus(true);

    return providerRepo.save(provider);
}

    @GetMapping("/providers")
public List<Provider> getProviders() {
    return providerRepo.findByStatusTrue();
}

    // =========================
    // 📡 GET ALL PROVIDERS
    // =========================
   @GetMapping("/services")
    public List<ServiceEntity> getServices() {
    return serviceRepo.findByActiveTrue()
        .stream()
        .filter(service ->
            service.getProvider() != null
            && service.getProvider().isStatus()
        )
        .toList();
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
@Transactional
public ResponseEntity<?> deleteProvider(@PathVariable Long id) {

    Provider provider = providerRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Provider not found"
        ));

    List<OrderEntity> pendingOrders =
        orderRepo.findByProviderAndStatusIn(
            provider,
            List.of("PENDING", "PROCESSING", "UNKNOWN")
        );

    if (!pendingOrders.isEmpty()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            Map.of("message",
                "Provider has pending orders. Resolve them first.")
        );
    }

    List<ServiceEntity> services =
        serviceRepo.findByProvider(provider);

    boolean hasOrderHistory =
        services.stream().anyMatch(orderRepo::existsByService);

    if (hasOrderHistory) {
        services.forEach(service -> service.setActive(false));
        serviceRepo.saveAll(services);

        List<ServiceProviderMapping> mappings =
            mappingRepo.findByProvider(provider);

        mappings.forEach(mapping -> mapping.setActive(false));
        mappingRepo.saveAll(mappings);

        provider.setStatus(false);
        providerRepo.save(provider);

        return ResponseEntity.ok(
            Map.of("message",
                "Provider deactivated. Historical orders preserved.")
        );
    }

    serviceRepo.deleteByProvider(provider);
    providerRepo.delete(provider);

    return ResponseEntity.ok(
        Map.of("message", "Provider deleted successfully")
    );
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

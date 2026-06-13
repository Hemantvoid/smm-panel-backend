package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.provider.ProviderFactory;
import com.example.demo.provider.SmmProvider;
import com.example.demo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderSyncService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProviderFactory providerFactory;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private ProviderRepository providerRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private TransactionRepository transactionRepo;
    
    public void syncProviderServices(Long providerId) {

        Provider provider = providerRepo.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        System.out.println("🚀 Syncing services for: " + provider.getName());

        SmmProvider smm = providerFactory.getProvider(provider);

        List<Map<String, Object>> services;

        try {
            services = smm.fetchServices(
                    provider.getApiKey(),
                    provider.getApiUrl()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch services");
        }

        if (services == null || services.isEmpty()) {
            throw new RuntimeException("No services returned");
        }
        
        for (Map<String, Object> s : services) {
            try {

                // 🔥 REQUIRED FIELDS
                if (s.get("service") == null || s.get("name") == null || s.get("rate") == null) {
                    System.out.println("⚠️ Skipping invalid: " + s);
                    continue;
                }

                Integer providerServiceId = Integer.parseInt(s.get("service").toString());
                String name = s.get("name").toString();
                Double rate = Double.parseDouble(s.get("rate").toString());

                ServiceEntity service = serviceRepo
                	    .findByProviderAndProviderServiceId(provider, providerServiceId)
                	    .orElse(new ServiceEntity());

                service.setProvider(provider);
                service.setProviderServiceId(providerServiceId);
                service.setName(name);

                // 🔥 THIS WAS YOUR BUG
              
                service.setCostPrice(rate);
                service.setSellPrice(rate * 1.5);

                if (s.get("min") != null) {
                    service.setMin(Integer.parseInt(s.get("min").toString()));
                }

                if (s.get("max") != null) {
                    service.setMax(Integer.parseInt(s.get("max").toString()));
                }

                if (s.get("category") != null) {
                    service.setCategory(s.get("category").toString());
                }

                serviceRepo.save(service);

            } catch (Exception e) {
                System.out.println("❌ Failed service: " + s);
                e.printStackTrace();
            }
        }

        System.out.println("✅ Services synced: " + provider.getName());
    }

    // ============================
    // 🔥 AUTO SYNC + REFUND
    // ============================
    @Scheduled(fixedRate = 60000)
    public void autoSync() {

        System.out.println("🔄 Running order sync...");

        List<OrderEntity> orders = orderRepo.findByStatusIn(
        		List.of(
        			    "PROCESSING",
        			    "PENDING",
        			    "UNKNOWN"
        			)
        );

        for (OrderEntity order : orders) {

            try {

                if (order.getProviderOrderId() == null || order.getProviderOrderId().isEmpty())
                    continue;

                Provider provider = order.getProvider();
                SmmProvider smm = providerFactory.getProvider(provider);

                Map<String, Object> res = smm.checkOrderStatus(
                        order.getProviderOrderId(),
                        provider.getApiKey(),
                        provider.getApiUrl()
                );
                System.out.println(
                	    "ORDER " +
                	    order.getProviderOrderId() +
                	    " RESPONSE => " +
                	    res
                	);

                if (res == null || res.get("status") == null) continue;

                String newStatus = normalizeStatus(res.get("status").toString());
                System.out.println(
                	    "STATUS FROM PROVIDER => " +
                	    res.get("status")
                	);

                	System.out.println(
                	    "NORMALIZED => " +
                	    newStatus
                	);

                // 🔥 only if status changed
                if (!newStatus.equals(order.getStatus())) {

                    order.setStatus(newStatus);

                    // ============================
                    // 💣 REFUND LOGIC
                    // ============================
                    if (!order.isRefundProcessed()) {

                        if (newStatus.equals("CANCELLED") || newStatus.equals("FAILED")) {

                            refundFull(order);

                        } else if (newStatus.equals("PARTIAL")) {

                            double remains = res.get("remains") != null
                                    ? Double.parseDouble(res.get("remains").toString())
                                    : 0;

                            refundPartial(order, remains);
                        }
                    }

                    orderRepo.save(order);

                    System.out.println("✅ Order " + order.getId() + " → " + newStatus);
                }

            } catch (Exception e) {
                System.out.println("❌ Failed order: " + order.getId() + " ERROR: " + e.getMessage());
            }
        }
    }

    // ============================
    // 💰 FULL REFUND
    // ============================
    private void refundFull(OrderEntity order) {

    	String username =
    	        order.getUsername();

    	if (username == null || username.isBlank()) {
    	    username =
    	            order.getCustomerName();
    	}

    	User user = userRepo.findByUsername(
    	        username
    	).orElseThrow();

        double refund = order.getSellPrice();

        user.setBalance(user.getBalance() + refund);
        userRepo.save(user);

        order.setRefundProcessed(true);

        Transaction txn = new Transaction();
        txn.setUsername(order.getUsername());
        txn.setAmount(refund);
        txn.setType("CREDIT");
        txn.setStatus("SUCCESS");
        txn.setDescription("Full refund");

        transactionRepo.save(txn);

        System.out.println("💰 Full refund: " + refund);
    }

    // ============================
    // 💰 PARTIAL REFUND
    // ============================
    private void refundPartial(OrderEntity order, double remains) {

        double pricePerUnit = order.getSellPrice() / order.getQuantity();
        double refund = remains * pricePerUnit;

        String username =
                order.getUsername();

        if (username == null || username.isBlank()) {
            username =
                    order.getCustomerName();
        }

        User user = userRepo.findByUsername(
                username
        ).orElseThrow();
        
        user.setBalance(user.getBalance() + refund);
        userRepo.save(user);

        order.setRefundProcessed(true);

        Transaction txn = new Transaction();
        txn.setUsername(order.getUsername());
        txn.setAmount(refund);
        txn.setType("CREDIT");
        txn.setStatus("SUCCESS");
        txn.setDescription("Partial refund");

        transactionRepo.save(txn);

        System.out.println("💰 Partial refund: " + refund);
    }

    // ============================
    // 🔄 STATUS NORMALIZATION
    // ============================
    private String normalizeStatus(String status) {

        if (status == null) return "UNKNOWN";

        status = status.toLowerCase();

        if (status.contains("complete")) return "COMPLETED";
        if (status.contains("success")) return "COMPLETED";

        if (status.contains("process")) return "PROCESSING";
        if (status.contains("progress")) return "PROCESSING";

        if (status.contains("pending")) return "PENDING";

        if (status.contains("partial")) return "PARTIAL";

        if (status.contains("cancel")) return "CANCELLED";
        if (status.contains("fail")) return "FAILED";

        return "UNKNOWN";
    }
}
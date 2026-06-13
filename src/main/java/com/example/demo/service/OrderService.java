package com.example.demo.service;

import com.example.demo.dto.OrderRequest;

import com.example.demo.model.Transaction;import com.example.demo.model.Transaction;
import com.example.demo.dto.OrderResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.OrderEntity;
import com.example.demo.model.Provider;
import com.example.demo.model.ServiceEntity;
import com.example.demo.model.User;
import com.example.demo.provider.ProviderFactory;
import com.example.demo.provider.SmmProvider;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private ProviderFactory providerFactory;

    // =========================
    // 🚀 PLACE ORDER
    // =========================
    @Transactional
    public OrderEntity placeOrder(
            OrderRequest req,
            String username
    ) {

        ServiceEntity service =
                serviceRepo.findById(req.getServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service not found"
                                ));

        Provider provider =
                service.getProvider();

        if (provider == null) {
            throw new RuntimeException(
                    "No provider assigned"
            );
        }

        if (service.getProviderServiceId() == null) {
            throw new RuntimeException(
                    "Provider service ID missing"
            );
        }
        
        
        // =========================
        // 💰 CALCULATE PRICE
        // =========================

        double costPerThousand =
                service.getCostPrice() != null
                        ? service.getCostPrice()
                        : 0;

        double sellPerThousand =
                service.getSellPrice() != null
                        ? service.getSellPrice()
                        : 0;

        double cost =
                (costPerThousand / 1000.0)
                        * req.getQuantity();

        double sell =
                (sellPerThousand / 1000.0)
                        * req.getQuantity();

        // =========================
        // 👤 USER
        // =========================

        User user =
                userRepo.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        if (user.getBalance() < sell) {

            throw new RuntimeException(
                    "Insufficient balance"
            );
        }

        // =========================
        // 🔥 PROVIDER ORDER
        // =========================

        SmmProvider smm =
                providerFactory.getProvider(provider);

        Map<String, Object> res =
                smm.placeOrder(
                        service.getProviderServiceId(),
                        req.getLink(),
                        req.getQuantity(),
                        provider.getApiKey(),
                        provider.getApiUrl()
                );

        if (res == null ||
                res.get("order") == null) {

            throw new RuntimeException(
                    "Provider order failed"
            );
        }

        String providerOrderId =
                res.get("order").toString();

     // =========================
     // 💳 WALLET DEDUCTION
     // =========================

     if (user.getBalance() < sell) {
         throw new RuntimeException("Insufficient balance");
     }

     user.setBalance(
         user.getBalance() - sell
     );

     userRepo.save(user);
     
     Transaction txn = new Transaction();

     txn.setUsername(username);
     txn.setAmount(sell);

     txn.setType("DEBIT");
     txn.setStatus("SUCCESS");

     txn.setDescription(
             "Order #" + providerOrderId
     );

     transactionRepo.save(txn);
     
        // =========================
        // 💾 SAVE ORDER
        // =========================

        OrderEntity order =
                new OrderEntity();

        order.setService(service);
        order.setProvider(provider);

        order.setCustomerName(username);
        order.setUsername(username);

        order.setQuantity(req.getQuantity());

        order.setProviderOrderId(
                providerOrderId
        );

        order.setStatus("PENDING");

        order.setCostPrice(cost);
        order.setSellPrice(sell);
        order.setProfit(sell - cost);

        return orderRepo.save(order);
    }

    // =========================
    // 📦 GET ORDERS
    // =========================

    public Page<OrderResponse> getOrders(
            String username,
            String role,
            Pageable pageable
    ) {

        Page<OrderEntity> orders;

        if ("ROLE_ADMIN".equals(role)) {

            orders =
                    orderRepo.findAll(
                            pageable
                    );

        } else {

            orders =
                    orderRepo.findByCustomerName(
                            username,
                            pageable
                    );
        }

        return orders.map(
                OrderMapper::toResponse
        );
    }
}
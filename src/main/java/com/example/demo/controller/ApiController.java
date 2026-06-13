package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.OrderRequest;
import com.example.demo.model.OrderEntity;
import com.example.demo.model.ServiceEntity;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ServiceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;

import com.example.demo.model.ApiLog;
import com.example.demo.repository.ApiLogRepository;

@Tag(
	    name = "Reseller API",
	    description = "SMM Reseller Endpoints"
	)
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private ApiLogRepository apiLogRepo;
    
    @Operation(
    	    summary = "Reseller API Endpoint",
    	    description = """
    	        Supported Actions:
    	        services - Get all active services
    	        balance - Get wallet balance
    	        add - Place new order
    	        status - Check order status
    	        """
    	)
    @PostMapping
    public Object api(
    		
    		HttpServletRequest httpRequest,
    		
            @RequestParam String key,

            @RequestParam String action,

            @RequestParam(required = false)
            Long service,

            @RequestParam(required = false)
            String link,

            @RequestParam(required = false)
            Integer quantity,
            
            @RequestParam(required = false)
            Long orders,
            
            @RequestParam(required = false)
    		String orderIds

    ) {

        // API KEY CHECK

        User user = userRepo
                .findByApiKey(key)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid API Key"
                        ));
        ApiLog log = new ApiLog();

        log.setUsername(
                user.getUsername()
        );

        log.setAction(
                action
        );

        log.setRequestTime(
                LocalDateTime.now()
        );

        log.setIpAddress(
                httpRequest.getRemoteAddr()
        );

        apiLogRepo.save(log);

        // ==================================
        // SERVICES
        // ==================================

        if ("services".equalsIgnoreCase(action)) {

            return serviceRepo.findAll()
                    .stream()
                    .filter(ServiceEntity::isActive)
                    .map(serviceEntity -> {

                        Map<String, Object> s =
                                new HashMap<>();

                        s.put(
                                "service",
                                serviceEntity.getId()
                        );

                        s.put(
                                "name",
                                serviceEntity.getName()
                        );

                        s.put(
                                "rate",
                                serviceEntity.getSellPrice()
                        );

                        s.put(
                                "min",
                                serviceEntity.getMin()
                        );

                        s.put(
                                "max",
                                serviceEntity.getMax()
                        );

                        s.put(
                                "category",
                                serviceEntity.getCategory()
                        );

                        return s;

                    })
                    .toList();
        }

        // ==================================
        // BALANCE
        // ==================================

        if ("balance".equalsIgnoreCase(action)) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "balance",
                    user.getBalance()
            );

            response.put(
                    "currency",
                    "INR"
            );

            return response;
        }

        // ==================================
        // ADD ORDER
        // ==================================

        if ("add".equalsIgnoreCase(action)) {

            if (service == null ||
                    link == null ||
                    quantity == null) {

                throw new RuntimeException(
                        "Missing parameters"
                );
            }

            OrderRequest request =
                    new OrderRequest();

            request.setServiceId(service);

            request.setLink(link);

            request.setQuantity(quantity);

            OrderEntity order =
                    orderService.placeOrder(
                            request,
                            user.getUsername()
                    );

            return Map.of(
                    "order",
                    order.getId()
            );
        }
        
        if ("status".equalsIgnoreCase(action)) {

            if (orders == null) {

                throw new RuntimeException(
                        "Order ID required"
                );
            }

            OrderEntity dbOrder =
                    orderRepo.findById(orders)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Order not found"
                                    ));
            if (!dbOrder.getUsername()
                    .equals(user.getUsername())) {

                throw new RuntimeException(
                        "Order not found"
                );
            }

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "order",
                    dbOrder.getId()
            );

            response.put(
                    "status",
                    dbOrder.getStatus()
            );

            response.put(
                    "charge",
                    dbOrder.getSellPrice()
            );

            response.put(
                    "start_count",
                    0
            );

            response.put(
                    "remains",
                    0
            );

            return response;
        }
        
        if ("statuses".equalsIgnoreCase(action)) {

            if (orderIds == null ||
                    orderIds.isBlank()) {

                throw new RuntimeException(
                        "Order IDs required"
                );
            }

            Map<String, Object> result =
                    new HashMap<>();

            String[] ids =
                    orderIds.split(",");

            for (String id : ids) {

                try {

                    Long orderId =
                            Long.parseLong(
                                    id.trim()
                            );

                    OrderEntity dbOrder =
                            orderRepo.findById(
                                    orderId
                            ).orElse(null);

                    if (dbOrder == null) {
                        continue;
                    }

                    if (!dbOrder.getUsername()
                            .equals(
                                    user.getUsername()
                            )) {
                        continue;
                    }

                    Map<String, Object> orderData =
                            new HashMap<>();

                    orderData.put(
                            "status",
                            dbOrder.getStatus()
                    );

                    orderData.put(
                            "charge",
                            dbOrder.getSellPrice()
                    );

                    orderData.put(
                            "start_count",
                            0
                    );

                    orderData.put(
                            "remains",
                            0
                    );

                    result.put(
                            String.valueOf(orderId),
                            orderData
                    );

                } catch (Exception ignored) {
                }
            }

            return result;
        }

        throw new RuntimeException(
                "Invalid action"
        );
    }
}
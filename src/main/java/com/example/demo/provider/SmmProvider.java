package com.example.demo.provider;

import java.util.List;
import java.util.Map;

public interface SmmProvider {

    Map<String, Object> placeOrder(
            Integer serviceId,
            String link,
            int quantity,
            String apiKey,
            String apiUrl
    );

    Map<String, Object> checkOrderStatus(
            String orderId,
            String apiKey,
            String apiUrl
    );

    String checkBalance(String apiKey, String apiUrl);

    List<Map<String, Object>> fetchServices(String apiKey, String apiUrl);
}
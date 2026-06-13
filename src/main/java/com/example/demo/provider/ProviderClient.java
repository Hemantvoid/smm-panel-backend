package com.example.demo.provider;

import com.example.demo.dto.ProviderResponse;
import com.example.demo.dto.ProviderStatusResponse;
import com.example.demo.model.Provider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProviderClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public ProviderResponse placeOrder(
            Provider provider,
            Integer serviceId,
            String link,
            int quantity
    ) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("key", provider.getApiKey());
            body.put("action", "add");
            body.put("service", serviceId);
            body.put("link", link);
            body.put("quantity", quantity);

            Map res = restTemplate.postForObject(
                    provider.getApiUrl(),
                    body,
                    Map.class
            );

            ProviderResponse pr = new ProviderResponse();

            if (res != null && res.get("order") != null) {
                pr.setOrderId(Long.parseLong(res.get("order").toString()));
            }

            return pr;

        } catch (Exception e) {
            return null;
        }
    }
    public ProviderStatusResponse checkStatus(Provider provider, String orderId) {

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("key", provider.getApiKey());
            body.put("action", "status");
            body.put("order", orderId);

            Map res = restTemplate.postForObject(
                    provider.getApiUrl(),
                    body,
                    Map.class
            );

            ProviderStatusResponse ps = new ProviderStatusResponse();

            if (res != null) {
                ps.setStatus((String) res.get("status"));

                if (res.get("remains") != null) {
                    ps.setRemains(Integer.parseInt(res.get("remains").toString()));
                }
            }

            return ps;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
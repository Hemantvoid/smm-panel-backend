package com.example.demo.provider;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("panelX")
@Component
public class PanelXProvider implements SmmProvider {

    @Autowired
    private RestTemplate restTemplate;

    // ============================
    // 🔥 PLACE ORDER (FIXED)
    // ============================
    @Override
    public Map<String, Object> placeOrder(
            Integer serviceId,
            String link,
            int quantity,
            String apiKey,
            String apiUrl
    ) {

        String url = apiUrl;

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("action", "add");
        body.add("service", String.valueOf(serviceId));
        body.add("quantity", String.valueOf(quantity));

        if (link != null) {
            body.add("link", link);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
        	ResponseEntity<String> response =
        	        restTemplate.postForEntity(
        	            url,
        	            request,
        	            String.class
        	        );

        	String bodyRes = response.getBody();

        	System.out.println("PROVIDER RESPONSE:");
        	System.out.println(bodyRes);

        	if (
        	    bodyRes == null ||
        	    bodyRes.startsWith("<")
        	) {
        	    throw new RuntimeException(
        	        "Provider returned HTML instead of JSON"
        	    );
        	}

        	ObjectMapper mapper =
        	    new ObjectMapper();

        	return mapper.readValue(
        	    bodyRes,
        	    Map.class
        	);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // ============================
    // 🔍 ORDER STATUS
    // ============================
    @Override
    public Map<String, Object> checkOrderStatus(
            String orderId,
            String apiKey,
            String apiUrl
    ) {

        String url =
                apiUrl.trim()
                      .replaceAll("\\s+", "");

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("key", apiKey);
        body.add("action", "status");
        body.add("order", orderId);

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        HttpEntity<MultiValueMap<String, String>>
                request =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class
                    );

            String bodyRes =
                    response.getBody();

            System.out.println(
                    "STATUS RESPONSE FOR ORDER "
                            + orderId
                            + " => "
                            + bodyRes
            );

            if (
                    bodyRes == null ||
                    bodyRes.startsWith("<")
            ) {

                throw new RuntimeException(
                        "Provider returned HTML instead of JSON"
                );
            }

            ObjectMapper mapper =
                    new ObjectMapper();

            return mapper.readValue(
                    bodyRes,
                    Map.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
    // ============================
    // 💰 CHECK BALANCE
    // ============================
    @Override
    public String checkBalance(String apiKey, String apiUrl) {

        String url = apiUrl;

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("action", "balance");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(body, headers);

        try {
            Map res = restTemplate.postForObject(url, requestEntity, Map.class);
            return res.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // ============================
    // 📦 FETCH SERVICES
    // ============================
    @Override
    public List<Map<String, Object>> fetchServices(String apiKey, String apiUrl) {

        String url = apiUrl;

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("action", "services");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<List> response =
                    restTemplate.postForEntity(url, request, List.class);

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch services: " + e.getMessage());
        }
    }

    // ============================
    // 🔁 ALT STATUS (optional)
    // ============================
}
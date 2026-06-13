package com.example.demo.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Provider;

import java.util.Map;

import com.example.demo.model.Provider;

@Service
public class ProviderFactory {

    @Autowired
    private Map<String, SmmProvider> providers;

    public SmmProvider getProvider(Provider provider) {

        String type = provider.getType(); // 🔥 अब सही

        SmmProvider p = providers.get(type);

        if (p == null) {
            throw new RuntimeException("Provider not found: " + type);
        }

        return p;
    }
}
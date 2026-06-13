package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.OrderRepository;

@Service
public class AdminService {

    @Autowired
    private OrderRepository orderRepo;

    public Map<String, Object> getDashboard() {

        Map<String, Object> res = new HashMap<>();

        res.put("revenue", safe(orderRepo.getTotalRevenue()));
        res.put("cost", safe(orderRepo.getTotalCost()));
        res.put("profit", safe(orderRepo.getTotalProfit()));
        res.put("todayProfit", safe(orderRepo.getTodayProfit()));

        res.put("totalOrders", orderRepo.getTotalOrders());
        res.put("completedOrders", orderRepo.getCompletedOrders());

        res.put("topServices", formatTopServices(orderRepo.getTopServices()));
        res.put("providerStats", formatProviders(orderRepo.getProviderStats()));

        return res;
    }

    private double safe(Double val) {
        return val != null ? val : 0;
    }

    // 🔥 raw Object[] ko readable banaya
    private List<Map<String, Object>> formatTopServices(List<Object[]> data) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object[] row : data) {
            Map<String, Object> m = new HashMap<>();
            m.put("service", row[0]);
            m.put("profit", row[1]);
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> formatProviders(List<Object[]> data) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object[] row : data) {
            Map<String, Object> m = new HashMap<>();
            m.put("provider", row[0]);
            m.put("orders", row[1]);
            m.put("profit", row[2]);
            list.add(m);
        }
        return list;
    }
}
package com.example.demo.mapper;

import com.example.demo.dto.OrderResponse;
import com.example.demo.model.OrderEntity;

public class OrderMapper {

    public static OrderResponse toResponse(OrderEntity order) {

        OrderResponse res = new OrderResponse();

        res.setOrderId(order.getId());
        res.setServiceName(order.getService().getName());
        res.setQuantity(order.getQuantity());
        res.setCustomerName(order.getCustomerName());

        // 🔥 REAL DATA (not fake)
        res.setStatus(order.getStatus());
        res.setMessage("Order placed");

        res.setCostPrice(order.getCostPrice());
        res.setSellPrice(order.getSellPrice());
        res.setProfit(order.getProfit());

        return res;
    }
}
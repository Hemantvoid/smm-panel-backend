package com.example.demo.dto;

import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    private Long serviceId;
    private int quantity;
    private String customerName;
    private Integer providerServiceId;
    private String link;
    
    public OrderRequest() {
    }
    public OrderRequest(Long providerServiceId, int quantity, String customerName, String link) {
    	this.providerServiceId = providerServiceId != null ? providerServiceId.intValue() : null;
        this.quantity = quantity;
        this.customerName = customerName;
        this.link = link;
    }
    
    public Integer getProviderServiceId() {
		return providerServiceId;
	}

	public void setProviderServiceId(Integer providerServiceId) {
		this.providerServiceId = providerServiceId;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String username;

    private String providerOrderId;
    private String status;

    private Double costPrice;   // provider cost
    private Double sellPrice;   // user charged
    private Double profit;

    private int quantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    @JsonIgnore
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "service_id")
    @JsonIgnore
    private ServiceEntity service;
    
    private boolean refundProcessed = false;

    public boolean isRefundProcessed() {
		return refundProcessed;
	}

	public void setRefundProcessed(boolean refundProcessed) {
		this.refundProcessed = refundProcessed;
	}

	@PrePersist
	public void prePersist() {

	    this.createdAt = LocalDateTime.now();

	    if (this.status == null) {
	        this.status = "PENDING";
	    }
	}
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 🔹 GETTERS / SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProviderOrderId() { return providerOrderId; }
    public void setProviderOrderId(String providerOrderId) { this.providerOrderId = providerOrderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public Double getProfit() { return profit; }
    public void setProfit(Double profit) { this.profit = profit; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }
}
package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"serviceId", "provider_id"})
    }
)
public class ServiceProviderMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long serviceId;               // internal service

    private Long providerServiceId;       // provider service id

    private Double costPrice;             // provider cost

    private Double margin;                // your margin

    private Double sellPrice;             // final price

    private boolean active = true;

    private Integer priority = 1;         // lower = higher priority

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    // ================= LOGIC =================

    public void calculateSellPrice() {
        if (costPrice != null && margin != null) {
            this.sellPrice = costPrice + margin;
        }
    }

    // ================= GETTERS/SETTERS =================

    public Long getId() { return id; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getProviderServiceId() { return providerServiceId; }
    public void setProviderServiceId(Long providerServiceId) {
        this.providerServiceId = providerServiceId;
    }

    public Double getCostPrice() { return costPrice; }
    public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }

    public Double getMargin() { return margin; }
    public void setMargin(Double margin) { this.margin = margin; }

    public Double getSellPrice() { return sellPrice; }
    public void setSellPrice(Double sellPrice) { this.sellPrice = sellPrice; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
}
package com.example.demo.dto;

public class MappingRequest {

    private Long serviceId;
    private Long providerId;
    private Long providerServiceId;
    private Double costPrice;
    private Double margin;
	public Double getMargin() {
		return margin;
	}
	public void setMargin(Double margin) {
		this.margin = margin;
	}
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public Long getProviderId() {
		return providerId;
	}
	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}
	public Long getProviderServiceId() {
		return providerServiceId;
	}
	public void setProviderServiceId(Long providerServiceId) {
		this.providerServiceId = providerServiceId;
	}
	public Double getCostPrice() {
		return costPrice;
	}
	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}
    

    // getters setters
}
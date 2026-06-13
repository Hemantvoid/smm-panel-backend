package com.example.demo.dto;

public class ProviderStatusResponse {

    private String status;
    private Integer remains;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRemains() {
        return remains;
    }

    public void setRemains(Integer remains) {
        this.remains = remains;
    }
}
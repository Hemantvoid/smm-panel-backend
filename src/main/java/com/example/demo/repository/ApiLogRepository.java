package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ApiLog;

public interface ApiLogRepository
        extends JpaRepository<ApiLog, Long> {

    List<ApiLog> findByUsernameOrderByRequestTimeDesc(
            String username
    );
    List<ApiLog> findTop100ByOrderByRequestTimeDesc();
}
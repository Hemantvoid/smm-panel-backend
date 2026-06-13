package com.example.demo.repository;

import com.example.demo.model.PanelSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanelSettingsRepository
        extends JpaRepository<PanelSettings, Long> {
}
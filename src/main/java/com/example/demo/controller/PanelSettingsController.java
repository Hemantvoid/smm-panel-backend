package com.example.demo.controller;

import com.example.demo.model.PanelSettings;
import com.example.demo.repository.PanelSettingsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/settings")
@CrossOrigin("*")
public class PanelSettingsController {

    @Autowired
    private PanelSettingsRepository repo;

    @GetMapping
    public PanelSettings getSettings() {

        return repo.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {

                    PanelSettings s =
                            new PanelSettings();

                    s.setPanelName("SMM Panel");
                    s.setPrimaryColor("#6366f1");
                    s.setSecondaryColor("#8b5cf6");
                    s.setThemeStyle("midnight");

                    return repo.save(s);
                });
    }

    // ✅ NEW PUBLIC ENDPOINT
    @GetMapping("/public")
    public PanelSettings getPublicSettings() {
        return getSettings();
    }

    @PutMapping
    public PanelSettings save(
            @RequestBody PanelSettings settings
    ) {

        settings.setId(1L);

        return repo.save(settings);
    }
}
package com.camping.admin.controller;

import com.camping.admin.service.DatabaseResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DatabaseResetService databaseResetService;

    public AdminController(DatabaseResetService databaseResetService) {
        this.databaseResetService = databaseResetService;
    }

    @PostMapping("/reset-db")
    public ResponseEntity<String> resetDatabase() {
        databaseResetService.resetDatabase();
        return ResponseEntity.ok("Database has been reset and re-seeded successfully.");
    }
}
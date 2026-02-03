package com.camping.admin.controller;

import com.camping.admin.dto.ProcessSaleRequest;
import com.camping.admin.dto.SalesRecordResponse;
import com.camping.admin.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sales") // Public-facing API prefix
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    public ResponseEntity<Void> processSale(@Valid @RequestBody ProcessSaleRequest request) {
        salesService.processSale(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SalesRecordResponse>> listSales() {
        var responses = salesService.findRecentSales(10);
        return ResponseEntity.ok(responses);
    }
}
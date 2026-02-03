package com.camping.admin.controller;

import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/reports/revenue")
@RequiredArgsConstructor
public class RevenueAdminController {

    private final SalesService salesService;

    @GetMapping("/daily")
    public ResponseEntity<DailyRevenueReportResponse> getDailyRevenueReport(
            @RequestParam LocalDate date) {
        var report = salesService.generateDailyRevenueReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/range")
    public ResponseEntity<RangeRevenueReportResponse> getRangeRevenueReport(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to) {
        var report = salesService.generateRangeRevenueReport(from, to);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/range/entries")
    public ResponseEntity<List<RevenueEntryResponse>> getRangeRevenueEntries(
            @RequestParam("from") LocalDate from,
            @RequestParam("to") LocalDate to) {
        var entries = salesService.generateRangeRevenueEntries(from, to);
        return ResponseEntity.ok(entries);
    }
}

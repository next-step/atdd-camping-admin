package com.camping.admin.controller;

import com.camping.admin.dto.DailyRevenueReportResponse;
import com.camping.admin.dto.RangeRevenueReportResponse;
import com.camping.admin.dto.RevenueEntryResponse;
import com.camping.admin.service.RevenueService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reports/revenue")
@RequiredArgsConstructor
public class RevenueAdminController {

    private final RevenueService revenueService;

    @GetMapping("/daily")
    public ResponseEntity<DailyRevenueReportResponse> getDailyRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyRevenueReportResponse report = revenueService.generateDailyRevenueReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/range")
    public ResponseEntity<RangeRevenueReportResponse> getRangeRevenueReport(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        RangeRevenueReportResponse report = revenueService.generateRangeRevenueReport(from, to);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/range/entries")
    public ResponseEntity<List<RevenueEntryResponse>> getRangeRevenueEntries(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<RevenueEntryResponse> entries = revenueService.generateRangeRevenueEntries(from, to);
        return ResponseEntity.ok(entries);
    }
}
package com.camping.admin.controller;

import com.camping.admin.dto.CampsiteRequest;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.service.CampsiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteService campsiteService;

    @GetMapping
    public ResponseEntity<List<CampsiteResponse>> getAllCampsites() {
        List<CampsiteResponse> campsites = campsiteService.getAllCampsites();
        return ResponseEntity.ok(campsites);
    }

    @PostMapping
    public ResponseEntity<CampsiteResponse> createCampsite(@Valid @RequestBody CampsiteRequest request) {
        CampsiteResponse campsite = campsiteService.createCampsite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(campsite);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<CampsiteResponse> updateCampsite(
            @PathVariable Long campsiteId,
            @Valid @RequestBody CampsiteRequest request
    ) {
        CampsiteResponse campsite = campsiteService.updateCampsite(campsiteId, request);
        return ResponseEntity.ok(campsite);
    }
}
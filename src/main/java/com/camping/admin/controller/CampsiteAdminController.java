package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteStatusRequest;
import com.camping.admin.service.CampsiteService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteService campsiteService;

    @GetMapping
    public ResponseEntity<List<Campsite>> getAllCampsites() {
        return ResponseEntity.ok(campsiteService.findAll());
    }

    @PostMapping
    public ResponseEntity<Campsite> createCampsite(@Valid @RequestBody CreateCampsiteRequest request) {
        Campsite saved = campsiteService.createCampsite(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody UpdateCampsiteRequest request) {
        Campsite updated = campsiteService.updateCampsite(campsiteId, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{campsiteId}/status")
    public ResponseEntity<Campsite> updateCampsiteStatus(
            @PathVariable Long campsiteId,
            @RequestBody UpdateCampsiteStatusRequest request) {
        Campsite updated = campsiteService.updateStatus(campsiteId, request.getStatus());
        return ResponseEntity.ok(updated);
    }
}

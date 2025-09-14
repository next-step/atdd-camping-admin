package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.service.CampsiteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteService campsiteService;
    private final CampsiteRepository campsiteRepository;

    @GetMapping
    public ResponseEntity<List<CampsiteResponse>> getAllCampsites() {
        var campsites = campsiteService.getAllCampsites();
        return ResponseEntity.ok(CampsiteResponse.from(campsites));
    }

    @PostMapping
    public ResponseEntity<CampsiteResponse> createCampsite(
        @RequestBody CreateCampsiteRequest request) {
        var saved = campsiteService.create(request);
        return new ResponseEntity<>(CampsiteResponse.from(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
        @PathVariable Long campsiteId,
        @RequestBody UpdateCampsiteRequest request) {
        var updated = campsiteService.update(campsiteId, request);
        return ResponseEntity.ok(updated);
    }
}

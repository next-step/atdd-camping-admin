package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;
import com.camping.admin.service.CampsiteService;
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
    public ResponseEntity<Campsite> createCampsite(@RequestBody CampsiteCreateRequest request) {
        Campsite saved = campsiteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody CampsiteUpdateRequest request) {
        return campsiteService.update(campsiteId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

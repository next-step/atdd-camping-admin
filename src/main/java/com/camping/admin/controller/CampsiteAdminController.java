package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.request.CampsiteRequest;
import com.camping.admin.dto.response.CampsiteResponse;
import com.camping.admin.service.CampsiteService;
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
        List<CampsiteResponse> responses = campsiteService.findAll().stream()
                .map(CampsiteResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CampsiteResponse> createCampsite(@RequestBody CampsiteRequest request) {
        Campsite campsite = campsiteService.create(
                request.siteNumber(),
                request.description(),
                request.maxPeople()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(CampsiteResponse.from(campsite));
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<CampsiteResponse> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody CampsiteRequest request) {
        Campsite campsite = campsiteService.update(
                campsiteId,
                request.siteNumber(),
                request.description(),
                request.maxPeople()
        );

        return ResponseEntity.ok(CampsiteResponse.from(campsite));
    }

    @DeleteMapping("/{campsiteId}")
    public ResponseEntity<Void> deleteCampsite(@PathVariable Long campsiteId) {
        campsiteService.delete(campsiteId);
        return ResponseEntity.noContent().build();
    }
}
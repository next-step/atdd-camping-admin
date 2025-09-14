package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.service.CampsiteService;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<List<Campsite>> getAllCampsites() {
        var result = campsiteService.getAllCampsites();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Campsite> createCampsite(@RequestBody CreateCampsiteRequest request) {
        var saved = campsiteService.create(request);

        if (saved == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
        @PathVariable Long campsiteId,
        @RequestBody Map<String, Object> body) {
        var campsite = campsiteService.update(campsiteId, body);
        return ResponseEntity.ok(campsite);
    }
}

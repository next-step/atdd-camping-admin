package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteDto;
import com.camping.admin.service.CampsiteService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteService campsiteService;

    @GetMapping
    public ResponseEntity<List<CampsiteDto>> getAllCampsites() {
        List<CampsiteDto> result = campsiteService.getAllCampsites();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Campsite> createCampsite(@RequestBody Map<String, Object> body) {
        Campsite campsite = campsiteService.createCampsite(body);
        return new ResponseEntity<>(campsite, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody Map<String, Object> body) {
        Campsite campsite = campsiteService.updateCampsite(campsiteId, body);
        return ResponseEntity.ok(campsite);
    }
}

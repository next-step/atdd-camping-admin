package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;
import java.util.List;

import com.camping.admin.service.CampsiteService;
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
        var responses = campsiteService.getAll();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<Campsite> createCampsite(@RequestBody CampsiteCreateRequest createReq) {
        var response = campsiteService.create(createReq);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody CampsiteUpdateRequest updateReq) {
        var response = campsiteService.update(campsiteId, updateReq);
        return ResponseEntity.ok(response);
    }
}

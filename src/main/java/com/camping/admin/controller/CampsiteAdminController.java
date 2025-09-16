package com.camping.admin.controller;

import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteResponse;
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
    public ResponseEntity<List<CampsiteResponse>> getAllCampsites() {
        List<CampsiteResponse> response = campsiteService.readAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CampsiteResponse> createCampsite(@RequestBody CampsiteCreateRequest request) {
        CampsiteResponse response = campsiteService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<CampsiteResponse> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody CampsiteCreateRequest request) {
        CampsiteResponse response = campsiteService.update(campsiteId, request);
        return ResponseEntity.ok(response);
    }
}

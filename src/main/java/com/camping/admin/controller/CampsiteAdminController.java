package com.camping.admin.controller;

import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteResponse;
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
    public ResponseEntity<List<CampsiteResponse>> getAllCampsites() {
        var responses = campsiteService.getAll();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CampsiteResponse> createCampsite(@RequestBody CampsiteCreateRequest createReq) {
        Long campsiteId = campsiteService.create(createReq);
        var response = campsiteService.get(campsiteId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<CampsiteResponse> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody CampsiteUpdateRequest updateReq) {
        campsiteService.update(campsiteId, updateReq);
        var response = campsiteService.get(campsiteId);
        return ResponseEntity.ok(response);
    }
}

package com.camping.admin.controller;

import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.dto.CampsiteUpdateRequest;
import com.camping.admin.service.CampsiteService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping
    public ResponseEntity<List<CampsiteResponse>> getAllCampsites() {
        return ResponseEntity.ok(campsiteService.findAll());
    }

    @GetMapping("/{campsiteId}")
    public ResponseEntity<CampsiteResponse> getCampsite(@PathVariable Long campsiteId) {
        return ResponseEntity.ok(campsiteService.getCampsiteResponse(campsiteId));
    }

    @PostMapping
    public ResponseEntity<Long> createCampsite(@RequestBody @Valid CampsiteCreateRequest request) {
        return new ResponseEntity<>(campsiteService.createCampsite(request), HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<CampsiteResponse> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody @Valid CampsiteUpdateRequest request) {
        return ResponseEntity.ok(campsiteService.updateCampsite(campsiteId, request));
    }

    @DeleteMapping("/{campsiteId}")
    public ResponseEntity<Void> deleteCampsite(@PathVariable Long campsiteId) {
        campsiteService.deleteCampsite(campsiteId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

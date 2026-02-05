package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.exception.DuplicateSiteNumberException;
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
    public ResponseEntity<List<Campsite>> getAllCampsites() {
        return ResponseEntity.ok(campsiteService.findAll());
    }

    @PostMapping
    public ResponseEntity<Campsite> createCampsite(@RequestBody CreateCampsiteRequest request) {
        Campsite created = campsiteService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody UpdateCampsiteRequest request) {
        Campsite updated = campsiteService.update(campsiteId, request);
        return ResponseEntity.ok(updated);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DuplicateSiteNumberException.class)
    public ResponseEntity<Void> handleDuplicateSiteNumberException(DuplicateSiteNumberException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}

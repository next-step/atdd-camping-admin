package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteDto;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.service.CampsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteService campsiteService;

    @GetMapping
    public ResponseEntity<List<CampsiteDto>> getAllCampsites() {
        List<CampsiteDto> result = campsiteService.findAll().stream()
                .map(CampsiteDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<CampsiteDto> createCampsite(@RequestBody CreateCampsiteRequest request) {
        Campsite result = campsiteService.createCampsite(request);
        return new ResponseEntity<>(CampsiteDto.from(result), HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<CampsiteDto> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody UpdateCampsiteRequest request) {
        Campsite result = campsiteService.updateCampsite(campsiteId, request);
        return ResponseEntity.ok(CampsiteDto.from(result));
    }
}
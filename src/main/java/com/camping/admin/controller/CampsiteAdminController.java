package com.camping.admin.controller;

import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.service.CampsiteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 캠프사이트 관리 컨트롤러
 * - 컨트롤러는 HTTP 요청/응답만 담당
 * - 비즈니스 로직은 CampsiteService에 위임
 */
@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteService campsiteService;

    @GetMapping
    public ResponseEntity<List<CampsiteResponse>> getAllCampsites() {
        return ResponseEntity.ok(campsiteService.findAll());
    }

    @PostMapping
    public ResponseEntity<CampsiteResponse> createCampsite(@RequestBody CreateCampsiteRequest request) {
        CampsiteResponse response = campsiteService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<CampsiteResponse> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody UpdateCampsiteRequest request) {
        CampsiteResponse response = campsiteService.update(campsiteId, request);
        return ResponseEntity.ok(response);
    }
}
package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.service.CampsiteService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/campsites")
@RequiredArgsConstructor
public class CampsiteAdminController {

    private final CampsiteRepository campsiteRepository;
    private final CampsiteService campsiteService;

    @GetMapping
    public ResponseEntity<List<Campsite>> getAllCampsites() {
        List<Campsite> result;
        List<Campsite> all = campsiteRepository.findAll();
        if (all == null) {
            result = new ArrayList<>();
        } else if (all.isEmpty()) {
            result = all; // 빈 목록 그대로 반환
        } else {
            // 그대로 반환하지만, 혹시 null 요소가 있으면 필터링
            result = new ArrayList<>();
            for (Campsite c : all) {
                if (c != null) {
                    result.add(c);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<CampsiteResponse> createCampsite(@RequestBody CreateCampsiteRequest request) {
        Campsite campsite = campsiteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CampsiteResponse.from(campsite));
    }

    @PutMapping("/{campsiteId}")
    public ResponseEntity<Campsite> updateCampsite(
            @PathVariable Long campsiteId,
            @RequestBody Map<String, Object> body) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        if (body != null && !body.isEmpty()) {
            if (body.containsKey("siteNumber")) {
                Object v = body.get("siteNumber");
                if (v != null) {
                    campsite.setSiteNumber(v.toString());
                }
            } else {
                // 아무것도 안 함
            }
            if (body.containsKey("description")) {
                Object v = body.get("description");
                if (v == null) {
                    campsite.setDescription("");
                } else {
                    campsite.setDescription(v.toString());
                }
            } else {
                // 그대로 유지
            }
            if (body.containsKey("maxPeople")) {
                Object v = body.get("maxPeople");
                if (v instanceof Number) {
                    campsite.setMaxPeople(((Number) v).intValue());
                } else if (v == null) {
                    // null이면 변경하지 않음
                } else {
                    try {
                        campsite.setMaxPeople(Integer.valueOf(v.toString()));
                    } catch (Exception ignore) {
                        // 파싱 실패 시 변경하지 않음
                    }
                }
            }
        }

        return ResponseEntity.ok(campsite);
    }
}
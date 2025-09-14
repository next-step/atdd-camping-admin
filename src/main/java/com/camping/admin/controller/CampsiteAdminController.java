package com.camping.admin.controller;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.exception.DuplicateSiteNumberException;
import com.camping.admin.repository.CampsiteRepository;
import java.util.ArrayList;
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

    private final CampsiteRepository campsiteRepository;

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
    public ResponseEntity<Campsite> createCampsite(@RequestBody Map<String, Object> body) {
        String siteNumber;
        if (body.containsKey("siteNumber")) {
            Object v = body.get("siteNumber");
            if (v == null) {
                siteNumber = null;
            } else {
                siteNumber = v.toString();
            }
        } else {
            siteNumber = null;
        }

        String description;
        if (body.containsKey("description")) {
            Object d = body.get("description");
            description = d == null ? "" : d.toString();
        } else {
            description = "";
        }

        Integer maxPeople;
        if (body.containsKey("maxPeople")) {
            Object m = body.get("maxPeople");
            if (m == null) {
                maxPeople = null;
            } else if (m instanceof Number) {
                maxPeople = ((Number) m).intValue();
            } else {
                try {
                    maxPeople = Integer.valueOf(m.toString());
                } catch (Exception e) {
                    maxPeople = null;
                }
            }
        } else {
            maxPeople = null;
        }

        if (siteNumber == null || siteNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("사이트 번호는 필수입니다.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("설명은 필수입니다.");
        }

        if (maxPeople == null || maxPeople <= 0) {
            throw new IllegalArgumentException("최대 인원은 1명 이상이어야 합니다.");
        }

        if (campsiteRepository.existsBySiteNumber(siteNumber)) {
            throw new DuplicateSiteNumberException(siteNumber);
        }

        Campsite newCampsite = new Campsite(siteNumber, description, maxPeople);
        Campsite saved = campsiteRepository.save(newCampsite);
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

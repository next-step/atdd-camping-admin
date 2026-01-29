package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.exception.BusinessException;
import com.camping.admin.repository.CampsiteRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 캠프사이트 서비스
 * - 비즈니스 로직 및 트랜잭션 관리 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<CampsiteResponse> findAll() {
        return campsiteRepository.findAll().stream()
                .map(CampsiteResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CampsiteResponse create(CreateCampsiteRequest request) {
        String description = request.getDescription() != null ? request.getDescription() : "";
        Campsite campsite = new Campsite(
                request.getSiteNumber(),
                description,
                request.getMaxPeople()
        );
        Campsite saved = campsiteRepository.save(campsite);
        return CampsiteResponse.from(saved);
    }

    @Transactional
    public CampsiteResponse update(Long campsiteId, UpdateCampsiteRequest request) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new BusinessException("캠프사이트를 찾을 수 없습니다: " + campsiteId, HttpStatus.NOT_FOUND));

        if (request.getSiteNumber() != null) {
            campsite.setSiteNumber(request.getSiteNumber());
        }
        if (request.getDescription() != null) {
            campsite.setDescription(request.getDescription());
        }
        if (request.getMaxPeople() != null) {
            campsite.setMaxPeople(request.getMaxPeople());
        }

        return CampsiteResponse.from(campsite);
    }
}
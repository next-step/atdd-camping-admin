package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteResponse;
import com.camping.admin.repository.CampsiteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<CampsiteResponse> findAll() {
        return campsiteRepository.findAll().stream()
                .map(CampsiteResponse::from)
                .toList();
    }

    public CampsiteResponse getById(Long campsiteId) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        return CampsiteResponse.from(campsite);
    }

}

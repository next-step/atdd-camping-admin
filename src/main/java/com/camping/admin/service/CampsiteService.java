package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.service.dto.CampsiteCommand;
import com.camping.admin.service.dto.CampsiteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    @Transactional
    public CampsiteResponse create(CampsiteCommand command) {
        if (campsiteRepository.existsBySiteNumber(command.getSiteNumber())) {
            throw new IllegalArgumentException("Already exists site number: " + command.getSiteNumber());
        }

        Campsite campsite = campsiteRepository.save(command.toEntity());
        return CampsiteResponse.from(campsite);
    }
}

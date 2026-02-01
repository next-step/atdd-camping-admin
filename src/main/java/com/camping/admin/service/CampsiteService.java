package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<Campsite> findAll() {
        return campsiteRepository.findAll();
    }

    @Transactional
    public Campsite create(CreateCampsiteRequest request) {
        if (campsiteRepository.existsBySiteNumber(request.getSiteNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 사이트 번호입니다: " + request.getSiteNumber());
        }

        Campsite campsite = request.toEntity();
        return campsiteRepository.save(campsite);
    }
}
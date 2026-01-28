package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CampsiteCreateRequest;
import com.camping.admin.dto.CampsiteUpdateRequest;
import com.camping.admin.repository.CampsiteRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    public List<Campsite> findAll() {
        return campsiteRepository.findAll();
    }

    @Transactional
    public Campsite create(CampsiteCreateRequest request) {
        Campsite campsite = new Campsite(
                request.getSiteNumber(),
                request.getDescription(),
                request.getMaxPeople()
        );
        return campsiteRepository.save(campsite);
    }

    @Transactional
    public Optional<Campsite> update(Long campsiteId, CampsiteUpdateRequest request) {
        return campsiteRepository.findById(campsiteId)
                .map(campsite -> {
                    if (request.getSiteNumber() != null) {
                        campsite.setSiteNumber(request.getSiteNumber());
                    }
                    if (request.getDescription() != null) {
                        campsite.setDescription(request.getDescription());
                    }
                    if (request.getMaxPeople() != null) {
                        campsite.setMaxPeople(request.getMaxPeople());
                    }
                    return campsite;
                });
    }
}

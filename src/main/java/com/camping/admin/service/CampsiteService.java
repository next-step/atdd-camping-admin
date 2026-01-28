package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
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

    public List<Campsite> findAll() {
        return campsiteRepository.findAll();
    }

    public Campsite findById(Long id) {
        return campsiteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + id));
    }

    @Transactional
    public Campsite create(String siteNumber, String description, Integer maxPeople) {
        return campsiteRepository.save(new Campsite(siteNumber, description, maxPeople));
    }

    @Transactional
    public Campsite update(Long campsiteId, String siteNumber, String description, Integer maxPeople) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));;
        campsite.update(siteNumber, description, maxPeople);
        return campsite;
    }
}
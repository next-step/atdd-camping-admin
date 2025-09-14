package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.dto.CreateCampsiteRequest;
import com.camping.admin.dto.UpdateCampsiteRequest;
import com.camping.admin.repository.CampsiteRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampsiteService {

    private final CampsiteRepository campsiteRepository;

    @Transactional(readOnly = true)
    public List<Campsite> getAllCampsites() {
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
        return result;
    }

    @Transactional
    public Campsite create(CreateCampsiteRequest request) {
        var siteNumber = request.getSiteNumber();

        var description = request.getDescription();
        if (description == null) {
            description = "";
        }

        var maxPeople = request.getMaxPeople();

        Campsite newCampsite = new Campsite(siteNumber, description, maxPeople);
        return campsiteRepository.save(newCampsite);
    }


    @Transactional
    public Campsite update(Long campsiteId, UpdateCampsiteRequest request) {
        Campsite campsite = campsiteRepository.findById(campsiteId)
            .orElseThrow(
                () -> new IllegalArgumentException("Cannot find campsite with id: " + campsiteId));

        if (request.getSiteNumber() != null) {
            campsite.setSiteNumber(request.getSiteNumber());
        }

        var description = request.getDescription();
        if (description == null) {
            description = "";
        }
        campsite.setDescription(description);

        if (request.getMaxPeople() != null) {
            campsite.setMaxPeople(request.getMaxPeople());
        }

        return campsiteRepository.save(campsite);
    }
}

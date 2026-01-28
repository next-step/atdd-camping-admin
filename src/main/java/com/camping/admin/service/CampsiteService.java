package com.camping.admin.service;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Campsite campsite = new Campsite(siteNumber, description, maxPeople);
        return campsiteRepository.save(campsite);
    }

    @Transactional
    public Campsite update(Long id, String siteNumber, String description, Integer maxPeople) {
        Campsite campsite = findById(id);
        campsite.update(siteNumber, description, maxPeople);
        return campsite;
    }

    @Transactional
    public void delete(Long id) {
        Campsite campsite = findById(id);

        // 예약이 있으면 삭제 불가
        if (!campsite.getReservations().isEmpty()) {
            throw new IllegalStateException("예약이 있는 캠핑장은 삭제할 수 없습니다");
        }

        campsiteRepository.delete(campsite);
    }
}

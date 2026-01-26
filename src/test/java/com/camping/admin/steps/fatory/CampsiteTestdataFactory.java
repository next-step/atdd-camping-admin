package com.camping.admin.steps.fatory;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.repository.CampsiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CampsiteTestdataFactory {

    private final CampsiteRepository campsiteRepository;

    @Transactional
    public Campsite createCampsite(String siteNumber) {
        return campsiteRepository.save(new Campsite(siteNumber, "Test Description", 4));
    }
}

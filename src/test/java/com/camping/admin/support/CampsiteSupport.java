package com.camping.admin.support;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.steps.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CampsiteSupport {
    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private TestContext testContext;

    @Transactional
    public void 캠핑장_사이트가_등록되어_있다(String siteNumber) {
        Campsite campsite = campsiteRepository.save(new Campsite(siteNumber, "Test Description", 4));
        testContext.setCampsite(campsite);
    }
}

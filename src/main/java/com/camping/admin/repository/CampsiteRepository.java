package com.camping.admin.repository;

import com.camping.admin.domain.entity.Campsite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampsiteRepository extends JpaRepository<Campsite, Long> {}

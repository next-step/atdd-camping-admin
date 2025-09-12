package com.camping.admin.repository;

import com.camping.admin.domain.entity.RentalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRecordRepository extends JpaRepository<RentalRecord, Long> {}

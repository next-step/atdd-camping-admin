package com.camping.admin.repository;

import com.camping.admin.domain.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findById(Long id);

    List<Product> findByNameContaining(String name);
}
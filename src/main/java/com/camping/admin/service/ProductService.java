package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + id));
    }

    @Transactional
    public Product create(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        return productRepository.save(new Product(name, stockQuantity, price, productType));
    }

    @Transactional
    public Product update(Long productId, String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));
        product.update(name, stockQuantity, price, productType);
        return product;
    }
}

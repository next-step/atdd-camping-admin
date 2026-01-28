package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        product.decreaseStock(quantity);
    }

    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        product.increaseStock(quantity);
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public Product create(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        Product product = new Product(name, stockQuantity, price, productType);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        Product product = findById(id);
        product.update(name, stockQuantity, price, productType);
        return product;
    }

}

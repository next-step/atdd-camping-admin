package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.CreateProductRequest;
import com.camping.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        Product product = request.toEntity();
        return productRepository.save(product);
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock for product " + product.getName());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
    }

    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        product.setStockQuantity(product.getStockQuantity() + quantity);
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));
    }
}

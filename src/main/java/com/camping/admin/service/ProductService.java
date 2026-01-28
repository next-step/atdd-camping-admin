package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.dto.ProductUpdateRequest;
import com.camping.admin.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    public Product create(ProductCreateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
        Product product = new Product(
                request.getName(),
                request.getStockQuantity(),
                request.getPrice(),
                request.getProductType()
        );
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> update(Long productId, ProductUpdateRequest request) {
        return productRepository.findById(productId)
                .map(product -> {
                    if (request.getName() != null) {
                        product.setName(request.getName());
                    }
                    if (request.getStockQuantity() != null) {
                        product.setStockQuantity(request.getStockQuantity());
                    }
                    if (request.getPrice() != null) {
                        product.setPrice(request.getPrice());
                    }
                    if (request.getProductType() != null) {
                        product.setProductType(request.getProductType());
                    }
                    return product;
                });
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("재고가 부족합니다: " + product.getName());
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
    }

    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        product.setStockQuantity(product.getStockQuantity() + quantity);
    }

    @Transactional
    public boolean delete(Long productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
    }
}

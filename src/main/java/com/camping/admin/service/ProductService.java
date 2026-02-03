package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.dto.ProductResponse;
import com.camping.admin.dto.ProductUpdateRequest;
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

    // ===== Command =====

    @Transactional
    public Long create(ProductCreateRequest createReq) {
        var newProduct = Product.create(
                createReq.name(),
                createReq.stockQuantity(),
                createReq.price(),
                createReq.productType());
        productRepository.save(newProduct);

        return newProduct.getId();
    }

    @Transactional
    public void update(Long productId, ProductUpdateRequest updateReq) {
        var product = findById(productId);
        product.update(
                updateReq.name(),
                updateReq.stockQuantity(),
                updateReq.price(),
                updateReq.productType());
    }

    // ===== Query =====

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse get(Long productId) {
        var product = findById(productId);
        return ProductResponse.from(product);
    }

    // ===== Helper =====

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));
    }
}

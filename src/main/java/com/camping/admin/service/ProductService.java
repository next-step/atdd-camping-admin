package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.CreateProductRequest;
import com.camping.admin.dto.ProductResponse;
import com.camping.admin.dto.UpdateProductRequest;
import com.camping.admin.exception.BusinessException;
import com.camping.admin.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 서비스
 * - 비즈니스 로직 및 트랜잭션 관리 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Integer stockQuantity = request.getStockQuantity() != null ? request.getStockQuantity() : 0;
        BigDecimal price = request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO;
        ProductType productType = request.getProductType() != null ? request.getProductType() : ProductType.SALE;

        Product product = new Product(request.getName(), stockQuantity, price, productType);
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long productId, UpdateProductRequest request) {
        Product product = findById(productId);

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

        return ProductResponse.from(product);
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("재고가 부족합니다: " + product.getName(), HttpStatus.CONFLICT);
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
                .orElseThrow(() -> new BusinessException("상품을 찾을 수 없습니다: " + productId, HttpStatus.NOT_FOUND));
    }
}

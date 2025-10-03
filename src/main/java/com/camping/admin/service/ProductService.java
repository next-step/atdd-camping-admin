package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.ProductRequest;
import com.camping.admin.dto.ProductResponse;
import com.camping.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getStockQuantity(),
                request.getPrice(),
                request.getProductType()
        );
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = findById(productId);

        product.updateProduct(
                request.getName(),
                request.getStockQuantity(),
                request.getPrice(),
                request.getProductType()
        );

        return ProductResponse.from(product);
    }

    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        product.decreaseStock(quantity);
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));
    }
}

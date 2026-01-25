package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.service.dto.ProductCommand;
import com.camping.admin.service.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        product.setStockQuantity(product.getStockQuantity() + quantity);
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));
    }

    @Transactional
    public Product decreaseStock(Long productId, Integer quantity) {
        Product product = findById(productId);
        product.decreaseStock(quantity);
        return product;
    }

    public ProductResponse create(ProductCommand productCommand) {
        Product product = productRepository.save(productCommand.toEntity());

        return ProductResponse.from(product);
    }
}

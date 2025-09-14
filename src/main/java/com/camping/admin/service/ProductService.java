package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.InsufficientStockException;
import com.camping.admin.exception.ProductNotFoundException;
import com.camping.admin.exception.ProductNotRentalException;
import com.camping.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        product.setStockQuantity(product.getStockQuantity() + quantity);
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Cannot find product with id: " + productId));
    }
}

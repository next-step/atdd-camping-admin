package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.dto.ProductUpdateRequest;
import com.camping.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // ===== Command =====

    @Transactional
    public Product create(ProductCreateRequest createReq) {
        Integer stockQuantity = requireNonNullElse(createReq.stockQuantity(), 0);
        BigDecimal price = requireNonNullElse(createReq.price(), BigDecimal.ZERO);
        ProductType productType = requireNonNullElse(createReq.productType(), ProductType.SALE);

        Product newProduct = new Product(createReq.name(), stockQuantity, price, productType);
        return productRepository.save(newProduct);
    }

    @Transactional
    public Product update(Long productId, ProductUpdateRequest updateReq) {
        Product product = findById(productId);

        String name = requireNonNullElse(updateReq.name(), product.getName());
        Integer stockQuantity = requireNonNullElse(updateReq.stockQuantity(), product.getStockQuantity());
        BigDecimal price = requireNonNullElse(updateReq.price(), product.getPrice());
        ProductType productType = requireNonNullElse(updateReq.productType(), product.getProductType());

        product.setName(name);
        product.setStockQuantity(stockQuantity);
        product.setPrice(price);
        product.setProductType(productType);

        return product;
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

    // ===== Query =====

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product get(Long productId) {
        return findById(productId);
    }

    // ===== Helper =====

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));
    }
}

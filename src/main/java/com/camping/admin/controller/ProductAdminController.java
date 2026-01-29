package com.camping.admin.controller;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.dto.ProductUpdateRequest;
import com.camping.admin.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.requireNonNullElse;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> all = productRepository.findAll();
        List<Product> result = new ArrayList<>();
        if (all != null) {
            for (Product p : all) {
                if (p != null) {
                    result.add(p);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductCreateRequest createReq) {
        Integer stockQuantity = requireNonNullElse(createReq.stockQuantity(), 0);
        BigDecimal price = requireNonNullElse(createReq.price(), BigDecimal.ZERO);
        ProductType productType = requireNonNullElse(createReq.productType(), ProductType.SALE);

        Product newProduct = new Product(createReq.name(), stockQuantity, price, productType);
        Product saved = productRepository.save(newProduct);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest updateReq) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));

        String name = requireNonNullElse(updateReq.name(), product.getName());
        Integer stockQuantity = requireNonNullElse(updateReq.stockQuantity(), product.getStockQuantity());
        BigDecimal price = requireNonNullElse(updateReq.price(), product.getPrice());
        ProductType productType = requireNonNullElse(updateReq.productType(), product.getProductType());

        product.setName(name);
        product.setStockQuantity(stockQuantity);
        product.setPrice(price);
        product.setProductType(productType);

        productRepository.save(product);

        return ResponseEntity.ok(product);
    }
}
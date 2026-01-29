package com.camping.admin.controller;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.dto.request.ProductRequest;
import com.camping.admin.dto.response.ProductResponse;
import com.camping.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productService.findAll().stream()
                .map(ProductResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        Product product = productService.create(
                request.name(),
                request.stockQuantity(),
                request.price(),
                request.productType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequest request) {
        Product product = productService.update(
                productId,
                request.name(),
                request.stockQuantity(),
                request.price(),
                request.productType()
        );
        return ResponseEntity.ok(ProductResponse.from(product));
    }
}
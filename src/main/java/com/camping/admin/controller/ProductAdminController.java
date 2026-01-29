package com.camping.admin.controller;

import com.camping.admin.dto.CreateProductRequest;
import com.camping.admin.dto.ProductResponse;
import com.camping.admin.dto.UpdateProductRequest;
import com.camping.admin.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 관리 컨트롤러
 * - 컨트롤러는 HTTP 요청/응답만 담당
 * - 비즈니스 로직은 ProductService에 위임
 */
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        ProductResponse response = productService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.update(productId, request);
        return ResponseEntity.ok(response);
    }
}
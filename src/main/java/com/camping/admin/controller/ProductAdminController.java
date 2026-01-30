package com.camping.admin.controller;

import com.camping.admin.dto.ProductCreateRequest;
import com.camping.admin.dto.ProductResponse;
import com.camping.admin.dto.ProductUpdateRequest;
import java.util.List;

import com.camping.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        var responses = productService.getAll();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductCreateRequest createReq) {
        Long productId = productService.create(createReq);
        var response = productService.get(productId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest updateReq) {
        productService.update(productId, updateReq);
        var response = productService.get(productId);
        return ResponseEntity.ok(response);
    }
}
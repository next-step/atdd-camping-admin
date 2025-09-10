package com.camping.admin.controller;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.exception.EntityNotFoundException;
import com.camping.admin.exception.ProductConflictException;
import com.camping.admin.exception.ValidationException;
import com.camping.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Map<String, Object> body) {
        try {
            Product product = productService.createProduct(body);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (ProductConflictException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } catch (ValidationException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> body) {
        try {
            Product product = productService.updateProduct(productId, body);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

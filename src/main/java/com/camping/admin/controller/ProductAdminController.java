package com.camping.admin.controller;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.CreateProductRequest;
import com.camping.admin.dto.ProductResponse;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.service.ProductService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductRepository productRepository;
    private final ProductService productService;

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
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> body) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + productId));

        if (body != null) {
            if (body.containsKey("name")) {
                Object v = body.get("name");
                if (v != null) {
                    product.setName(v.toString());
                }
            }
            if (body.containsKey("stockQuantity")) {
                Object v = body.get("stockQuantity");
                if (v instanceof Number) {
                    product.setStockQuantity(((Number) v).intValue());
                } else if (v != null) {
                    try {
                        product.setStockQuantity(Integer.valueOf(v.toString()));
                    } catch (Exception ignore) {
                    }
                }
            }
            if (body.containsKey("price")) {
                Object v = body.get("price");
                if (v instanceof Number) {
                    product.setPrice(new BigDecimal(((Number) v).toString()));
                } else if (v != null) {
                    try {
                        product.setPrice(new BigDecimal(v.toString()));
                    } catch (Exception ignore) {
                    }
                }
            }
            if (body.containsKey("productType")) {
                Object v = body.get("productType");
                if (v != null) {
                    try {
                        product.setProductType(ProductType.valueOf(v.toString()));
                    } catch (Exception ignore) {
                    }
                }
            }
        }

        return ResponseEntity.ok(product);
    }
}
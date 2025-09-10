package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.EntityNotFoundException;
import com.camping.admin.exception.ProductConflictException;
import com.camping.admin.exception.ValidationException;
import com.camping.admin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
        product.increaseStock(quantity);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct(Map<String, Object> body) {
        String name = Product.extractName(body);
        Integer stockQuantity = Product.extractStockQuantity(body);
        BigDecimal price = Product.extractPrice(body);
        ProductType productType = Product.extractProductType(body);

        validateStockQuantity(stockQuantity);
        validatePrice(price);
        return createAndSaveProduct(name, stockQuantity, price, productType);
    }

    private void validateStockQuantity(Integer stockQuantity) {
        if (stockQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Price cannot be negative");
        }
    }

    private Product createAndSaveProduct(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        try {
            Product newProduct = new Product(name, stockQuantity, price, productType);
            return productRepository.save(newProduct);
        } catch (DataIntegrityViolationException e) {
            throw new ProductConflictException("Product creation failed due to data integrity violation");
        }
    }

    @Transactional
    public Product updateProduct(Long productId, Map<String, Object> body) {
        Product product = findById(productId);
        product.updateFromMap(body);
        return product;
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find product with id: " + productId));
    }


    @Transactional
    public Product createProductFromWebForm(Map<String, String> params) {
        String name = extractNameFromForm(params);
        Integer stockQuantity = extractStockQuantityFromForm(params);
        BigDecimal price = extractPriceFromForm(params);
        ProductType type = extractProductTypeFromForm(params);

        return createProductEntity(name, stockQuantity, price, type);
    }

    private Product createProductEntity(String name, Integer stockQuantity, BigDecimal price, ProductType type) {
        Product entity = new Product(name, stockQuantity, price, type);
        return productRepository.save(entity);
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find product with id: " + id));
    }

    @Transactional
    public Product updateProductFromWebForm(Long id, Map<String, String> params) {
        Product product = findProductById(id);
        updateProductFromForm(product, params);
        return product;
    }

    private String extractNameFromForm(Map<String, String> params) {
        return params.getOrDefault("name", null);
    }

    private Integer extractStockQuantityFromForm(Map<String, String> params) {
        try {
            return params.containsKey("stockQuantity") ? Integer.valueOf(params.get("stockQuantity")) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private BigDecimal extractPriceFromForm(Map<String, String> params) {
        try {
            return params.containsKey("price") ? new BigDecimal(params.get("price")) : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private ProductType extractProductTypeFromForm(Map<String, String> params) {
        try {
            return params.containsKey("productType") ? ProductType.valueOf(params.get("productType")) : ProductType.SALE;
        } catch (Exception e) {
            return ProductType.SALE;
        }
    }

    private void updateProductFromForm(Product product, Map<String, String> params) {
        updateNameFromForm(product, params);
        updateStockQuantityFromForm(product, params);
        updatePriceFromForm(product, params);
        updateProductTypeFromForm(product, params);
    }

    private void updateNameFromForm(Product product, Map<String, String> params) {
        if (params.containsKey("name")) {
            product.setName(params.get("name"));
        }
    }

    private void updateStockQuantityFromForm(Product product, Map<String, String> params) {
        if (params.containsKey("stockQuantity")) {
            try {
                product.setStockQuantity(Integer.valueOf(params.get("stockQuantity")));
            } catch (Exception e) {
                throw new ValidationException("Invalid stock quantity format during form update: " + params.get("stockQuantity"));
            }
        }
    }

    private void updatePriceFromForm(Product product, Map<String, String> params) {
        if (params.containsKey("price")) {
            try {
                product.setPrice(new BigDecimal(params.get("price")));
            } catch (Exception e) {
                throw new ValidationException("Invalid price format during form update: " + params.get("price"));
            }
        }
    }

    private void updateProductTypeFromForm(Product product, Map<String, String> params) {
        if (params.containsKey("productType")) {
            try {
                product.setProductType(ProductType.valueOf(params.get("productType")));
            } catch (Exception e) {
                throw new ValidationException("Invalid product type during form update: " + params.get("productType"));
            }
        }
    }
}

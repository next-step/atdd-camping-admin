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

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct(Map<String, Object> body) {
        String name = extractName(body);
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }

        Integer stockQuantity = extractStockQuantity(body);
        BigDecimal price = extractPrice(body);
        ProductType productType = extractProductType(body);

        if (stockQuantity < 0) {
            throw new ValidationException("Stock quantity cannot be negative");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Price cannot be negative");
        }

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

        if (body != null) {
            updateName(product, body);
            updateStockQuantity(product, body);
            updatePrice(product, body);
            updateProductType(product, body);
        }

        return product;
    }

    private Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find product with id: " + productId));
    }

    private String extractName(Map<String, Object> body) {
        if (body.containsKey("name")) {
            Object v = body.get("name");
            return v == null ? null : v.toString();
        }
        return null;
    }

    private Integer extractStockQuantity(Map<String, Object> body) {
        if (body.containsKey("stockQuantity")) {
            Object v = body.get("stockQuantity");
            if (v instanceof Number) {
                return ((Number) v).intValue();
            } else if (v == null) {
                return 0;
            } else {
                try {
                    return Integer.valueOf(v.toString());
                } catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private BigDecimal extractPrice(Map<String, Object> body) {
        if (body.containsKey("price")) {
            Object v = body.get("price");
            if (v instanceof Number) {
                return new BigDecimal(((Number) v).toString());
            } else if (v == null) {
                return BigDecimal.ZERO;
            } else {
                try {
                    return new BigDecimal(v.toString());
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private ProductType extractProductType(Map<String, Object> body) {
        if (body.containsKey("productType")) {
            Object v = body.get("productType");
            if (v == null) {
                return ProductType.SALE;
            } else {
                try {
                    return ProductType.valueOf(v.toString());
                } catch (Exception e) {
                    return ProductType.SALE;
                }
            }
        }
        return ProductType.SALE;
    }

    private void updateName(Product product, Map<String, Object> body) {
        if (body.containsKey("name")) {
            Object v = body.get("name");
            if (v != null) {
                product.setName(v.toString());
            }
        }
    }

    private void updateStockQuantity(Product product, Map<String, Object> body) {
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
    }

    private void updatePrice(Product product, Map<String, Object> body) {
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
    }

    private void updateProductType(Product product, Map<String, Object> body) {
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
}

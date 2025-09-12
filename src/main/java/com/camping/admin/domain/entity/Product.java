package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    public Product(String name, Integer stockQuantity, BigDecimal price, ProductType productType) {
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.productType = productType;
    }

    public static Product from(Map<String, Object> body) {
        String name = extractName(body);
        Integer stockQuantity = extractStockQuantity(body);
        BigDecimal price = extractPrice(body);
        ProductType productType = extractProductType(body);
        return new Product(name, stockQuantity, price, productType);
    }

    private static String extractName(Map<String, Object> body) {
        if (body.containsKey("name")) {
            Object value = body.get("name");
            if (value == null) {
                throw new ValidationException("Product name cannot be null");
            }
            return value.toString();
        }
        throw new ValidationException("Product name is required");
    }

    private static Integer extractStockQuantity(Map<String, Object> body) {
        if (body.containsKey("stockQuantity")) {
            Object value = body.get("stockQuantity");
            return parseIntegerValue(value);
        }
        return 0;
    }

    private static Integer parseIntegerValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value == null) {
            return 0;
        } else {
            return parseStringToInteger(value.toString());
        }
    }

    private static Integer parseStringToInteger(String stringValue) {
        try {
            return Integer.valueOf(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid stock quantity format: " + stringValue);
        }
    }

    private static BigDecimal extractPrice(Map<String, Object> body) {
        if (body.containsKey("price")) {
            Object value = body.get("price");
            return parseBigDecimalValue(value);
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal parseBigDecimalValue(Object value) {
        if (value instanceof Number) {
            return new BigDecimal(((Number) value).toString());
        } else if (value == null) {
            return BigDecimal.ZERO;
        } else {
            return parseStringToBigDecimal(value.toString());
        }
    }

    private static BigDecimal parseStringToBigDecimal(String stringValue) {
        try {
            return new BigDecimal(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid price format: " + stringValue);
        }
    }

    private static ProductType extractProductType(Map<String, Object> body) {
        if (body.containsKey("productType")) {
            Object value = body.get("productType");
            return parseProductTypeValue(value);
        }
        return ProductType.SALE;
    }

    private static ProductType parseProductTypeValue(Object value) {
        if (value == null) {
            return ProductType.SALE;
        } else {
            return parseStringToProductType(value.toString());
        }
    }

    private static ProductType parseStringToProductType(String stringValue) {
        try {
            return ProductType.valueOf(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid product type: " + stringValue);
        }
    }

    public void decreaseStock(Integer quantity) {
        validateQuantity(quantity);
        if (this.stockQuantity < quantity) {
            throw new ValidationException("Not enough stock for product " + this.name);
        }
        this.stockQuantity = this.stockQuantity - quantity;
    }

    public void increaseStock(Integer quantity) {
        validateQuantity(quantity);
        this.stockQuantity = this.stockQuantity + quantity;
    }

    public boolean isRentalType() {
        return ProductType.RENTAL.equals(this.productType);
    }

    public BigDecimal calculateTotalPrice(Integer quantity) {
        validateQuantity(quantity);
        return this.price.multiply(new BigDecimal(quantity));
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("Quantity must be greater than 0");
        }
    }

    public void updateFromMap(Map<String, Object> body) {
        if (body == null) {
            return;
        }
        updateName(body);
        updateStockQuantity(body);
        updatePrice(body);
        updateProductType(body);
    }

    private void updateName(Map<String, Object> body) {
        if (body.containsKey("name")) {
            Object value = body.get("name");
            if (value != null) {
                this.name = value.toString();
            }
        }
    }

    private void updateStockQuantity(Map<String, Object> body) {
        if (body.containsKey("stockQuantity")) {
            Object value = body.get("stockQuantity");
            updateStockQuantityFromValue(value);
        }
    }

    private void updateStockQuantityFromValue(Object value) {
        if (value instanceof Number) {
            this.stockQuantity = ((Number) value).intValue();
        } else if (value != null) {
            updateStockQuantityFromString(value.toString());
        }
    }

    private void updateStockQuantityFromString(String stringValue) {
        try {
            this.stockQuantity = Integer.valueOf(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid stock quantity format during update: " + stringValue);
        }
    }

    private void updatePrice(Map<String, Object> body) {
        if (body.containsKey("price")) {
            Object value = body.get("price");
            updatePriceFromValue(value);
        }
    }

    private void updatePriceFromValue(Object value) {
        if (value instanceof Number) {
            this.price = new BigDecimal(((Number) value).toString());
        } else if (value != null) {
            updatePriceFromString(value.toString());
        }
    }

    private void updatePriceFromString(String stringValue) {
        try {
            this.price = new BigDecimal(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid price format during update: " + stringValue);
        }
    }

    private void updateProductType(Map<String, Object> body) {
        if (body.containsKey("productType")) {
            Object value = body.get("productType");
            updateProductTypeFromValue(value);
        }
    }

    private void updateProductTypeFromValue(Object value) {
        if (value != null) {
            updateProductTypeFromString(value.toString());
        }
    }

    private void updateProductTypeFromString(String stringValue) {
        try {
            this.productType = ProductType.valueOf(stringValue);
        } catch (Exception e) {
            throw new ValidationException("Invalid product type during update: " + stringValue);
        }
    }

}

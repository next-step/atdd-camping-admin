package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 수정 요청 DTO
 * - Map<String, Object> 대신 타입 안전한 DTO 사용
 */
@Getter
@NoArgsConstructor
public class UpdateProductRequest {

    private String name;
    private Integer stockQuantity;
    private BigDecimal price;
    private ProductType productType;
}
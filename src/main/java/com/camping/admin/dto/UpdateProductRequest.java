package com.camping.admin.dto;

import com.camping.admin.domain.enums.ProductType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProductRequest {

    @Size(min = 1, message = "상품명은 빈 값일 수 없습니다")
    private String name;

    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    private Integer stockQuantity;

    @PositiveOrZero(message = "가격은 0 이상이어야 합니다")
    private BigDecimal price;

    private ProductType productType;
}
package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaleItemResponse {
    private Long productId;
    private Integer quantity;
}
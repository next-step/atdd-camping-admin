package com.camping.admin.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProcessSaleRequest {
    private List<SaleItemResponse> items;
}
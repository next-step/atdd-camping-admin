package com.camping.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProcessSaleRequest {
    private List<SaleItemResponse> items;
}

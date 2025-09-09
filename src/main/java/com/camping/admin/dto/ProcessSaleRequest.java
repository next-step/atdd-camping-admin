package com.camping.admin.dto;

import java.util.List;

public record ProcessSaleRequest(List<SaleItemResponse> items) {}

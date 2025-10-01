package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.ProcessSaleRequest;
import com.camping.admin.dto.SaleItemResponse;
import com.camping.admin.dto.SalesRecordResponse;
import com.camping.admin.domain.constants.BusinessConstants;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.SalesRecordRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesService {

    private final ProductRepository productRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final ProductService productService;

    @Transactional
    public void processSale(ProcessSaleRequest request) {
        for (SaleItemResponse itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + itemDto.getProductId()));

            BigDecimal totalPrice = product.calculateTotalPrice(itemDto.getQuantity());
            productService.decreaseStock(itemDto.getProductId(), itemDto.getQuantity());

            SalesRecord salesRecord = new SalesRecord(product, itemDto.getQuantity(), totalPrice);
            salesRecordRepository.save(salesRecord);
        }
    }

    public List<SalesRecordResponse> findRecentSales(int limit) {
        return salesRecordRepository.findRecentSales(PageRequest.of(BusinessConstants.FIRST_PAGE, limit)).stream()
                .map(SalesRecordResponse::from)
                .toList();
    }
}
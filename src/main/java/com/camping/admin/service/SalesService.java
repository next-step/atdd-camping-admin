package com.camping.admin.service;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.dto.ProcessSaleRequest;
import com.camping.admin.dto.SaleItemResponse;
import com.camping.admin.dto.SalesRecordResponse;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.SalesRecordRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesService {

    private final ProductRepository productRepository;
    private final SalesRecordRepository salesRecordRepository;

    @Transactional
    public void processSale(ProcessSaleRequest request) {
        for (SaleItemResponse itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find product with id: " + itemDto.getProductId()));
            
            // 도메인 로직 위임: 재고 감소
            product.decreaseStock(itemDto.getQuantity());
            
            // 도메인 로직 위임: 총액 계산은 SalesRecord 생성자 내부에서 처리
            SalesRecord salesRecord = new SalesRecord(product, itemDto.getQuantity());
            salesRecordRepository.save(salesRecord);
        }
    }

    public List<SalesRecordResponse> findRecentSales(int limit) {
        return salesRecordRepository.findAll().stream()
                .sorted(Comparator.comparing(SalesRecord::getCreatedAt).reversed())
                .limit(limit)
                .map(SalesRecordResponse::from)
                .toList();
    }
}

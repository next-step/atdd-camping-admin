package com.camping.admin.service;

import com.camping.admin.domain.event.ProductStockDecreasedEvent;
import com.camping.admin.domain.event.ProductStockIncreasedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockEventHandler {

    private final ProductService productService;

    @EventListener
    @Transactional
    public void handleProductStockDecreased(ProductStockDecreasedEvent event) {
        log.info("Processing ProductStockDecreasedEvent: productId={}, quantity={}",
                event.getProductId(), event.getQuantity().getQuantity());

        productService.decreaseStock(event.getProductId(), event.getQuantity());

        log.info("Successfully decreased stock for product {}", event.getProductId());
    }

    @EventListener
    @Transactional
    public void handleProductStockIncreased(ProductStockIncreasedEvent event) {
        log.info("Processing ProductStockIncreasedEvent: productId={}, quantity={}",
                event.getProductId(), event.getQuantity().getQuantity());

        productService.increaseStock(event.getProductId(), event.getQuantity());

        log.info("Successfully increased stock for product {}", event.getProductId());
    }
}
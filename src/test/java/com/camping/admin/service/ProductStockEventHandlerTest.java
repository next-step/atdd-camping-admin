package com.camping.admin.service;

import com.camping.admin.domain.event.ProductStockDecreasedEvent;
import com.camping.admin.domain.event.ProductStockIncreasedEvent;
import com.camping.admin.domain.vo.RecordQuantity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductStockEventHandlerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductStockEventHandler productStockEventHandler;

    @Test
    @DisplayName("재고 감소 이벤트 처리 시 ProductService의 decreaseStock이 호출된다")
    void 재고_감소_이벤트_처리_시_ProductService의_decreaseStock이_호출된다() {
        // given
        Long productId = 1L;
        Integer quantity = 2;
        ProductStockDecreasedEvent event = new ProductStockDecreasedEvent(productId, quantity);

        // when
        productStockEventHandler.handleProductStockDecreased(event);

        // then
        then(productService).should().decreaseStock(productId, new RecordQuantity(quantity));
    }

    @Test
    @DisplayName("재고 증가 이벤트 처리 시 ProductService의 increaseStock이 호출된다")
    void 재고_증가_이벤트_처리_시_ProductService의_increaseStock이_호출된다() {
        // given
        Long productId = 2L;
        Integer quantity = 1;
        ProductStockIncreasedEvent event = new ProductStockIncreasedEvent(productId, quantity);

        // when
        productStockEventHandler.handleProductStockIncreased(event);

        // then
        then(productService).should().increaseStock(productId, new RecordQuantity(quantity));
    }

    @Test
    @DisplayName("최소 수량(1)의 재고 감소 이벤트도 정상 처리된다")
    void 최소_수량의_재고_감소_이벤트도_정상_처리된다() {
        // given
        Long productId = 3L;
        Integer quantity = 1;
        ProductStockDecreasedEvent event = new ProductStockDecreasedEvent(productId, quantity);

        // when
        productStockEventHandler.handleProductStockDecreased(event);

        // then
        then(productService).should().decreaseStock(productId, new RecordQuantity(quantity));
    }

    @Test
    @DisplayName("대량 수량의 재고 증가 이벤트도 정상 처리된다")
    void 대량_수량의_재고_증가_이벤트도_정상_처리된다() {
        // given
        Long productId = 4L;
        Integer quantity = 100;
        ProductStockIncreasedEvent event = new ProductStockIncreasedEvent(productId, quantity);

        // when
        productStockEventHandler.handleProductStockIncreased(event);

        // then
        then(productService).should().increaseStock(productId, new RecordQuantity(quantity));
    }
}
package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.domain.event.ProductStockDecreasedEvent;
import com.camping.admin.domain.event.ProductStockIncreasedEvent;
import com.camping.admin.exception.RentalAlreadyReturnedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class RentalRecordTest {

    @Test
    @DisplayName("대여 기록 생성 시 재고 감소 이벤트가 발행된다")
    void 대여_기록_생성_시_재고_감소_이벤트가_발행된다() {
        // given
        Product product = new Product("랜턴", 10, new BigDecimal("30000"), ProductType.RENTAL);
        product.setId(1L); // 테스트용 ID 설정
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("홍길동", null, null, campsite);

        // when
        RentalRecord rentalRecord = new RentalRecord(reservation, product, 2);

        // then
        assertThat(rentalRecord.getDomainEvents()).hasSize(1);
        Object firstEvent = rentalRecord.getDomainEvents().iterator().next();
        assertThat(firstEvent)
                .isInstanceOf(ProductStockDecreasedEvent.class);

        ProductStockDecreasedEvent event = (ProductStockDecreasedEvent) rentalRecord.getDomainEvents().iterator().next();
        assertThat(event.getProductId()).isEqualTo(1L);
        assertThat(event.getQuantity()).isEqualTo(2);
        assertThat(event.getOccurredOn()).isNotNull();
    }

    @Test
    @DisplayName("반납 처리 시 재고 증가 이벤트가 발행된다")
    void 반납_처리_시_재고_증가_이벤트가_발행된다() {
        // given
        Product product = new Product("코펠 세트", 15, new BigDecimal("20000"), ProductType.RENTAL);
        product.setId(2L); // 테스트용 ID 설정
        RentalRecord rentalRecord = new RentalRecord(null, product, 1);
        rentalRecord.clearEvents(); // 생성 시 이벤트 초기화

        // when
        rentalRecord.returnProduct();

        // then
        assertThat(rentalRecord.getIsReturned()).isTrue();
        assertThat(rentalRecord.getDomainEvents()).hasSize(1);
        Object firstEvent = rentalRecord.getDomainEvents().iterator().next();
        assertThat(firstEvent)
                .isInstanceOf(ProductStockIncreasedEvent.class);

        ProductStockIncreasedEvent event = (ProductStockIncreasedEvent) rentalRecord.getDomainEvents().iterator().next();
        assertThat(event.getProductId()).isEqualTo(2L);
        assertThat(event.getQuantity()).isEqualTo(1);
        assertThat(event.getOccurredOn()).isNotNull();
    }

    @Test
    @DisplayName("이미 반납된 상품을 다시 반납하면 예외가 발생하고 이벤트가 발행되지 않는다")
    void 이미_반납된_상품을_다시_반납하면_예외가_발생하고_이벤트가_발행되지_않는다() {
        // given
        Product product = new Product("의자", 5, new BigDecimal("15000"), ProductType.RENTAL);
        RentalRecord rentalRecord = new RentalRecord(null, product, 1);
        rentalRecord.returnProduct(); // 첫 번째 반납
        rentalRecord.clearEvents(); // 이벤트 초기화

        // when & then
        assertThatThrownBy(() -> rentalRecord.returnProduct())
                .isInstanceOf(RentalAlreadyReturnedException.class)
                .hasMessage("This item has already been returned.");

        // 예외 발생 시 이벤트가 발행되지 않아야 함
        assertThat(rentalRecord.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("워크인 대여도 정상적으로 이벤트를 발행한다")
    void 워크인_대여도_정상적으로_이벤트를_발행한다() {
        // given
        Product product = new Product("테이블", 3, new BigDecimal("25000"), ProductType.RENTAL);
        product.setId(3L);

        // when - reservation 없이 워크인 대여
        RentalRecord walkinRental = new RentalRecord(null, product, 1);

        // then
        assertThat(walkinRental.getReservation()).isNull();
        assertThat(walkinRental.getDomainEvents()).hasSize(1);

        ProductStockDecreasedEvent event = (ProductStockDecreasedEvent) walkinRental.getDomainEvents().iterator().next();
        assertThat(event.getProductId()).isEqualTo(3L);
        assertThat(event.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("대여 기록 생성과 반납을 연속으로 수행하면 두 개의 이벤트가 순서대로 발행된다")
    void 대여_기록_생성과_반납을_연속으로_수행하면_두_개의_이벤트가_순서대로_발행된다() {
        // given
        Product product = new Product("버너", 8, new BigDecimal("18000"), ProductType.RENTAL);
        product.setId(4L);

        // when
        RentalRecord rentalRecord = new RentalRecord(null, product, 2);
        rentalRecord.returnProduct();

        // then
        assertThat(rentalRecord.getDomainEvents()).hasSize(2);

        // 첫 번째: 재고 감소 이벤트 (대여 시)
        Object firstEvent = rentalRecord.getDomainEvents().iterator().next();
        assertThat(firstEvent)
                .isInstanceOf(ProductStockDecreasedEvent.class);
        ProductStockDecreasedEvent decreaseEvent = (ProductStockDecreasedEvent) rentalRecord.getDomainEventsList().get(0);
        assertThat(decreaseEvent.getProductId()).isEqualTo(4L);
        assertThat(decreaseEvent.getQuantity()).isEqualTo(2);

        // 두 번째: 재고 증가 이벤트 (반납 시)
        assertThat(rentalRecord.getDomainEventsList().get(1))
                .isInstanceOf(ProductStockIncreasedEvent.class);
        ProductStockIncreasedEvent increaseEvent = (ProductStockIncreasedEvent) rentalRecord.getDomainEventsList().get(1);
        assertThat(increaseEvent.getProductId()).isEqualTo(4L);
        assertThat(increaseEvent.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("이벤트 초기화 후에는 도메인 이벤트가 비어있다")
    void 이벤트_초기화_후에는_도메인_이벤트가_비어있다() {
        // given
        Product product = new Product("취사도구 세트", 12, new BigDecimal("12000"), ProductType.RENTAL);
        RentalRecord rentalRecord = new RentalRecord(null, product, 1);

        // when
        assertThat(rentalRecord.getDomainEvents()).hasSize(1); // 이벤트 있음 확인
        rentalRecord.clearEvents(); // 이벤트 초기화

        // then
        assertThat(rentalRecord.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("대여 기록의 기본 속성들이 올바르게 설정된다")
    void 대여_기록의_기본_속성들이_올바르게_설정된다() {
        // given
        Product product = new Product("캠핑 랜턴", 20, new BigDecimal("35000"), ProductType.RENTAL);
        Campsite campsite = new Campsite();
        Reservation reservation = new Reservation("김철수", null, null, campsite);

        // when
        RentalRecord rentalRecord = new RentalRecord(reservation, product, 3);

        // then
        assertThat(rentalRecord.getReservation()).isEqualTo(reservation);
        assertThat(rentalRecord.getProduct()).isEqualTo(product);
        assertThat(rentalRecord.getQuantity().getQuantity()).isEqualTo(3);
        assertThat(rentalRecord.getIsReturned()).isFalse();
        assertThat(rentalRecord.getCreatedAt()).isNotNull();
        assertThat(rentalRecord.getId()).isNull(); // JPA 저장 전이므로 null
    }
}
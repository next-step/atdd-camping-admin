package com.camping.admin.domain.vo;

import com.camping.admin.exception.InsufficientStockException;
import com.camping.admin.exception.InvalidQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StockQuantityTest {

    @Test
    @DisplayName("유효한 재고 수량으로 StockQuantity를 생성할 수 있다")
    void 유효한_재고_수량으로_StockQuantity를_생성할_수_있다() {
        // given
        int validStock = 10;

        // when
        StockQuantity stockQuantity = new StockQuantity(validStock);

        // then
        assertThat(stockQuantity.getQuantity()).isEqualTo(validStock);
    }

    @Test
    @DisplayName("0 재고로 StockQuantity를 생성할 수 있다")
    void 영_재고로_StockQuantity를_생성할_수_있다() {
        // given
        int zeroStock = 0;

        // when
        StockQuantity stockQuantity = new StockQuantity(zeroStock);

        // then
        assertThat(stockQuantity.getQuantity()).isEqualTo(zeroStock);
        assertThat(stockQuantity.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("음수 재고로 StockQuantity 생성 시 예외가 발생한다")
    void 음수_재고로_StockQuantity_생성_시_예외가_발생한다() {
        // given
        int negativeStock = -1;

        // when & then
        assertThatThrownBy(() -> new StockQuantity(negativeStock))
                .isInstanceOf(InvalidQuantityException.class)
                .hasMessage("Stock quantity cannot be negative. Provided: " + negativeStock);
    }

    @Test
    @DisplayName("재고 감소가 가능한지 확인할 수 있다")
    void 재고_감소가_가능한지_확인할_수_있다() {
        // given
        StockQuantity stockQuantity = new StockQuantity(10);
        RecordQuantity validDecrease = new RecordQuantity(5);
        RecordQuantity invalidDecrease = new RecordQuantity(15);

        // when & then
        assertThat(stockQuantity.canDecrease(validDecrease)).isTrue();
        assertThat(stockQuantity.canDecrease(invalidDecrease)).isFalse();
    }

    @Test
    @DisplayName("충분한 재고가 있을 때 재고를 감소시킬 수 있다")
    void 충분한_재고가_있을_때_재고를_감소시킬_수_있다() {
        // given
        StockQuantity stockQuantity = new StockQuantity(10);
        RecordQuantity decreaseAmount = new RecordQuantity(3);

        // when
        StockQuantity result = stockQuantity.decrease(decreaseAmount);

        // then
        assertThat(result.getQuantity()).isEqualTo(7);
        assertThat(stockQuantity.getQuantity()).isEqualTo(10); // 원본은 불변
    }

    @Test
    @DisplayName("재고가 부족할 때 재고 감소 시 예외가 발생한다")
    void 재고가_부족할_때_재고_감소_시_예외가_발생한다() {
        // given
        StockQuantity stockQuantity = new StockQuantity(3);
        RecordQuantity decreaseAmount = new RecordQuantity(5);

        // when & then
        assertThatThrownBy(() -> stockQuantity.decrease(decreaseAmount))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Insufficient stock. Current: 3, Required: 5");
    }

    @Test
    @DisplayName("재고를 증가시킬 수 있다")
    void 재고를_증가시킬_수_있다() {
        // given
        StockQuantity stockQuantity = new StockQuantity(5);
        RecordQuantity increaseAmount = new RecordQuantity(3);

        // when
        StockQuantity result = stockQuantity.increase(increaseAmount);

        // then
        assertThat(result.getQuantity()).isEqualTo(8);
        assertThat(stockQuantity.getQuantity()).isEqualTo(5); // 원본은 불변
    }

    @Test
    @DisplayName("0 재고에서도 증가시킬 수 있다")
    void 영_재고에서도_증가시킬_수_있다() {
        // given
        StockQuantity emptyStock = new StockQuantity(0);
        RecordQuantity increaseAmount = new RecordQuantity(5);

        // when
        StockQuantity result = emptyStock.increase(increaseAmount);

        // then
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("재고가 비어있는지 확인할 수 있다")
    void 재고가_비어있는지_확인할_수_있다() {
        // given
        StockQuantity emptyStock = new StockQuantity(0);
        StockQuantity availableStock = new StockQuantity(10);

        // when & then
        assertThat(emptyStock.isEmpty()).isTrue();
        assertThat(availableStock.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("특정 수량이 사용 가능한지 확인할 수 있다")
    void 특정_수량이_사용_가능한지_확인할_수_있다() {
        // given
        StockQuantity stockQuantity = new StockQuantity(10);
        RecordQuantity availableAmount = new RecordQuantity(5);
        RecordQuantity unavailableAmount = new RecordQuantity(15);

        // when & then
        assertThat(stockQuantity.isAvailable(availableAmount)).isTrue();
        assertThat(stockQuantity.isAvailable(unavailableAmount)).isFalse();
    }

    @Test
    @DisplayName("두 StockQuantity를 더할 수 있다")
    void 두_StockQuantity를_더할_수_있다() {
        // given
        StockQuantity stock1 = new StockQuantity(5);
        StockQuantity stock2 = new StockQuantity(3);

        // when
        StockQuantity result = stock1.add(stock2);

        // then
        assertThat(result.getQuantity()).isEqualTo(8);
    }

    @Test
    @DisplayName("두 StockQuantity가 같은 수량이면 동등하다고 판단한다")
    void 두_StockQuantity가_같은_수량이면_동등하다고_판단한다() {
        // given
        StockQuantity stock1 = new StockQuantity(5);
        StockQuantity stock2 = new StockQuantity(5);

        // when & then
        assertThat(stock1.isEqualTo(stock2)).isTrue();
        assertThat(stock2.isEqualTo(stock1)).isTrue();
    }

    @Test
    @DisplayName("StockQuantity 크기 비교가 가능하다")
    void StockQuantity_크기_비교가_가능하다() {
        // given
        StockQuantity largerStock = new StockQuantity(10);
        StockQuantity smallerStock = new StockQuantity(5);
        StockQuantity equalStock = new StockQuantity(10);

        // when & then
        assertThat(largerStock.isGreaterThan(smallerStock)).isTrue();
        assertThat(smallerStock.isLessThan(largerStock)).isTrue();
        assertThat(largerStock.isGreaterThan(equalStock)).isFalse();
        assertThat(largerStock.isLessThan(equalStock)).isFalse();
    }

    @Test
    @DisplayName("정확히 같은 수량만큼 감소시킬 수 있다")
    void 정확히_같은_수량만큼_감소시킬_수_있다() {
        // given
        StockQuantity stockQuantity = new StockQuantity(5);
        RecordQuantity exactAmount = new RecordQuantity(5);

        // when
        StockQuantity result = stockQuantity.decrease(exactAmount);

        // then
        assertThat(result.getQuantity()).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("대량 재고에서도 정상적으로 동작한다")
    void 대량_재고에서도_정상적으로_동작한다() {
        // given
        StockQuantity largeStock = new StockQuantity(10000);
        RecordQuantity decreaseAmount = new RecordQuantity(3000);

        // when
        StockQuantity result = largeStock.decrease(decreaseAmount);

        // then
        assertThat(result.getQuantity()).isEqualTo(7000);
    }
}
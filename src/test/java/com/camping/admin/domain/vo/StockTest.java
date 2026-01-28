package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @ParameterizedTest(name = "수량 {0}으로 생성하면 {0}이 저장된다")
        @ValueSource(ints = {0, 1, 10, 100})
        @DisplayName("유효한 수량으로 생성할 수 있다")
        void createWithValidQuantity(int quantity) {
            Stock stock = new Stock(quantity);

            assertThat(stock.getQuantity()).isEqualTo(quantity);
        }

        @ParameterizedTest(name = "수량 {0}으로 생성하면 예외 발생")
        @ValueSource(ints = {-1, -10, -100})
        @DisplayName("음수로 생성하면 예외가 발생한다")
        void createWithNegative(int quantity) {
            assertThatThrownBy(() -> new Stock(quantity))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("null로 생성하면 예외가 발생한다")
        void createWithNull() {
            assertThatThrownBy(() -> new Stock(null))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("zero() 팩토리 메서드로 0인 재고를 생성할 수 있다")
        void createWithZeroFactory() {
            Stock stock = Stock.zero();

            assertThat(stock.getQuantity()).isZero();
        }
    }

    @Nested
    @DisplayName("decrease")
    class Decrease {

        @ParameterizedTest(name = "재고 {0}에서 {1} 감소하면 {2}이 된다")
        @CsvSource({
                "10, 3, 7",
                "10, 10, 0",
                "5, 1, 4",
                "100, 50, 50"
        })
        @DisplayName("재고를 감소시킬 수 있다")
        void decreaseStock(int initial, int amount, int expected) {
            Stock stock = new Stock(initial);

            Stock decreased = stock.decrease(amount);

            assertThat(decreased.getQuantity()).isEqualTo(expected);
            assertThat(stock.getQuantity()).isEqualTo(initial); // 불변 확인
        }

        @ParameterizedTest(name = "재고 {0}에서 {1} 감소하면 예외 발생")
        @CsvSource({
                "5, 10",
                "0, 1",
                "3, 5"
        })
        @DisplayName("재고보다 많이 감소시키면 예외가 발생한다")
        void decreaseMoreThanStock(int initial, int amount) {
            Stock stock = new Stock(initial);

            assertThatThrownBy(() -> stock.decrease(amount))
                    .isInstanceOf(DomainException.class);
        }

        @ParameterizedTest(name = "감소량 {0}이면 예외 발생")
        @ValueSource(ints = {0, -1, -10})
        @DisplayName("0 이하로 감소시키면 예외가 발생한다")
        void decreaseZeroOrNegative(int amount) {
            Stock stock = new Stock(10);

            assertThatThrownBy(() -> stock.decrease(amount))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("increase")
    class Increase {

        @ParameterizedTest(name = "재고 {0}에서 {1} 증가하면 {2}이 된다")
        @CsvSource({
                "10, 5, 15",
                "0, 1, 1",
                "100, 50, 150"
        })
        @DisplayName("재고를 증가시킬 수 있다")
        void increaseStock(int initial, int amount, int expected) {
            Stock stock = new Stock(initial);

            Stock increased = stock.increase(amount);

            assertThat(increased.getQuantity()).isEqualTo(expected);
            assertThat(stock.getQuantity()).isEqualTo(initial); // 불변 확인
        }

        @ParameterizedTest(name = "증가량 {0}이면 예외 발생")
        @ValueSource(ints = {0, -1, -10})
        @DisplayName("0 이하로 증가시키면 예외가 발생한다")
        void increaseZeroOrNegative(int amount) {
            Stock stock = new Stock(10);

            assertThatThrownBy(() -> stock.increase(amount))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("hasEnough")
    class HasEnough {

        @ParameterizedTest(name = "재고 {0}, 요청 {1} -> {2}")
        @CsvSource({
                "10, 5, true",
                "10, 10, true",
                "5, 10, false",
                "0, 1, false",
                "0, 0, true"
        })
        @DisplayName("재고 충분 여부를 확인할 수 있다")
        void hasEnoughStock(int quantity, int requested, boolean expected) {
            Stock stock = new Stock(quantity);

            assertThat(stock.hasEnough(requested)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("isEmpty")
    class IsEmpty {

        @ParameterizedTest(name = "재고 {0} -> isEmpty: {1}")
        @CsvSource({
                "0, true",
                "1, false",
                "10, false"
        })
        @DisplayName("재고가 비었는지 확인할 수 있다")
        void isEmpty(int quantity, boolean expected) {
            Stock stock = new Stock(quantity);

            assertThat(stock.isEmpty()).isEqualTo(expected);
        }
    }
}
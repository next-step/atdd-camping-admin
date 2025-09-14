package com.camping.admin.domain.vo;

import com.camping.admin.exception.InvalidQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RecordQuantityTest {

    @Test
    @DisplayName("유효한 수량으로 RecordQuantity를 생성할 수 있다")
    void 유효한_수량으로_RecordQuantity를_생성할_수_있다() {
        // given
        int validQuantity = 5;

        // when
        RecordQuantity recordQuantity = new RecordQuantity(validQuantity);

        // then
        assertThat(recordQuantity.getQuantity()).isEqualTo(validQuantity);
    }

    @Test
    @DisplayName("음수 수량으로 RecordQuantity 생성 시 예외가 발생한다")
    void 음수_수량으로_RecordQuantity_생성_시_예외가_발생한다() {
        // given
        int negativeQuantity = -1;

        // when & then
        assertThatThrownBy(() -> new RecordQuantity(negativeQuantity))
                .isInstanceOf(InvalidQuantityException.class)
                .hasMessage("Quantity must be greater than zero. Provided: " + negativeQuantity);
    }

    @Test
    @DisplayName("0 수량으로 RecordQuantity 생성 시 예외가 발생한다")
    void 영_수량으로_RecordQuantity_생성_시_예외가_발생한다() {
        // given
        int zeroQuantity = 0;

        // when & then
        assertThatThrownBy(() -> new RecordQuantity(zeroQuantity))
                .isInstanceOf(InvalidQuantityException.class)
                .hasMessage("Quantity must be greater than zero. Provided: " + zeroQuantity);
    }

    @Test
    @DisplayName("두 RecordQuantity가 같은 수량이면 동등하다고 판단한다")
    void 두_RecordQuantity가_같은_수량이면_동등하다고_판단한다() {
        // given
        RecordQuantity quantity1 = new RecordQuantity(5);
        RecordQuantity quantity2 = new RecordQuantity(5);

        // when & then
        assertThat(quantity1.isEqualTo(quantity2)).isTrue();
        assertThat(quantity2.isEqualTo(quantity1)).isTrue();
    }

    @Test
    @DisplayName("두 RecordQuantity가 다른 수량이면 동등하지 않다고 판단한다")
    void 두_RecordQuantity가_다른_수량이면_동등하지_않다고_판단한다() {
        // given
        RecordQuantity quantity1 = new RecordQuantity(5);
        RecordQuantity quantity2 = new RecordQuantity(3);

        // when & then
        assertThat(quantity1.isEqualTo(quantity2)).isFalse();
        assertThat(quantity2.isEqualTo(quantity1)).isFalse();
    }

    @Test
    @DisplayName("더 큰 수량의 RecordQuantity가 작은 수량보다 크다고 판단한다")
    void 더_큰_수량의_RecordQuantity가_작은_수량보다_크다고_판단한다() {
        // given
        RecordQuantity largerQuantity = new RecordQuantity(10);
        RecordQuantity smallerQuantity = new RecordQuantity(5);

        // when & then
        assertThat(largerQuantity.isGreaterThan(smallerQuantity)).isTrue();
        assertThat(smallerQuantity.isGreaterThan(largerQuantity)).isFalse();
    }

    @Test
    @DisplayName("같은 수량의 RecordQuantity는 서로보다 크지 않다고 판단한다")
    void 같은_수량의_RecordQuantity는_서로보다_크지_않다고_판단한다() {
        // given
        RecordQuantity quantity1 = new RecordQuantity(5);
        RecordQuantity quantity2 = new RecordQuantity(5);

        // when & then
        assertThat(quantity1.isGreaterThan(quantity2)).isFalse();
        assertThat(quantity2.isGreaterThan(quantity1)).isFalse();
    }

    @Test
    @DisplayName("두 RecordQuantity를 더할 수 있다")
    void 두_RecordQuantity를_더할_수_있다() {
        // given
        RecordQuantity quantity1 = new RecordQuantity(3);
        RecordQuantity quantity2 = new RecordQuantity(7);

        // when
        RecordQuantity result = quantity1.add(quantity2);

        // then
        assertThat(result.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("두 RecordQuantity를 뺄 수 있다")
    void 두_RecordQuantity를_뺄_수_있다() {
        // given
        RecordQuantity quantity1 = new RecordQuantity(10);
        RecordQuantity quantity2 = new RecordQuantity(3);

        // when
        RecordQuantity result = quantity1.subtract(quantity2);

        // then
        assertThat(result.getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("뺄셈 결과가 0 이하가 되면 예외가 발생한다")
    void 뺄셈_결과가_0_이하가_되면_예외가_발생한다() {
        // given
        RecordQuantity quantity1 = new RecordQuantity(5);
        RecordQuantity quantity2 = new RecordQuantity(5);

        // when & then
        assertThatThrownBy(() -> quantity1.subtract(quantity2))
                .isInstanceOf(InvalidQuantityException.class)
                .hasMessage("Quantity must be greater than zero. Provided: 0");
    }

    @Test
    @DisplayName("뺄셈 결과가 음수가 되면 예외가 발생한다")
    void 뺄셈_결과가_음수가_되면_예외가_발생한다() {
        // given
        RecordQuantity quantity1 = new RecordQuantity(3);
        RecordQuantity quantity2 = new RecordQuantity(5);

        // when & then
        assertThatThrownBy(() -> quantity1.subtract(quantity2))
                .isInstanceOf(InvalidQuantityException.class)
                .hasMessage("Quantity must be greater than zero. Provided: -2");
    }

    @Test
    @DisplayName("경계값 테스트 - 최소 유효한 수량 1로 생성할 수 있다")
    void 경계값_테스트_최소_유효한_수량_1로_생성할_수_있다() {
        // given
        int minValidQuantity = 1;

        // when
        RecordQuantity recordQuantity = new RecordQuantity(minValidQuantity);

        // then
        assertThat(recordQuantity.getQuantity()).isEqualTo(minValidQuantity);
    }

    @Test
    @DisplayName("큰 수량으로도 정상적으로 동작한다")
    void 큰_수량으로도_정상적으로_동작한다() {
        // given
        int largeQuantity = 1000;

        // when
        RecordQuantity recordQuantity = new RecordQuantity(largeQuantity);

        // then
        assertThat(recordQuantity.getQuantity()).isEqualTo(largeQuantity);
    }
}
package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SiteNumberTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "A-01, A-01",
                "a-01, A-01",
                "B-99, B-99",
                "'  Z-00  ', Z-00"
        })
        @DisplayName("유효한 사이트 번호로 생성하면 대문자로 정규화되고 앞뒤 공백이 제거된다")
        void createWithValidSiteNumber(String input, String expected) {
            SiteNumber siteNumber = new SiteNumber(input);

            assertThat(siteNumber.getValue()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("null 또는 빈 문자열이면 예외가 발생한다")
        void createWithNullOrBlank(String value) {
            assertThatThrownBy(() -> new SiteNumber(value))
                    .isInstanceOf(DomainException.class);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @ValueSource(strings = {"A01", "A-1", "AA-01", "1-01", "A-001"})
        @DisplayName("A-01 형식이 아니면 예외가 발생한다")
        void createWithInvalidFormat(String value) {
            assertThatThrownBy(() -> new SiteNumber(value))
                    .isInstanceOf(DomainException.class);
        }
    }
}
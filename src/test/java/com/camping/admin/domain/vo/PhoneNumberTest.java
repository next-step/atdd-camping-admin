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

class PhoneNumberTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "01012345678, 01012345678",
                "010-1234-5678, 01012345678",
                "01112345678, 01112345678",
                "016-123-4567, 0161234567"
        })
        @DisplayName("유효한 전화번호로 생성하면 하이픈 없이 저장된다")
        void createWithValidPhoneNumber(String input, String expected) {
            PhoneNumber phoneNumber = new PhoneNumber(input);

            assertThat(phoneNumber.getValue()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("null 또는 빈 문자열이면 예외가 발생한다")
        void createWithNullOrBlank(String value) {
            assertThatThrownBy(() -> new PhoneNumber(value))
                    .isInstanceOf(DomainException.class);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @ValueSource(strings = {"02-1234-5678", "12345678901", "010-12-34", "abcdefghijk"})
        @DisplayName("유효하지 않은 형식이면 예외가 발생한다")
        void createWithInvalidFormat(String value) {
            assertThatThrownBy(() -> new PhoneNumber(value))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("formatted")
    class Formatted {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "01012345678, 010-1234-5678",
                "0161234567, 016-123-4567"
        })
        @DisplayName("전화번호를 포맷팅할 수 있다")
        void format(String input, String expected) {
            PhoneNumber phoneNumber = new PhoneNumber(input);

            assertThat(phoneNumber.formatted()).isEqualTo(expected);
        }
    }
}
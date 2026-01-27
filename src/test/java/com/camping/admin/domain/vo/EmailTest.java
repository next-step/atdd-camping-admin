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

class EmailTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "test@example.com, test@example.com",
                "Test@Example.COM, test@example.com",
                "USER.NAME@domain.co.kr, user.name@domain.co.kr",
                "'  test@example.com  ', test@example.com"
        })
        @DisplayName("유효한 이메일로 생성하면 소문자로 정규화되고 앞뒤 공백이 제거된다")
        void createWithValidEmail(String input, String expected) {
            Email email = new Email(input);

            assertThat(email.getValue()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("null 또는 빈 문자열이면 예외가 발생한다")
        void createWithNullOrBlank(String value) {
            assertThatThrownBy(() -> new Email(value))
                    .isInstanceOf(DomainException.class);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @ValueSource(strings = {"invalid", "test@", "@example.com", "test@.com", "test@com"})
        @DisplayName("유효하지 않은 형식이면 예외가 발생한다")
        void createWithInvalidFormat(String value) {
            assertThatThrownBy(() -> new Email(value))
                    .isInstanceOf(DomainException.class);
        }
    }
}
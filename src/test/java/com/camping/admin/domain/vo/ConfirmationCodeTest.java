package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfirmationCodeTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "ABC123, ABC123",
                "abc123, ABC123",
                "ABCDEF, ABCDEF",
                "123456, 123456"
        })
        @DisplayName("유효한 코드로 생성하면 대문자로 정규화된다")
        void createWithValidCode(String input, String expected) {
            ConfirmationCode code = new ConfirmationCode(input);

            assertThat(code.getValue()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("null 또는 빈 문자열이면 예외가 발생한다")
        void createWithNullOrBlank(String value) {
            assertThatThrownBy(() -> new ConfirmationCode(value))
                    .isInstanceOf(DomainException.class);
        }

        @ParameterizedTest(name = "{0}이면 예외 발생")
        @ValueSource(strings = {"ABC12", "ABC1234", "ABC!23", "AB C12"})
        @DisplayName("6자리 영숫자가 아니면 예외가 발생한다")
        void createWithInvalidFormat(String value) {
            assertThatThrownBy(() -> new ConfirmationCode(value))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("generate")
    class Generate {

        @Test
        @DisplayName("6자리 영숫자 코드를 생성한다")
        void generateCode() {
            ConfirmationCode code = ConfirmationCode.generate();

            assertThat(code.getValue())
                    .hasSize(6)
                    .matches("^[A-Z0-9]{6}$");
        }
    }
}
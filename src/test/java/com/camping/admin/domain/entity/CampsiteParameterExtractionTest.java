package com.camping.admin.domain.entity;

import com.camping.admin.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class CampsiteParameterExtractionTest {

    @DisplayName("사이트 번호를 정상적으로 추출할 수 있다")
    @Test
    void extractSiteNumber_Success() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("siteNumber", "A-01");

        // when
        String siteNumber = Campsite.extractSiteNumber(body);

        // then
        assertThat(siteNumber).isEqualTo("A-01");
    }

    @DisplayName("사이트 번호가 null인 경우 예외가 발생한다")
    @Test
    void extractSiteNumber_NullValue_ThrowsException() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("siteNumber", null);

        // when & then
        assertThatThrownBy(() -> Campsite.extractSiteNumber(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number cannot be null");
    }

    @DisplayName("사이트 번호 키가 없는 경우 예외가 발생한다")
    @Test
    void extractSiteNumber_NoKey_ThrowsException() {
        // given
        Map<String, Object> body = new HashMap<>();

        // when & then
        assertThatThrownBy(() -> Campsite.extractSiteNumber(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("설명을 정상적으로 추출할 수 있다")
    @Test
    void extractDescription_Success() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("description", "일반 캠프사이트");

        // when
        String description = Campsite.extractDescription(body);

        // then
        assertThat(description).isEqualTo("일반 캠프사이트");
    }

    @DisplayName("설명이 null인 경우 빈 문자열을 반환한다")
    @Test
    void extractDescription_NullValue_ReturnsEmpty() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("description", null);

        // when
        String description = Campsite.extractDescription(body);

        // then
        assertThat(description).isEqualTo("");
    }

    @DisplayName("설명 키가 없는 경우 빈 문자열을 반환한다")
    @Test
    void extractDescription_NoKey_ReturnsEmpty() {
        // given
        Map<String, Object> body = new HashMap<>();

        // when
        String description = Campsite.extractDescription(body);

        // then
        assertThat(description).isEqualTo("");
    }

    @DisplayName("최대 인원을 정상적으로 추출할 수 있다")
    @Test
    void extractMaxPeople_Success() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", 4);

        // when
        Integer maxPeople = Campsite.extractMaxPeople(body);

        // then
        assertThat(maxPeople).isEqualTo(4);
    }

    @DisplayName("최대 인원이 문자열인 경우 파싱해서 반환한다")
    @Test
    void extractMaxPeople_StringValue_ParsesCorrectly() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", "6");

        // when
        Integer maxPeople = Campsite.extractMaxPeople(body);

        // then
        assertThat(maxPeople).isEqualTo(6);
    }

    @DisplayName("최대 인원 키가 없는 경우 예외가 발생한다")
    @Test
    void extractMaxPeople_NoKey_ThrowsException() {
        // given
        Map<String, Object> body = new HashMap<>();

        // when & then
        assertThatThrownBy(() -> Campsite.extractMaxPeople(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Max people is required");
    }

    @DisplayName("유효하지 않은 최대 인원 문자열인 경우 예외가 발생한다")
    @Test
    void extractMaxPeople_InvalidStringValue_ThrowsException() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", "invalid");

        // when & then
        assertThatThrownBy(() -> Campsite.extractMaxPeople(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid max people format: invalid");
    }

    @DisplayName("최대 인원이 null인 경우 예외가 발생한다")
    @Test
    void extractMaxPeople_NullValue_ThrowsException() {
        // given
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", null);

        // when & then
        assertThatThrownBy(() -> Campsite.extractMaxPeople(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Max people value cannot be null");
    }

    @DisplayName("유효한 사이트 번호 검증이 성공한다")
    @Test
    void validateSiteNumber_Valid_Success() {
        // when & then
        assertThatNoException().isThrownBy(() -> Campsite.validateSiteNumber("A-01"));
    }

    @DisplayName("null 사이트 번호 검증 시 예외가 발생한다")
    @Test
    void validateSiteNumber_Null_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Campsite.validateSiteNumber(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("빈 사이트 번호 검증 시 예외가 발생한다")
    @Test
    void validateSiteNumber_Empty_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Campsite.validateSiteNumber(""))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("공백 사이트 번호 검증 시 예외가 발생한다")
    @Test
    void validateSiteNumber_Whitespace_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Campsite.validateSiteNumber("   "))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("유효한 최대 인원 검증이 성공한다")
    @Test
    void validateMaxPeople_Valid_Success() {
        // when & then
        assertThatNoException().isThrownBy(() -> Campsite.validateMaxPeople(4));
    }

    @DisplayName("null 최대 인원 검증이 성공한다")
    @Test
    void validateMaxPeople_Null_Success() {
        // when & then
        assertThatNoException().isThrownBy(() -> Campsite.validateMaxPeople(null));
    }

    @DisplayName("음수 최대 인원 검증 시 예외가 발생한다")
    @Test
    void validateMaxPeople_Negative_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Campsite.validateMaxPeople(-1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Max people cannot be negative");
    }

    @DisplayName("Map으로부터 캠프사이트 정보를 업데이트할 수 있다")
    @Test
    void updateFromMap_Success() {
        // given
        Campsite campsite = new Campsite("원래-번호", "원래 설명", 2);
        Map<String, Object> body = new HashMap<>();
        body.put("siteNumber", "새-번호");
        body.put("description", "새 설명");
        body.put("maxPeople", 6);

        // when
        campsite.updateFromMap(body);

        // then
        assertThat(campsite.getSiteNumber()).isEqualTo("새-번호");
        assertThat(campsite.getDescription()).isEqualTo("새 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(6);
    }

    @DisplayName("빈 Map으로 업데이트해도 변경되지 않는다")
    @Test
    void updateFromMap_EmptyMap_NoChange() {
        // given
        Campsite campsite = new Campsite("원래-번호", "원래 설명", 2);
        Map<String, Object> body = new HashMap<>();

        // when
        campsite.updateFromMap(body);

        // then
        assertThat(campsite.getSiteNumber()).isEqualTo("원래-번호");
        assertThat(campsite.getDescription()).isEqualTo("원래 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(2);
    }

    @DisplayName("null Map으로 업데이트해도 변경되지 않는다")
    @Test
    void updateFromMap_NullMap_NoChange() {
        // given
        Campsite campsite = new Campsite("원래-번호", "원래 설명", 2);

        // when
        campsite.updateFromMap(null);

        // then
        assertThat(campsite.getSiteNumber()).isEqualTo("원래-번호");
        assertThat(campsite.getDescription()).isEqualTo("원래 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(2);
    }

    @DisplayName("설명만 업데이트할 수 있다")
    @Test
    void updateFromMap_DescriptionOnly() {
        // given
        Campsite campsite = new Campsite("A-01", "원래 설명", 4);
        Map<String, Object> body = new HashMap<>();
        body.put("description", "새로운 설명");

        // when
        campsite.updateFromMap(body);

        // then
        assertThat(campsite.getSiteNumber()).isEqualTo("A-01");
        assertThat(campsite.getDescription()).isEqualTo("새로운 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(4);
    }

    @DisplayName("유효하지 않은 최대 인원으로 업데이트 시 예외가 발생한다")
    @Test
    void updateFromMap_InvalidMaxPeople_ThrowsException() {
        // given
        Campsite campsite = new Campsite("A-01", "설명", 4);
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", "invalid");

        // when & then
        assertThatThrownBy(() -> campsite.updateFromMap(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid max people format during update: invalid");
    }
}
package com.camping.admin.domain.entity;

import com.camping.admin.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampsiteParameterExtractionTest {

    @DisplayName("사이트 번호를 정상적으로 추출할 수 있다")
    @Test
    void extractSiteNumber_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("siteNumber", "A-01");

        String siteNumber = Campsite.extractSiteNumber(body);

        assertThat(siteNumber).isEqualTo("A-01");
    }

    @DisplayName("사이트 번호가 null인 경우 예외가 발생한다")
    @Test
    void extractSiteNumber_NullValue_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("siteNumber", null);

        assertThatThrownBy(() -> Campsite.extractSiteNumber(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number cannot be null");
    }

    @DisplayName("사이트 번호 키가 없는 경우 예외가 발생한다")
    @Test
    void extractSiteNumber_NoKey_ThrowsException() {
        Map<String, Object> body = new HashMap<>();

        assertThatThrownBy(() -> Campsite.extractSiteNumber(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("설명을 정상적으로 추출할 수 있다")
    @Test
    void extractDescription_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("description", "일반 캠프사이트");

        String description = Campsite.extractDescription(body);

        assertThat(description).isEqualTo("일반 캠프사이트");
    }

    @DisplayName("설명이 null인 경우 빈 문자열을 반환한다")
    @Test
    void extractDescription_NullValue_ReturnsEmpty() {
        Map<String, Object> body = new HashMap<>();
        body.put("description", null);

        String description = Campsite.extractDescription(body);

        assertThat(description).isEqualTo("");
    }

    @DisplayName("설명 키가 없는 경우 빈 문자열을 반환한다")
    @Test
    void extractDescription_NoKey_ReturnsEmpty() {
        Map<String, Object> body = new HashMap<>();

        String description = Campsite.extractDescription(body);

        assertThat(description).isEqualTo("");
    }

    @DisplayName("최대 인원을 정상적으로 추출할 수 있다")
    @Test
    void extractMaxPeople_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", 4);

        Integer maxPeople = Campsite.extractMaxPeople(body);

        assertThat(maxPeople).isEqualTo(4);
    }

    @DisplayName("최대 인원이 문자열인 경우 파싱해서 반환한다")
    @Test
    void extractMaxPeople_StringValue_ParsesCorrectly() {
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", "6");

        Integer maxPeople = Campsite.extractMaxPeople(body);

        assertThat(maxPeople).isEqualTo(6);
    }

    @DisplayName("최대 인원 키가 없는 경우 예외가 발생한다")
    @Test
    void extractMaxPeople_NoKey_ThrowsException() {
        Map<String, Object> body = new HashMap<>();

        assertThatThrownBy(() -> Campsite.extractMaxPeople(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Max people is required");
    }

    @DisplayName("유효하지 않은 최대 인원 문자열인 경우 예외가 발생한다")
    @Test
    void extractMaxPeople_InvalidStringValue_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", "invalid");

        assertThatThrownBy(() -> Campsite.extractMaxPeople(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid max people format: invalid");
    }

    @DisplayName("최대 인원이 null인 경우 예외가 발생한다")
    @Test
    void extractMaxPeople_NullValue_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", null);

        assertThatThrownBy(() -> Campsite.extractMaxPeople(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Max people value cannot be null");
    }

    @DisplayName("유효한 사이트 번호 검증이 성공한다")
    @Test
    void validateSiteNumber_Valid_Success() {
        assertThatNoException().isThrownBy(() -> Campsite.validateSiteNumber("A-01"));
    }

    @DisplayName("null 사이트 번호 검증 시 예외가 발생한다")
    @Test
    void validateSiteNumber_Null_ThrowsException() {
        assertThatThrownBy(() -> Campsite.validateSiteNumber(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("빈 사이트 번호 검증 시 예외가 발생한다")
    @Test
    void validateSiteNumber_Empty_ThrowsException() {
        assertThatThrownBy(() -> Campsite.validateSiteNumber(""))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("공백 사이트 번호 검증 시 예외가 발생한다")
    @Test
    void validateSiteNumber_Whitespace_ThrowsException() {
        assertThatThrownBy(() -> Campsite.validateSiteNumber("   "))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Site number is required");
    }

    @DisplayName("유효한 최대 인원 검증이 성공한다")
    @Test
    void validateMaxPeople_Valid_Success() {
        assertThatNoException().isThrownBy(() -> Campsite.validateMaxPeople(4));
    }

    @DisplayName("null 최대 인원 검증이 성공한다")
    @Test
    void validateMaxPeople_Null_Success() {
        assertThatNoException().isThrownBy(() -> Campsite.validateMaxPeople(null));
    }

    @DisplayName("음수 최대 인원 검증 시 예외가 발생한다")
    @Test
    void validateMaxPeople_Negative_ThrowsException() {
        assertThatThrownBy(() -> Campsite.validateMaxPeople(-1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Max people cannot be negative");
    }

    @DisplayName("Map으로부터 캠프사이트 정보를 업데이트할 수 있다")
    @Test
    void updateFromMap_Success() {
        Campsite campsite = new Campsite("원래-번호", "원래 설명", 2);
        Map<String, Object> body = new HashMap<>();
        body.put("siteNumber", "새-번호");
        body.put("description", "새 설명");
        body.put("maxPeople", 6);

        campsite.updateFromMap(body);

        assertThat(campsite.getSiteNumber()).isEqualTo("새-번호");
        assertThat(campsite.getDescription()).isEqualTo("새 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(6);
    }

    @DisplayName("빈 Map으로 업데이트해도 변경되지 않는다")
    @Test
    void updateFromMap_EmptyMap_NoChange() {
        Campsite campsite = new Campsite("원래-번호", "원래 설명", 2);
        Map<String, Object> body = new HashMap<>();

        campsite.updateFromMap(body);

        assertThat(campsite.getSiteNumber()).isEqualTo("원래-번호");
        assertThat(campsite.getDescription()).isEqualTo("원래 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(2);
    }

    @DisplayName("null Map으로 업데이트해도 변경되지 않는다")
    @Test
    void updateFromMap_NullMap_NoChange() {
        Campsite campsite = new Campsite("원래-번호", "원래 설명", 2);

        campsite.updateFromMap(null);

        assertThat(campsite.getSiteNumber()).isEqualTo("원래-번호");
        assertThat(campsite.getDescription()).isEqualTo("원래 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(2);
    }

    @DisplayName("설명만 업데이트할 수 있다")
    @Test
    void updateFromMap_DescriptionOnly() {
        Campsite campsite = new Campsite("A-01", "원래 설명", 4);
        Map<String, Object> body = new HashMap<>();
        body.put("description", "새로운 설명");

        campsite.updateFromMap(body);

        assertThat(campsite.getSiteNumber()).isEqualTo("A-01");
        assertThat(campsite.getDescription()).isEqualTo("새로운 설명");
        assertThat(campsite.getMaxPeople()).isEqualTo(4);
    }

    @DisplayName("유효하지 않은 최대 인원으로 업데이트 시 예외가 발생한다")
    @Test
    void updateFromMap_InvalidMaxPeople_ThrowsException() {
        Campsite campsite = new Campsite("A-01", "설명", 4);
        Map<String, Object> body = new HashMap<>();
        body.put("maxPeople", "invalid");

        assertThatThrownBy(() -> campsite.updateFromMap(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid max people format during update: invalid");
    }
}

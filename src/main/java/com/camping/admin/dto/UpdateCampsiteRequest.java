package com.camping.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 캠프사이트 수정 요청 DTO
 * - Map<String, Object> 대신 타입 안전한 DTO 사용
 */
@Getter
@NoArgsConstructor
public class UpdateCampsiteRequest {

    private String siteNumber;
    private String description;
    private Integer maxPeople;
}
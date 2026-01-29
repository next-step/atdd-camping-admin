package com.camping.admin.helper;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.common.CommonHooks;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.camping.admin.apiExtractableresponse.CampsiteApiExtractableResponse.캠프사이트를_등록한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 캠프사이트 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class CampsiteTestHelper {

    @Autowired
    private CampsiteRepository campsiteRepository;

    private long campsiteCountBeforeRequest;

    // ==================== 캠프사이트 설정 (Given) ====================

    public void 사이트번호로_캠프사이트를_생성한다(String siteNumber) {
        if (!campsiteRepository.existsBySiteNumber(siteNumber)) {
            campsiteRepository.save(new Campsite(siteNumber, "테스트 설명", 4));
        }
    }

    // ==================== API 호출 (When) ====================

    public void 캠프사이트_정보로_등록을_요청한다(DataTable dataTable) {
        this.campsiteCountBeforeRequest = campsiteRepository.count();

        Map<String, String> row = dataTable.asMaps().get(0);
        String siteNumber = row.get("사이트번호");
        String description = row.get("설명");
        int maxPeople = Integer.parseInt(row.get("최대인원"));

        Map<String, Object> campsiteData = new HashMap<>();
        campsiteData.put("siteNumber", siteNumber);
        campsiteData.put("description", description);
        campsiteData.put("maxPeople", maxPeople);

        CommonHooks.lastResponse = 캠프사이트를_등록한다(campsiteData);
    }

    public void 사이트번호_없이_등록을_요청한다() {
        this.campsiteCountBeforeRequest = campsiteRepository.count();

        Map<String, Object> campsiteData = new HashMap<>();
        campsiteData.put("description", "테스트 설명");
        campsiteData.put("maxPeople", 4);

        CommonHooks.lastResponse = 캠프사이트를_등록한다(campsiteData);
    }

    public void 사이트번호로_등록을_요청한다(String siteNumber) {
        this.campsiteCountBeforeRequest = campsiteRepository.count();

        Map<String, Object> campsiteData = new HashMap<>();
        campsiteData.put("siteNumber", siteNumber);
        campsiteData.put("description", "테스트 설명");
        campsiteData.put("maxPeople", 4);

        CommonHooks.lastResponse = 캠프사이트를_등록한다(campsiteData);
    }

    public void 최대인원으로_등록을_요청한다(int maxPeople) {
        this.campsiteCountBeforeRequest = campsiteRepository.count();

        Map<String, Object> campsiteData = new HashMap<>();
        campsiteData.put("siteNumber", "TEST-001");
        campsiteData.put("description", "테스트 설명");
        campsiteData.put("maxPeople", maxPeople);

        CommonHooks.lastResponse = 캠프사이트를_등록한다(campsiteData);
    }

    public void 빈_사이트번호로_등록을_요청한다() {
        this.campsiteCountBeforeRequest = campsiteRepository.count();

        Map<String, Object> campsiteData = new HashMap<>();
        campsiteData.put("siteNumber", "");
        campsiteData.put("description", "테스트 설명");
        campsiteData.put("maxPeople", 4);

        CommonHooks.lastResponse = 캠프사이트를_등록한다(campsiteData);
    }

    // ==================== 검증 (Then) ====================

    public void 캠프사이트가_등록되었는지_검증한다() {
        long currentCount = campsiteRepository.count();
        assertThat(currentCount).isEqualTo(campsiteCountBeforeRequest + 1);
    }

    public void 캠프사이트가_등록되지_않았는지_검증한다() {
        long currentCount = campsiteRepository.count();
        assertThat(currentCount).isEqualTo(campsiteCountBeforeRequest);
    }

    public void 사이트번호로_캠프사이트가_존재하는지_검증한다(String siteNumber) {
        assertThat(campsiteRepository.existsBySiteNumber(siteNumber)).isTrue();
    }
}
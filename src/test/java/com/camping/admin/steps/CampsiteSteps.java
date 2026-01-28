package com.camping.admin.steps;

import com.camping.admin.api.CampsiteApi;
import com.camping.admin.common.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static com.camping.admin.common.TestContext.Key.*;

public class CampsiteSteps {

    private static String uniqueSiteNumber() {
        return "SITE-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // ===== Given =====

    @Given("등록된 캠프사이트가 있다")
    public void 등록된_캠프사이트가_있다() {
        Response response = CampsiteApi.캠프사이트_생성(
                TestContext.getAdminToken(),
                uniqueSiteNumber(),
                "테스트 캠프사이트",
                4
        );
        TestContext.set(CAMPSITE_ID, response.jsonPath().getLong("id"));
    }

    // ===== When: 인증 O =====

    @When("관리자가 캠프사이트를 등록한다")
    public void 관리자가_캠프사이트를_등록한다() {
        Response response = CampsiteApi.캠프사이트_생성(
                TestContext.getAdminToken(),
                uniqueSiteNumber(),
                "신규 캠프사이트",
                6
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 캠프사이트 정보를 수정한다")
    public void 관리자가_캠프사이트_정보를_수정한다() {
        Response response = CampsiteApi.캠프사이트_수정(
                TestContext.getAdminToken(),
                TestContext.getId(CAMPSITE_ID),
                Map.of("description", "수정된 설명", "maxPeople", 8)
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 캠프사이트 목록을 조회한다")
    public void 관리자가_캠프사이트_목록을_조회한다() {
        Response response = CampsiteApi.목록_조회(TestContext.getAdminToken());
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 캠프사이트를 수정한다")
    public void 관리자가_존재하지_않는_캠프사이트를_수정한다() {
        Response response = CampsiteApi.캠프사이트_수정(
                TestContext.getAdminToken(),
                99999L,
                Map.of("description", "수정")
        );
        TestContext.setLastResponse(response);
    }

    // ===== When: 인증 X =====

    @When("관리자 권한 없이 캠프사이트를 등록한다")
    public void 관리자_권한_없이_캠프사이트를_등록한다() {
        Response response = CampsiteApi.캠프사이트_생성_인증없이("C-001", "테스트", 4);
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 캠프사이트를 수정한다")
    public void 관리자_권한_없이_캠프사이트를_수정한다() {
        Response response = CampsiteApi.캠프사이트_수정_인증없이(1L, Map.of("description", "수정"));
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 캠프사이트 목록을 조회한다")
    public void 관리자_권한_없이_캠프사이트_목록을_조회한다() {
        Response response = CampsiteApi.목록_조회_인증없이();
        TestContext.setLastResponse(response);
    }
}
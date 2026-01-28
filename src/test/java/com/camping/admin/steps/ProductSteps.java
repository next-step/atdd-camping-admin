package com.camping.admin.steps;

import com.camping.admin.api.ProductApi;
import com.camping.admin.common.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class ProductSteps {

    // ===== Given =====

    @Given("등록된 상품이 있다")
    public void 등록된_상품이_있다() {
        Response response = ProductApi.상품_생성(
                TestContext.getAdminToken(),
                "테스트 상품",
                100,
                10000,
                "RENTAL"
        );
        TestContext.setProductId(response.jsonPath().getLong("id"));
    }

    // ===== When: 인증 O =====

    @When("관리자가 대여 상품을 등록한다")
    public void 관리자가_대여_상품을_등록한다() {
        Response response = ProductApi.상품_생성(
                TestContext.getAdminToken(),
                "대여 테스트 상품",
                50,
                5000,
                "RENTAL"
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 판매 상품을 등록한다")
    public void 관리자가_판매_상품을_등록한다() {
        Response response = ProductApi.상품_생성(
                TestContext.getAdminToken(),
                "판매 테스트 상품",
                100,
                15000,
                "SALE"
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 상품 정보를 수정한다")
    public void 관리자가_상품_정보를_수정한다() {
        Response response = ProductApi.상품_수정(
                TestContext.getAdminToken(),
                TestContext.getProductId(),
                Map.of("name", "수정된 상품명", "price", 20000)
        );
        TestContext.setLastResponse(response);
    }

    @When("관리자가 상품 목록을 조회한다")
    public void 관리자가_상품_목록을_조회한다() {
        Response response = ProductApi.목록_조회(TestContext.getAdminToken());
        TestContext.setLastResponse(response);
    }

    @When("관리자가 존재하지 않는 상품을 수정한다")
    public void 관리자가_존재하지_않는_상품을_수정한다() {
        Response response = ProductApi.상품_수정(
                TestContext.getAdminToken(),
                99999L,
                Map.of("name", "수정된 상품명")
        );
        TestContext.setLastResponse(response);
    }

    // ===== When: 인증 X =====

    @When("관리자 권한 없이 상품을 등록한다")
    public void 관리자_권한_없이_상품을_등록한다() {
        Response response = ProductApi.상품_생성_인증없이("테스트 상품", 10, 1000, "SALE");
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 상품을 수정한다")
    public void 관리자_권한_없이_상품을_수정한다() {
        Response response = ProductApi.상품_수정_인증없이(1L, Map.of("name", "수정"));
        TestContext.setLastResponse(response);
    }

    @When("관리자 권한 없이 상품 목록을 조회한다")
    public void 관리자_권한_없이_상품_목록을_조회한다() {
        Response response = ProductApi.목록_조회_인증없이();
        TestContext.setLastResponse(response);
    }
}

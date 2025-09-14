package com.camping.admin.steps.campsite;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.steps.campsite.dto.CampsiteDetail;
import com.camping.admin.steps.test_context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class CampsiteCreateSteps {

    @Given("사이트번호가 {string}인 캠프사이트가 없다")
    public void 사이트번호가_XX인_캠프사이트가_없다(String siteNumber) {
        var 전체_캠프사이트_조회_응답 = CampsiteClient.전체_캠프사이트_조회_요청을_한다(TestContext.auth.인증_토큰());
        assertThat(전체_캠프사이트_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 파라미터로 들어온 siteNumber와 동일한 캠프사이트가 존재하면, 해당 캠프사이트를 수정
        var 전체_캠프사이트 = 전체_캠프사이트_조회_응답.as(new TypeRef<List<CampsiteDetail>>() {});

        for (var campsiteDetail : 전체_캠프사이트) {
            if (campsiteDetail.siteNumber().equals(siteNumber)) {
                캠프사이트의_사이트번호를_랜덤으로_변경한다(campsiteDetail);
                return;
            }
        }
    }

    private void 캠프사이트의_사이트번호를_랜덤으로_변경한다(CampsiteDetail campsiteDetail) {
        var 캠프사이트_수정_응답 = CampsiteClient.캠프사이트_수정_요청을_한다(
            TestContext.auth.인증_토큰(),
            campsiteDetail.id(),
            UUID.randomUUID().toString(), // 랜덤한 사이트번호 생성
            campsiteDetail.description(),
            4
        );
        assertThat(캠프사이트_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Given("사이트번호가 {string}인 캠프사이트가 생성되어있다")
    public void 사이트번호가_XX인_캠프사이트가_생성되어있다(String siteNumber) {
        사이트번호가_XX인_캠프사이트를_생성한다(siteNumber);
        캠프사이트_생성이_성공한다();
    }

    @When("사이트번호가 null인 캠프사이트를 생성한다")
    public void 사이트번호가_null인_캠프사이트를_생성한다() {
        var 캠프사이트_생성_응답 = CampsiteClient.캠프사이트_생성_요청을_한다(
            TestContext.auth.인증_토큰(),
            null,
            "테스트 캠프사이트",
            4
        );

        TestContext.campsite.캠프사이트_생성_응답(캠프사이트_생성_응답);
    }

    @When("사이트번호가 {string}인 캠프사이트를 생성한다")
    public void 사이트번호가_XX인_캠프사이트를_생성한다(String siteNumber) {
        var 캠프사이트_생성_응답 = CampsiteClient.캠프사이트_생성_요청을_한다(
            TestContext.auth.인증_토큰(),
            siteNumber,
            "테스트 캠프사이트",
            4
        );
        TestContext.campsite.캠프사이트_생성_응답(캠프사이트_생성_응답);
    }

    @Then("캠프사이트 생성이 성공한다")
    public void 캠프사이트_생성이_성공한다() {
        Objects.requireNonNull(TestContext.campsite.캠프사이트_생성_응답());
        assertThat(TestContext.campsite.캠프사이트_생성_응답().statusCode())
            .isEqualTo(HttpStatus.CREATED.value());
    }

    @Then("캠프사이트 생성이 실패한다")
    public void 캠프사이트_생성이_실패한다() {
        Objects.requireNonNull(TestContext.campsite.캠프사이트_생성_응답());
        assertThat(TestContext.campsite.캠프사이트_생성_응답().statusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

package com.camping.admin.steps.campsite;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.camping.admin.steps.campsite.dto.CampsiteDetail;
import com.camping.admin.steps.test_context.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class CampsiteUpdateSteps {

    @When("캠프사이트의 사이트번호를 {string}로 수정한다")
    public void 캠프사이트의_사이트번호를_XX로_수정한다(String siteNumber) {
        var campsiteDetail = TestContext.campsite.캠프사이트_생성_응답().as(CampsiteDetail.class);
        var 캠프사이트_수정_응답 = CampsiteClient.캠프사이트_수정_요청을_한다(
            TestContext.auth.인증_토큰(),
            campsiteDetail.id(),
            siteNumber,
            campsiteDetail.description(),
            campsiteDetail.maxPeople()
        );
        TestContext.campsite.캠프사이트_수정_응답(캠프사이트_수정_응답);
    }

    @When("사이트번호가 {string}인 캠프사이트의 사이트번호를 {string}로 수정한다")
    public void 사이트번호가_XX인_캠프사이트의_사이트번호를_YY로_수정한다(String oldSiteNumber, String newSiteNumber) {
        var campsiteDetail = 사이트번호로_캠프사이트를_조회한다(oldSiteNumber);
        var 캠프사이트_수정_응답 = CampsiteClient.캠프사이트_수정_요청을_한다(
            TestContext.auth.인증_토큰(),
            campsiteDetail.id(),
            newSiteNumber,
            campsiteDetail.description(),
            campsiteDetail.maxPeople()
        );
        TestContext.campsite.캠프사이트_수정_응답(캠프사이트_수정_응답);
    }

    private static CampsiteDetail 사이트번호로_캠프사이트를_조회한다(String siteNumber) {
        var 전체_캠프사이트_조회_응답 = CampsiteClient.전체_캠프사이트_조회_요청을_한다(TestContext.auth.인증_토큰());
        var 전체_캠프사이트 = 전체_캠프사이트_조회_응답.as(new TypeRef<List<CampsiteDetail>>() {
        });

        CampsiteDetail targetCampsite = null;
        for (var campsiteDetail : 전체_캠프사이트) {
            if (campsiteDetail.siteNumber().equals(siteNumber)) {
                return campsiteDetail;
            }
        }

        throw new IllegalArgumentException("캠프사이트를 찾을 수 없습니다 - siteNumber: " + siteNumber);
    }


    @Then("캠프사이트 수정이 성공한다")
    public void 캠프사이트_수정이_성공한다() {
        Objects.requireNonNull(TestContext.campsite.캠프사이트_수정_응답());
        assertThat(TestContext.campsite.캠프사이트_수정_응답().statusCode())
            .isEqualTo(HttpStatus.OK.value());
    }

    @Then("캠프사이트 수정이 실패한다")
    public void 캠프사이트_수정이_실패한다() {
        Objects.requireNonNull(TestContext.campsite.캠프사이트_수정_응답());
        assertThat(TestContext.campsite.캠프사이트_수정_응답().statusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

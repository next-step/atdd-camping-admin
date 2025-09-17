package com.camping.admin.steps.campsite;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.steps.campsite.dto.CampsiteDetail;
import com.camping.admin.steps.test_context.TestContext;
import io.cucumber.java.en.Then;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class CampsiteGetSteps {

    @Then("캠프사이트 목록에 사이트번호가 {string}인 캠프사이트가 조회된다")
    public void 캠프사이트_목록에_사이트번호가_XX인_캠프사이트가_조회된다(String siteNumber) {
        전체_캠프사이트를_조회한다();
        전체_캠프사이트_조회가_성공한다();
        캠프사이트가_존재한다(siteNumber);
    }

    public void 전체_캠프사이트를_조회한다() {
        var 전체_캠프사이트_조회_응답 = CampsiteClient.전체_캠프사이트_조회_요청을_한다();
        TestContext.campsite.전체_캠프사이트_조회_응답(전체_캠프사이트_조회_응답);
    }

    public void 전체_캠프사이트_조회가_성공한다() {
        Objects.requireNonNull(TestContext.campsite.전체_캠프사이트_조회_응답());
        assertThat(TestContext.campsite.전체_캠프사이트_조회_응답().statusCode()).isEqualTo(
            HttpStatus.OK.value());
    }

    public void 캠프사이트가_존재한다(String siteNumber) {
        var 캠프사이트_목록 = TestContext.campsite.전체_캠프사이트_조회_응답()
            .as(new TypeRef<List<CampsiteDetail>>() {});

        boolean campsiteExists = 캠프사이트_목록.stream()
            .anyMatch(campsite -> campsite.siteNumber().equals(siteNumber));
        assertThat(campsiteExists).isTrue();
    }
}

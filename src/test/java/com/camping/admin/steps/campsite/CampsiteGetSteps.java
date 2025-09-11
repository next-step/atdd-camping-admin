package com.camping.admin.steps.campsite;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.steps.auth.LoginSteps;
import com.camping.admin.steps.campsite.dto.CampsiteDetail;
import io.cucumber.java.en.Then;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class CampsiteGetSteps {

    private Response 전체_목록_조회_응답;

    @Then("캠프사이트 목록에 생성한 캠프사이트가 조회된다")
    public void 캠프사이트_목록에_생성한_캠프사이트가_조회된다() {
        전체_캠프사이트를_조회한다();
        전체_캠프사이트_조회가_성공한다();
        캠프사이트가_존재한다();
    }

    public void 전체_캠프사이트를_조회한다() {
        전체_목록_조회_응답 = given()
            .header("Authorization", "Bearer " + LoginSteps.get어드민_인증_토큰())
            .when()
            .get("/admin/campsites")
            .andReturn();
    }

    public void 전체_캠프사이트_조회가_성공한다() {
        Objects.requireNonNull(전체_목록_조회_응답);
        assertThat(전체_목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public void 캠프사이트가_존재한다() {
        var 캠프사이트_목록 = 전체_목록_조회_응답.as(new TypeRef<List<CampsiteDetail>>() {
        });

        boolean campsiteExists = 캠프사이트_목록.stream()
            .anyMatch(campsite ->
                CampsiteCreateSteps.get생성한_캠프사이트_번호().equals(campsite.siteNumber()));
        assertThat(campsiteExists).isTrue();
    }
}

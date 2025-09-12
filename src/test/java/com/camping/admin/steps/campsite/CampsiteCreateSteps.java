package com.camping.admin.steps.campsite;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.steps.auth.LoginSteps;
import com.camping.admin.steps.campsite.dto.CampsiteDetail;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CampsiteCreateSteps {

    @Getter
    private static String 생성한_캠프사이트_번호;

    private Response 캠프사이트_생성_응답;

    @Given("사이트번호가 {string}인 캠프사이트가 없다")
    public void 사이트번호가_XX인_캠프사이트가_없다(String siteNumber) {
        var 전체_목록_조회_응답 = CampsiteClient.전체_캠프사이트를_조회한다(LoginSteps.get어드민_인증_토큰());
        assertThat(전체_목록_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 파라미터로 들어온 siteNumber와 동일한 캠프사이트가 존재하면, 해당 캠프사이트를 수정
        var 캠프사이트_목록 = 전체_목록_조회_응답.as(new TypeRef<List<CampsiteDetail>>() {
        });
        for (var campsiteDetail : 캠프사이트_목록) {
            if (campsiteDetail.siteNumber().equals(siteNumber)) {
                var 캠프사이트_수정_응답 = CampsiteClient.캠프사이트를_수정한다(
                    LoginSteps.get어드민_인증_토큰(),
                    campsiteDetail.id(),
                    UUID.randomUUID().toString(),
                    "테스트 캠프사이트",
                    4
                );
                assertThat(캠프사이트_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                return;
            }
        }
    }

    @When("사이트번호가 {string}인 캠프사이트를 생성한다")
    public void 사이트번호가_XX인_캠프사이트를_생성한다(String siteNumber) {
        var 캠프사이트_생성_응답 = CampsiteClient.캠프사이트를_생성한다(
            LoginSteps.get어드민_인증_토큰(),
            siteNumber,
            "테스트 캠프사이트",
            4
        );

        생성한_캠프사이트_번호 = siteNumber;
        this.캠프사이트_생성_응답 = 캠프사이트_생성_응답;
    }

    @Then("캠프사이트 생성이 성공한다")
    public void 캠프사이트_생성이_성공한다() {
        Objects.requireNonNull(this.캠프사이트_생성_응답);
        assertThat(this.캠프사이트_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}

package com.camping.admin.steps.campsite;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.steps.auth.LoginSteps;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CampsiteCreateSteps {

    @Getter
    private static String 생성한_캠프사이트_번호;

    private Response 캠프사이트_생성_응답;

    @When("캠프사이트를 생성한다")
    public void 캠프사이트를_생성한다() {
        var randomCampsiteNumber = UUID.randomUUID().toString();

        var 캠프사이트_생성_응답 = given()
            .header("Authorization", "Bearer " + LoginSteps.get어드민_인증_토큰())
            .body(Map.of(
                "siteNumber", randomCampsiteNumber,
                "description", "테스트 캠프사이트",
                "maxPeople", 4
            ))
            .when()
            .post("/admin/campsites")
            .andReturn();

        생성한_캠프사이트_번호 = randomCampsiteNumber;
        this.캠프사이트_생성_응답 = 캠프사이트_생성_응답;
    }

    @Then("캠프사이트가 생성된다")
    public void 캠프사이트가_생성된다() {
        Objects.requireNonNull(this.캠프사이트_생성_응답);
        assertThat(this.캠프사이트_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}

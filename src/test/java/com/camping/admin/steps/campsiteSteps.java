package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class campsiteSteps {

    @Autowired private TestContext context;
    @Autowired private CampsiteRepository campsiteRepository;

    @Given("캠프사이트 {int}개가 등록되어 있다")
    public void 캠프사이트N개가등록되어있다(int count) {
        for (int i = 1; i <= count; i++) {
            campsiteRepository.save(new Campsite("T-0" + i, "테스트 사이트 " + i, 4));
        }
    }

    @When("캠프사이트 목록을 조회한다")
    public void 캠프사이트목록을조회한다() {
        context.response = context.authRequest().get("/admin/campsites");
    }

    @When("새로운 캠프사이트를 등록한다")
    public void 새로운캠프사이트를등록한다() {
        context.response = context.authRequest()
                .body(Map.of("siteNumber", "B-01", "description", "신규 사이트", "maxPeople", 5))
                .post("/admin/campsites");
        if (context.response.statusCode() == 201) {
            context.campsiteId = context.response.jsonPath().getLong("id");
        }
    }

    @When("siteNumber 없이 캠프사이트를 등록한다")
    public void siteNumber없이캠프사이트를등록한다() {
        context.response = context.authRequest()
                .body(Map.of("description", "siteNumber 누락", "maxPeople", 4))
                .post("/admin/campsites");
    }

    @When("중복 siteNumber로 캠프사이트를 등록한다")
    public void 중복siteNumber로캠프사이트를등록한다() {
        context.response = context.authRequest()
                .body(Map.of("siteNumber", "T-01", "description", "중복 사이트", "maxPeople", 4))
                .post("/admin/campsites");
    }

    @When("음수 최대 인원으로 캠프사이트를 등록한다")
    public void 음수최대인원으로캠프사이트를등록한다() {
        context.response = context.authRequest()
                .body(Map.of("siteNumber", "NEG-01", "description", "음수 인원", "maxPeople", -1))
                .post("/admin/campsites");
    }

    @When("캠프사이트 정보를 수정한다")
    public void 캠프사이트정보를수정한다() {
        context.response = context.authRequest()
                .body(Map.of("description", "수정된 사이트 설명", "maxPeople", 8))
                .put("/admin/campsites/" + context.campsiteId);
    }

    @Then("캠프사이트가 생성된다")
    public void 캠프사이트가생성된다() {
        context.response.then().statusCode(201);
    }

    @And("캠프사이트 {int}개가 반환된다")
    public void 캠프사이트N개가반환된다(int count) {
        context.response.then().body("$", hasSize(count));
    }

    @And("생성된 캠프사이트 정보가 반환된다")
    public void 생성된캠프사이트정보가반환된다() {
        context.response.then()
                .body("id", notNullValue())
                .body("siteNumber", notNullValue());
    }

    @And("수정된 캠프사이트 정보가 반환된다")
    public void 수정된캠프사이트정보가반환된다() {
        context.response.then()
                .body("description", equalTo("수정된 사이트 설명"))
                .body("maxPeople", equalTo(8));
    }

    // ── 예외 시나리오 ──────────────────────────────────────────

    @When("존재하지 않는 캠프사이트를 수정한다")
    public void 존재하지않는캠프사이트를수정한다() {
        context.response = context.authRequest()
                .body(Map.of("description", "없는 사이트", "maxPeople", 4))
                .put("/admin/campsites/99999");
    }

    @Then("캠프사이트를 찾을 수 없다")
    public void 캠프사이트를찾을수없다() {
        context.response.then().statusCode(404);
    }
}

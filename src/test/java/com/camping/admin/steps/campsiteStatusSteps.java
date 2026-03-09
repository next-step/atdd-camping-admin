package com.camping.admin.steps;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.enums.CampsiteStatus;
import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class campsiteStatusSteps {

    @Autowired private TestContext context;
    @Autowired private CampsiteRepository campsiteRepository;

    // ── Given ─────────────────────────────────────────────

    @Given("캠프사이트가 점검중 상태이다")
    public void 캠프사이트가점검중상태이다() {
        Campsite campsite = campsiteRepository.findById(context.campsiteId).orElseThrow();
        campsite.setStatus(CampsiteStatus.MAINTENANCE);
        campsiteRepository.save(campsite);
    }

    // ── When ──────────────────────────────────────────────

    @When("캠프사이트 상태를 점검중으로 변경한다")
    public void 캠프사이트상태를점검중으로변경한다() {
        patchCampsiteStatus("MAINTENANCE");
    }

    @When("캠프사이트 상태를 운영종료로 변경한다")
    public void 캠프사이트상태를운영종료로변경한다() {
        patchCampsiteStatus("CLOSED");
    }

    @When("캠프사이트 상태를 이용가능으로 변경한다")
    public void 캠프사이트상태를이용가능으로변경한다() {
        patchCampsiteStatus("AVAILABLE");
    }

    @When("유효하지 않은 캠프사이트 상태로 변경한다")
    public void 유효하지않은캠프사이트상태로변경한다() {
        patchCampsiteStatus("INVALID_STATUS");
    }

    // ── Then / And ────────────────────────────────────────

    @Then("캠프사이트 상태 변경이 거부된다")
    public void 캠프사이트상태변경이거부된다() {
        context.response.then().statusCode(400);
    }

    @And("캠프사이트 상태가 점검중이다")
    public void 캠프사이트상태가점검중이다() {
        context.response.then().body("status", equalTo("MAINTENANCE"));
    }

    @And("캠프사이트 상태가 운영종료이다")
    public void 캠프사이트상태가운영종료이다() {
        context.response.then().body("status", equalTo("CLOSED"));
    }

    @And("캠프사이트 상태가 이용가능이다")
    public void 캠프사이트상태가이용가능이다() {
        context.response.then().body("status", equalTo("AVAILABLE"));
    }

    // ── 헬퍼 ──────────────────────────────────────────────

    private void patchCampsiteStatus(String status) {
        context.response = context.authRequest()
                .body(Map.of("status", status))
                .patch("/admin/campsites/" + context.campsiteId + "/status");
    }
}

package com.camping.admin.steps;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import com.camping.admin.context.CommonContext;
import com.camping.admin.helper.CampSiteApiHelper;
import com.camping.admin.helper.ReservationApiHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CampsiteSteps {
    private final CommonContext commonContext = new CommonContext();

    @Given("{string} 사이트 번호로 캠프사이트가 이미 존재한다")
    public void campsiteWithSiteNumberAlreadyExists(String siteNumber) {
        commonContext.setResponse(
                CampSiteApiHelper.sendCampsiteCreationRequest(siteNumber)
        );
        assertSuccessfulCreation();
    }

    @Given("{string} 사이트에 활성화된 예약이 있다")
    public void campsiteHasActiveReservation(String siteNumber) {
        Long campsiteId = CampSiteApiHelper.findCampsiteIdBySiteNumber(siteNumber);
        ReservationApiHelper.createReservationRequest(campsiteId);
    }


    @When("사이트 번호 {string}로 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreation(String siteNumber) {
        commonContext.setResponse(
                CampSiteApiHelper.sendCampsiteCreationRequest(siteNumber)
        );
    }

    @When("사이트 번호 없이 캠프사이트 생성을 요청한다")
    public void requestCampsiteCreationWithoutSiteNumber() {
        commonContext.setResponse(
                CampSiteApiHelper.sendCampsiteCreationRequest(null)
        );
    }

    @When("{string} 사이트를 {string} 사이트 번호로 변경 요청한다")
    public void requestCampsiteSiteNumberUpdate(String oldSiteNumber, String newSiteNumber) {
        Long campsiteId = CampSiteApiHelper.findCampsiteIdBySiteNumber(oldSiteNumber);

        commonContext.setResponse(
                CampSiteApiHelper.sendCampsiteUpdateRequest(campsiteId, newSiteNumber, "수정된 설명", 4)
        );
    }

    @When("{string} 사이트 삭제를 요청한다")
    public void requestCampsiteDeletion(String siteNumber) {
        Long campsiteId = CampSiteApiHelper.findCampsiteIdBySiteNumber(siteNumber);
        commonContext.setResponse(
                CampSiteApiHelper.sendCampsiteDeletionRequest(campsiteId)
        );
    }

    @When("존재하지 않는 사이트 번호로 삭제를 요청한다")
    public void requestNonExistentCampsiteDeletion() {
        commonContext.setResponse(
                CampSiteApiHelper.sendCampsiteDeletionRequest(99999L)
        );
    }

    @Then("요청이 실패하고 오류 메시지를 받는다")
    public void assertFailedRequest() {
        commonContext.getResponse().then()
                .statusCode(greaterThanOrEqualTo(400))
                .statusCode(lessThan(500))
                .body("message", notNullValue());
    }

    @Then("캠프사이트가 성공적으로 생성된다")
    public void assertSuccessfulCreation() {
        commonContext.getResponse().then()
                .statusCode(201)
                .body(notNullValue());
    }

    @Then("캠프사이트 정보가 성공적으로 수정된다")
    public void assertSuccessfulUpdate() {
        commonContext.getResponse().then().statusCode(200);
    }

    @Then("캠프사이트가 성공적으로 삭제된다")
    public void assertSuccessfulDeletion() {
        commonContext.getResponse().then().statusCode(204);
    }

}

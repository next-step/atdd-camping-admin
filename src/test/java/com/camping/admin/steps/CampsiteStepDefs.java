package com.camping.admin.steps;

import com.camping.admin.support.ApiHelper;
import com.camping.admin.support.CommonContext;
import com.camping.admin.support.DataTableHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

public class CampsiteStepDefs {

    @When("관리자가 캠프사이트 목록을 조회한다")
    public void 관리자가캠프사이트목록을조회한다() {
        ApiHelper.makeAuthenticatedRequest("GET", "/admin/campsites", null);
    }

    @When("관리자가 다음 정보로 캠프사이트를 생성한다:")
    public void 관리자가다음정보로캠프사이트를생성한다(DataTable dataTable) {
        Map<String, Object> requestBody = DataTableHelper.buildRequestBodyFromDataTable(dataTable, true);
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/campsites", requestBody);
    }

    @Then("캠프사이트 목록이 반환된다")
    public void 캠프사이트목록이반환된다() {
        CommonContext.getLastResponse().then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("캠프사이트 정보에는 사이트 번호와 최대 인원이 포함된다")
    public void 캠프사이트정보에는사이트번호와최대인원이포함된다() {
        CommonContext.getLastResponse().then()
                .body("[0].siteNumber", notNullValue())
                .body("[0].maxPeople", notNullValue());
    }

    @And("생성된 캠프사이트 정보가 반환된다")
    public void 생성된캠프사이트정보가반환된다() {
        CommonContext.getLastResponse().then()
                .body("id", notNullValue())
                .body("siteNumber", notNullValue());
    }

    @And("캠프사이트 번호는 {string}이다")
    public void 캠프사이트번호는이다(String expectedSiteNumber) {
        // 고유 접미사를 추가했으므로 사이트 번호가 예상 값으로 시작하는지만 확인
        CommonContext.getLastResponse().then()
                .body("siteNumber", startsWith(expectedSiteNumber));
    }

    @And("최대 인원은 {int}이다")
    public void 최대인원은이다(int expectedMaxPeople) {
        CommonContext.getLastResponse().then()
                .body("maxPeople", equalTo(expectedMaxPeople));
    }

    @And("생성된 캠프사이트의 사이트 번호는 null이다")
    public void 생성된캠프사이트의사이트번호는null이다() {
        CommonContext.getLastResponse().then()
                .body("siteNumber", nullValue());
    }

    @And("생성된 캠프사이트의 최대 인원은 null이다")
    public void 생성된캠프사이트의최대인원은null이다() {
        CommonContext.getLastResponse().then()
                .body("maxPeople", nullValue());
    }

    @Given("관리자가 다음 정보로 캠프사이트를 생성했다:")
    public void 관리자가다음정보로캠프사이트를생성했다(DataTable dataTable) {
        Map<String, Object> requestBody = DataTableHelper.buildRequestBodyFromDataTable(dataTable, true);
        ApiHelper.makeAuthenticatedRequest("POST", "/admin/campsites", requestBody);
    }

    @When("권한 없는 사용자가 다음 정보로 캠프사이트를 생성한다:")
    public void 권한없는사용자가다음정보로캠프사이트를생성한다(DataTable dataTable) {
        Map<String, Object> requestBody = DataTableHelper.buildRequestBodyFromDataTable(dataTable, true);
        ApiHelper.makeUnauthenticatedRequest("POST", "/admin/campsites", requestBody);
    }

    @When("권한 없는 사용자가 캠프사이트 목록을 조회한다")
    public void 권한없는사용자가캠프사이트목록을조회한다() {
        ApiHelper.makeUnauthenticatedRequest("GET", "/admin/campsites", null);
    }
}

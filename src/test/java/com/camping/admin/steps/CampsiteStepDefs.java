package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CampsiteStepDefs {

    @When("관리자가 캠프사이트 목록을 조회한다")
    public void 관리자가캠프사이트목록을조회한다() {
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/campsites");
        CommonContext.setLastResponse(response);
    }

    @When("관리자가 다음 정보로 캠프사이트를 생성한다:")
    public void 관리자가다음정보로캠프사이트를생성한다(DataTable dataTable) {
        Map<String, String> campsiteData = dataTable.asMaps().get(0);
        
        Map<String, Object> requestBody = new java.util.HashMap<>();
        
        if (campsiteData.containsKey("siteNumber")) {
            // 중복 방지를 위해 고유 접미사 추가
            String uniqueSiteNumber = campsiteData.get("siteNumber") + "_" + System.currentTimeMillis();
            requestBody.put("siteNumber", uniqueSiteNumber);
        }
        if (campsiteData.containsKey("description")) {
            requestBody.put("description", campsiteData.get("description"));
        }
        if (campsiteData.containsKey("maxPeople")) {
            requestBody.put("maxPeople", Integer.parseInt(campsiteData.get("maxPeople")));
        }
        
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(requestBody)
                .post("/admin/campsites");
        CommonContext.setLastResponse(response);
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
}

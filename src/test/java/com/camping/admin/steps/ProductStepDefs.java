package com.camping.admin.steps;

import com.camping.admin.support.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductStepDefs {

    @When("관리자가 상품 목록을 조회한다")
    public void 관리자가상품목록을조회한다() {
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .get("/admin/products");
        CommonContext.setLastResponse(response);
    }

    @When("관리자가 다음 정보로 상품을 생성한다:")
    public void 관리자가다음정보로상품을생성한다(DataTable dataTable) {
        Map<String, String> productData = dataTable.asMaps().get(0);
        
        Map<String, Object> requestBody = new java.util.HashMap<>();
        
        if (productData.containsKey("name")) {
            // 중복 방지를 위해 고유 접미사 추가
            String uniqueName = productData.get("name") + "_" + System.currentTimeMillis();
            requestBody.put("name", uniqueName);
        }
        if (productData.containsKey("price")) {
            requestBody.put("price", Integer.parseInt(productData.get("price")));
        }
        if (productData.containsKey("stockQuantity")) {
            requestBody.put("stockQuantity", Integer.parseInt(productData.get("stockQuantity")));
        }
        if (productData.containsKey("productType")) {
            requestBody.put("productType", productData.get("productType"));
        }
        
        Response response = given().spec(CommonContext.getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(requestBody)
                .post("/admin/products");
        CommonContext.setLastResponse(response);
    }

    @Then("상품 목록이 반환된다")
    public void 상품목록이반환된다() {
        CommonContext.getLastResponse().then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @And("상품 정보에는 이름과 가격 정보가 포함된다")
    public void 상품정보에는이름과가격정보가포함된다() {
        CommonContext.getLastResponse().then()
                .body("[0].name", notNullValue())
                .body("[0].price", notNullValue())
                .body("[0].productType", notNullValue());
    }

    @And("생성된 상품 정보가 반환된다")
    public void 생성된상품정보가반환된다() {
        CommonContext.getLastResponse().then()
                .body("id", notNullValue())
                .body("name", notNullValue())
                .body("price", notNullValue());
    }

    @And("상품 이름은 {string}이다")
    public void 상품이름은이다(String expectedName) {
        // 고유 접미사를 추가했으므로 이름이 예상 값으로 시작하는지만 확인
        CommonContext.getLastResponse().then()
                .body("name", startsWith(expectedName));
    }

    @And("상품 가격은 {int}이다")
    public void 상품가격은이다(int expectedPrice) {
        CommonContext.getLastResponse().then()
                .body("price", equalTo(expectedPrice));
    }

    @But("생성된 상품의 이름은 null이다")
    public void 생성된상품의이름은null이다() {
        CommonContext.getLastResponse().then()
                .body("name", nullValue());
    }

    @And("생성된 상품의 타입은 {string}이다")
    public void 생성된상품의타입은이다(String expectedType) {
        CommonContext.getLastResponse().then()
                .body("productType", equalTo(expectedType));
    }
}

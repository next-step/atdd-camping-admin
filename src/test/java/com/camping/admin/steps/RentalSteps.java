package com.camping.admin.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;

import static com.camping.admin.helper.CommonContext.adminToken;
import static com.camping.admin.helper.CommonContext.requestSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("대여 생성 테스트")
public class RentalSteps {

    Long reservationId;
    Long productId;
    Response lastResponse;

    @Given("사용자가 예약을 했다.")
    public void 예약이_있다() {
        reservationId = 1L; // data.sql의 예약 데이터 사용
    }

    @And("제품의 재고가 있다.")
    public void 제품의_재고가_있다() {
        productId = 1L; // data.sql의 상품 데이터 사용
    }

    @When("사용자가 제품을 대여한다")
    public void 사용자가_제품을_대여한다() {
        var request = new HashMap<String, Object>();
        request.put("reservationId", reservationId);
        request.put("productId", productId);
        request.put("quantity", 1L);

        lastResponse = given().spec(requestSpec)
                .contentType(JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(request)
                .when()
                .post("/admin/rentals");
    }

    @Then("대여에 성공했다")
    public void 대여에_성공했다() {
        lastResponse.then()
                .log().all()
                .statusCode(CREATED.value());
    }

    @And("대여 ID가 생성된다")
    public void 대여_ID가_생성된다() {
        lastResponse.then()
                .body("id", notNullValue());
    }

    @And("대여된 제품의 재고가 감소했다")
    public void 제품_재고가_감소된다() {
        var response = given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/admin/products")
                .then()
                .log().all()
                .extract();

        // productId에 해당하는 제품의 stockQuantity 확인
        var actualStockQuantity = response.jsonPath()
                .getInt("find { it.id == " + productId + " }.stockQuantity");

        assertThat(actualStockQuantity).isEqualTo(19);
    }

    @And("대여 상태가 '대여중'이다") // TODO: 3단계 리팩터링에서 대여중과 false를 매핑시키는 enum 추가하기
    public void 대여상태가_대여중_이다() {
        lastResponse.then()
                .body("isReturned", equalTo(false));
    }

}

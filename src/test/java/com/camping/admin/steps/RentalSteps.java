package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.dto.CreateRentalRequest;
import com.camping.admin.dto.ReservationResponse;
import com.camping.admin.helper.CommonContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;

import static com.camping.admin.helper.CommonContext.getAdminToken;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

public class RentalSteps {

    Long reservationId;
    Product product;
    Response lastResponse;

    @Given("예약이 있다")
    @Sql(scripts = {"/sql/create-reservation.sql"})
    public void 예약이_있다() {
        var response = given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + getAdminToken())
                .when()
                .get("/admin/reservations")
                .then()
                .log().all()
                .extract();

        reservationId = response.jsonPath()
                .getObject("reservationResponse", ReservationResponse.class)
                .getId();
    }

    @And("제품의 재고가 {int}개 있다")
    public void 제품의_재고가_있다(Integer stockQuantity) {
        var request = new HashMap<String, Object>();
        request.put("name", "화로");
        request.put("stockQuantity", stockQuantity);
        request.put("price", 10000L);
        request.put("productType", ProductType.RENTAL);

        var response = given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + getAdminToken())
                .body(request)
                .when()
                .post("/admin/products")
                .then()
                .log().all()
                .extract();

        product = response.jsonPath().getObject("product", Product.class);
    }

    @When("사용자가 제품 {int}개를 대여한다")
    public void 사용자가_제품을_대여한다(int rentalQuantity) {
        var request = new CreateRentalRequest();
        request.setReservationId(reservationId);
        request.setProductId(product.getId());
        request.setQuantity(rentalQuantity);

        lastResponse = given().spec(CommonContext.getRequestSpec())
                .contentType(JSON)
                .header("Authorization", "Bearer " + getAdminToken())
                .body(request)
                .when()
                .post("/admin/rentals");
    }

    @Then("대여에 성공했다")
    public void 대여에_성공했다() {
        lastResponse.then()
                .log().all()
                .statusCode(200);
    }

    @And("대여 ID가 생성된다.")
    public void 대여_ID가_생성된다() {
        lastResponse.then()
                .body("id", notNullValue());
    }

    @And("제품 재고가 {int}개로 감소된다")
    public void 제품_재고가_감소된다(int stockQuantity) {
        var response = given()
                .contentType(JSON)
                .header("Authorization", "Bearer " + getAdminToken())
                .when()
                .get("/admin/products")
                .then()
                .log().all()
                .extract();

        var updatedProduct = response.jsonPath().getList("result", Product.class)
                .stream().filter(p -> p.getId().equals(product.getId()))
                .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Cannot find product with id: " + product.getId()));

        Assertions.assertThat(updatedProduct.getStockQuantity()).isEqualTo(stockQuantity);
    }

    @And("대여 상태가 '대여중'이다") // TODO: 3단계 리팩터링에서 대여중과 false를 매핑시키는 enum 추가하기
    public void 대여상태가_대여중_이다() {
        lastResponse.then()
                .body("isReturned", equalTo(false));
    }

}

package com.camping.admin.acceptance;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

// 이 클래스는 상품(Product) 도메인의 인수 테스트를 모은다. 티켓별 세부 내용은 각 @Nested 위 주석에 적는다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductAcceptanceTest {

    @LocalServerPort
    private int port;

    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.config = RestAssured.config()
                .encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"));

        accessToken = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\",\"password\":\"admin123\"}")
                .when().post("/auth/login")
                .then().statusCode(200)
                .extract().path("accessToken");
    }

    // T-1: 상품 수정 요청은 302로 성공 응답을 주고, 조회 시 수정된 값으로 나온다.
    @Nested
    class T1_상품수정이저장되지않음 {

        @Test
        @Sql("/sql/product-reset.sql")
        void 이름을_수정하면_조회시_수정된_이름으로_나와야_한다() {
            given()
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(ContentType.URLENC)
                    .formParam("name", "랜턴-수정됨")
                    .formParam("stockQuantity", "20")
                    .formParam("price", "30000")
                    .formParam("productType", "RENTAL")
                    .when().post("/console/products/1")
                    .then().statusCode(302);

            String editPage = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/console/products/1/edit")
                    .then().statusCode(200)
                    .extract().asString();

            org.assertj.core.api.Assertions.assertThat(editPage).contains("랜턴-수정됨");
        }
    }
}

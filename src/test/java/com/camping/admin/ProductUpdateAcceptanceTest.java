package com.camping.admin;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {
        "DELETE FROM products WHERE id = 1001",
        "INSERT INTO products (id, name, stock_quantity, price, product_type) "
                + "VALUES (1001, '테스트 랜턴', 20, 30000.00, 'RENTAL')"
})
class ProductUpdateAcceptanceTest {

    private static final long PRODUCT_ID = 1001L;

    @LocalServerPort
    private int port;

    @Test
    void shouldPersistPositiveStockQuantity() {
        String accessToken = login();

        Response updateResponse = updateProduct(accessToken, Map.of("stockQuantity", 21));

        assertUpdateResponse(updateResponse, "테스트 랜턴", 21, "30000.00", "RENTAL");
        assertStoredProduct(accessToken, "테스트 랜턴", 21, "30000.00", "RENTAL");
    }

    @Test
    void shouldPersistZeroStockQuantity() {
        String accessToken = login();

        Response updateResponse = updateProduct(accessToken, Map.of("stockQuantity", 0));

        assertUpdateResponse(updateResponse, "테스트 랜턴", 0, "30000.00", "RENTAL");
        assertStoredProduct(accessToken, "테스트 랜턴", 0, "30000.00", "RENTAL");
    }

    @Test
    void shouldPreserveStockAndPriceWhenOnlyNameIsSubmitted() {
        String accessToken = login();

        Response updateResponse = updateProduct(accessToken, Map.of("name", "이름만 변경"));

        assertThat(updateResponse.statusCode()).isEqualTo(200);
        assertStockAndPrice(updateResponse.jsonPath().getMap("$"), 20, "30000.00");
        Map<String, Object> storedProduct = findProduct(accessToken);
        assertStockAndPrice(storedProduct, 20, "30000.00");
    }

    private String login() {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    private Response updateProduct(String accessToken, Map<String, Object> body) {
        return given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
        .when()
                .put("/admin/products/{id}", PRODUCT_ID);
    }

    private void assertUpdateResponse(
            Response updateResponse,
            String expectedName,
            int expectedStockQuantity,
            String expectedPrice,
            String expectedProductType
    ) {
        assertThat(updateResponse.statusCode()).isEqualTo(200);
        assertProduct(
                updateResponse.jsonPath().getMap("$"),
                expectedName,
                expectedStockQuantity,
                expectedPrice,
                expectedProductType);
    }

    private void assertStoredProduct(
            String accessToken,
            String expectedName,
            int expectedStockQuantity,
            String expectedPrice,
            String expectedProductType
    ) {
        assertProduct(
                findProduct(accessToken),
                expectedName,
                expectedStockQuantity,
                expectedPrice,
                expectedProductType);
    }

    private void assertProduct(
            Map<String, Object> product,
            String expectedName,
            int expectedStockQuantity,
            String expectedPrice,
            String expectedProductType
    ) {
        assertThat(product.get("name")).isEqualTo(expectedName);
        assertThat(product.get("stockQuantity")).isEqualTo(expectedStockQuantity);
        assertThat(new BigDecimal(product.get("price").toString()))
                .isEqualByComparingTo(expectedPrice);
        assertThat(product.get("productType")).isEqualTo(expectedProductType);
    }

    private void assertStockAndPrice(
            Map<String, Object> product,
            int expectedStockQuantity,
            String expectedPrice
    ) {
        assertThat(product.get("stockQuantity")).isEqualTo(expectedStockQuantity);
        assertThat(new BigDecimal(product.get("price").toString()))
                .isEqualByComparingTo(expectedPrice);
    }

    private Map<String, Object> findProduct(String accessToken) {
        return given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .accept(ContentType.JSON)
        .when()
                .get("/admin/products")
        .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getMap("find { it.id == " + PRODUCT_ID + " }");
    }
}

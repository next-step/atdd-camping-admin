package com.camping.admin.steps.common;

import com.camping.admin.repository.ProductRepository;
import com.camping.admin.support.ApiClient;
import com.camping.admin.support.ScenarioContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductRegistrationSteps {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private ApiClient apiClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before("@product-registration")
    public void cleanupProducts() {
        long nextId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id), 0) + 1 FROM products", Long.class
        );
        jdbcTemplate.execute("ALTER TABLE products ALTER COLUMN id RESTART WITH " + nextId);
    }

    // ===== 공통 헬퍼 메서드 =====

    private void requestProductRegistration(String body) {
        Response response = apiClient.post("/admin/products", body);
        scenarioContext.setResponse(response);
    }

    private String buildProductJson(Map<String, String> data) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!first) json.append(",");
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("stockQuantity") || key.equals("price")) {
                json.append("\"").append(key).append("\":").append(value);
            } else {
                json.append("\"").append(key).append("\":\"").append(value).append("\"");
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    // ===== When =====

    @When("다음 정보로 상품 등록을 요청한다:")
    public void 다음_정보로_상품_등록을_요청한다(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String body = buildProductJson(data);
        requestProductRegistration(body);
    }

    @When("상품명 {string}만 입력하여 상품 등록을 요청한다")
    public void 상품명_만_입력하여_상품_등록을_요청한다(String name) {
        String body = String.format("{\"name\":\"%s\"}", name);
        requestProductRegistration(body);
    }

    @When("상품명 없이 상품 등록을 요청한다")
    public void 상품명_없이_상품_등록을_요청한다() {
        requestProductRegistration("{\"stockQuantity\":10,\"price\":10000}");
    }

    @When("빈 요청 본문으로 상품 등록을 요청한다")
    public void 빈_요청_본문으로_상품_등록을_요청한다() {
        requestProductRegistration("{}");
    }

    @When("재고에 {string}을 입력하여 상품 등록을 요청한다")
    public void 재고에_을_입력하여_상품_등록을_요청한다(String stockQuantity) {
        String body = String.format("{\"name\":\"테스트 상품\",\"stockQuantity\":%s}", stockQuantity);
        requestProductRegistration(body);
    }

    // ===== Then =====

    @Then("상품이 성공적으로 등록된다")
    public void 상품이_성공적으로_등록된다() {
        Response response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Then("상품 등록이 실패한다")
    public void 상품_등록이_실패한다() {
        Response response = scenarioContext.getResponse();
        assertThat(response.statusCode()).isIn(400, 500);
    }

    @And("등록된 상품의 이름은 {string}이다")
    public void 등록된_상품의_이름은_이다(String expectedName) {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getString("name")).isEqualTo(expectedName);
    }

    @And("등록된 상품의 재고는 {int}개이다")
    public void 등록된_상품의_재고는_개이다(int expectedStock) {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getInt("stockQuantity")).isEqualTo(expectedStock);
    }

    @And("등록된 상품의 가격은 {int}원이다")
    public void 등록된_상품의_가격은_원이다(int expectedPrice) {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getInt("price")).isEqualTo(expectedPrice);
    }

    @And("등록된 상품의 유형은 {string}이다")
    public void 등록된_상품의_유형은_이다(String expectedType) {
        Response response = scenarioContext.getResponse();
        assertThat(response.jsonPath().getString("productType")).isEqualTo(expectedType);
    }
}

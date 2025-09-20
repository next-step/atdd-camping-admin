package features;

import io.cucumber.java.Status;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class MyStepdefs {

    String adminToken;
    Map<String, Object> campingSite;

    @Given("관리자가 로그인했다")
    public void 관리자가로그인했다() {
        System.out.println("관리자가 로그인 했다");
        adminToken = given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
                .when().post("/auth/login")
                .then()
                .extract()
                .cookie("AUTH_TOKEN");
    }

    @Given("등록할 캠핑 사이트 정보가 있다")
    public void 등록할캠핑사이트정보가있다() {
        System.out.println("등록할 캠핑 사이트 정보가 있다");
        campingSite = Map.of(
                "siteNumber", "A-03",
                "description", "마운틴 뷰, 와이파이 사용 가능",
                "maxPeople", 5);
    }

    @When("캠핑 사이트를 등록하면")
    public void 캠핑사이트를등록하면() {
        System.out.println("캠핑 사이트를 등록하면");

        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(campingSite)
                .when()
                .post("/admin/campsites")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Then("캠핑 사이트가 등록된다")
    public void 캠핑사이트가등록된다() {
        System.out.println("캠핑 사이트가 등록된다");

        ExtractableResponse<Response> campingSites = RestAssured.given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/admin/campsites")
                .then()
                .extract();

        List<String> siteNumbers = campingSites.jsonPath().getList("siteNumber");
        assertThat(siteNumbers).contains(campingSite.get("siteNumber").toString());
    }
}

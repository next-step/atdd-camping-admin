package com.camping.admin.steps;

import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.security.JwtService;
import io.cucumber.java.Before;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 모든 시나리오에서 공통으로 사용되는 Cucumber Hooks
 */
public class CommonHooks extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Value("${admin.username}")
    private String adminUsername;

    // 테스트 컨텍스트 공유용 (다른 Step 클래스에서 접근 가능)
    public static String adminToken;
    public static Response lastResponse;

    @Before(order = 0)
    public void setupRestAssured() {
        RestAssured.port = port;
    }

    @Before(order = 1)
    public void setupAdminToken() {
        adminToken = jwtService.generateToken(adminUsername);
    }

    @Given("관리자가 로그인을 했다")
    public void 관리자가_로그인을_했다() {
        // @Before 훅에서 이미 처리됨 - Background 문서화 목적
    }

    // ==================== 공통 Then ====================

    @Then("응답 상태 코드는 {int}이다")
    public void 응답_상태_코드는_N이다(int expectedStatusCode) {
        assertThat(lastResponse.statusCode()).isEqualTo(expectedStatusCode);
    }

    // ==================== Parameter Types ====================

    @ParameterType("취소|확정|대기|유효하지 않음")
    public String 예약상태(String status) {
        ReservationStatus found = ReservationStatus.fromDisplayName(status);
        return found != null ? found.name() : status;
    }
}
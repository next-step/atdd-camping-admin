package com.camping.admin.common;

import com.camping.admin.CucumberSpringConfiguration;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 모든 시나리오에서 공통으로 사용되는 Cucumber Hooks
 */
public class CommonHooks extends CucumberSpringConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Value("${admin.username}")
    private String adminUsername;

    // 테스트 컨텍스트 공유용 (다른 Step 클래스에서 접근 가능)
    public static String adminToken;
    public static Response lastResponse;

    @Before(order = 0)
    public void cleanupDatabase() {
        // 1. 모든 테이블 초기화
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'",
                String.class
        );

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY");
        }

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");

        // 2. data.sql 시드 데이터 재삽입
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("data.sql"));
        } catch (SQLException e) {
            throw new RuntimeException("data.sql 실행 실패", e);
        }
    }

    @Before(order = 1)
    public void setupRestAssured() {
        RestAssured.port = port;
    }

    @Before(order = 2)
    public void setupAdminToken() {
        adminToken = jwtService.generateToken(adminUsername);
    }

    @Given("관리자가 로그인되어 있다")
    public void 관리자가_로그인되어_있다() {
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
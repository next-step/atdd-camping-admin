package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.entity.SalesRecord;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.SalesRecordRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class revenueReportSteps {

    @Autowired private TestContext context;
    @Autowired private ProductRepository productRepository;
    @Autowired private SalesRecordRepository salesRecordRepository;

    @Given("판매 상품과 판매 기록이 존재한다")
    public void 판매상품과판매기록이존재한다() {
        Product product = productRepository.save(
                new Product("장작팩", 50, BigDecimal.valueOf(10000), ProductType.SALE)
        );
        salesRecordRepository.save(new SalesRecord(product, 3, BigDecimal.valueOf(30000)));
    }

    @When("오늘 날짜로 일별 매출 리포트를 조회한다")
    public void 오늘날짜로일별매출리포트를조회한다() {
        context.response = context.authRequest()
                .queryParam("date", LocalDate.now().toString())
                .get("/admin/reports/revenue/daily");
    }

    @When("최근 7일 기간으로 매출 리포트를 조회한다")
    public void 최근7일기간으로매출리포트를조회한다() {
        context.response = context.authRequest()
                .queryParam("from", LocalDate.now().minusDays(7).toString())
                .queryParam("to", LocalDate.now().toString())
                .get("/admin/reports/revenue/range");
    }

    @When("최근 30일 기간으로 매출 상세 내역을 조회한다")
    public void 최근30일기간으로매출상세내역을조회한다() {
        context.response = context.authRequest()
                .queryParam("from", LocalDate.now().minusDays(30).toString())
                .queryParam("to", LocalDate.now().toString())
                .get("/admin/reports/revenue/range/entries");
    }

    @When("판매 기록이 없는 날짜의 일별 매출 리포트를 조회한다")
    public void 판매기록이없는날짜의일별매출리포트를조회한다() {
        context.response = context.authRequest()
                .queryParam("date", "2020-01-01")
                .get("/admin/reports/revenue/daily");
    }

    @And("매출 합계가 반환된다")
    public void 매출합계가반환된다() {
        context.response.then().body("grandTotalRevenue", notNullValue());
    }

    @And("매출 내역이 반환된다")
    public void 매출내역이반환된다() {
        context.response.then().body("$", not(empty()));
    }

    // BigDecimal 비교는 타입 정밀도가 중요하므로 AssertJ 사용
    @And("매출 합계는 0이다")
    public void 매출합계는0이다() {
        BigDecimal total = context.response.jsonPath().getObject("grandTotalRevenue", BigDecimal.class);
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // ── 예외 시나리오 ──────────────────────────────────────────

    @When("잘못된 날짜 형식으로 일별 매출 리포트를 조회한다")
    public void 잘못된날짜형식으로일별매출리포트를조회한다() {
        context.response = context.authRequest()
                .queryParam("date", "not-a-date")
                .get("/admin/reports/revenue/daily");
    }

    @When("from 파라미터 없이 기간별 매출 리포트를 조회한다")
    public void from파라미터없이기간별매출리포트를조회한다() {
        context.response = context.authRequest()
                .queryParam("to", LocalDate.now().toString())
                .get("/admin/reports/revenue/range");
    }

    @When("to 파라미터 없이 기간별 매출 리포트를 조회한다")
    public void to파라미터없이기간별매출리포트를조회한다() {
        context.response = context.authRequest()
                .queryParam("from", LocalDate.now().minusDays(7).toString())
                .get("/admin/reports/revenue/range");
    }

    @When("시작일이 종료일보다 늦은 기간으로 매출 리포트를 조회한다")
    public void 시작일이종료일보다늦은기간으로매출리포트를조회한다() {
        context.response = context.authRequest()
                .queryParam("from", LocalDate.now().toString())
                .queryParam("to", LocalDate.now().minusDays(7).toString())
                .get("/admin/reports/revenue/range");
    }

    @When("판매 기록이 없는 기간의 매출 상세 내역을 조회한다")
    public void 판매기록이없는기간의매출상세내역을조회한다() {
        context.response = context.authRequest()
                .queryParam("from", "2020-01-01")
                .queryParam("to", "2020-01-07")
                .get("/admin/reports/revenue/range/entries");
    }

    @Then("조회가 거부된다")
    public void 조회가거부된다() {
        context.response.then().statusCode(400);
    }
}

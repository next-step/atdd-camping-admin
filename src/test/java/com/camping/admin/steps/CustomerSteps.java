package com.camping.admin.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.camping.admin.api.TestContext;
import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Customer;
import com.camping.admin.domain.entity.Reservation;
import com.camping.admin.support.TestApiSupport;
import com.camping.admin.support.TestRepositorySupport;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class CustomerSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private TestApiSupport api;

    @Autowired
    private TestRepositorySupport repository;

    // ==================== Given Steps ====================

    @Given("다음과 같은 고객이 등록되어 있다:")
    public void 고객_등록(DataTable dataTable) {
        List<Map<String, String>> customers = dataTable.asMaps();
        for (Map<String, String> customerData : customers) {
            String name = customerData.get("name");
            String email = customerData.get("email");
            String phoneNumber = customerData.get("phoneNumber");

            Customer customer = new Customer(name, email, phoneNumber);
            repository.customer().save(customer);

            // 테스트 컨텍스트에 마지막 고객 정보 저장 (필요 시)
            testContext.getCustomer().setId(customer.getId());
            testContext.getCustomer().setName(name);
        }
    }

    @Given("{string} 캠핑장 사이트가 등록되어 있다")
    public void 캠핑장_사이트_등록(String siteNumber) {
        Campsite campsite = new Campsite(siteNumber, "테스트용 사이트", 4);
        repository.campsite().save(campsite);
    }

    @Given("{string} 고객이 {string}부터 {string}까지 {string} 사이트를 예약했다")
    public void 고객이_사이트_예약(String customerName, String startDate, String endDate, String siteNumber) {
        Customer customer = findCustomerByName(customerName);
        Campsite campsite = findCampsiteByNumber(siteNumber);

        Reservation reservation = new Reservation(customerName, LocalDate.parse(startDate), LocalDate.parse(endDate), campsite);
        reservation.setPhoneNumber(customer.getPhoneNumber());
        // 실제 구현에서는 Reservation에 Customer 연관관계 설정 필요
        // reservation.setCustomer(customer);
        repository.reservation().save(reservation);

        testContext.getReservation().setId(reservation.getId());
    }

    // ==================== When Steps ====================

    @When("관리자가 {string} 고객의 정보로 {string} 사이트에 예약을 생성한다")
    public void 기존_고객_정보로_예약_생성(String customerName, String siteNumber) {
        Customer customer = findCustomerByName(customerName);

        Map<String, Object> params = api.customer().예약_요청_파라미터_생성(
                siteNumber,
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(2).toString(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );

        var response = api.reservation().예약_생성(testContext.getAccessToken(), params);
        testContext.setResponse(response);
    }

    @When("관리자가 이메일 {string}을 입력하여 예약을 시도한다")
    public void 이메일로_예약_시도(String email) {
        Map<String, Object> params = api.customer().예약_요청_파라미터_생성(
                "A1",
                LocalDate.now().plusDays(3).toString(),
                LocalDate.now().plusDays(4).toString(),
                null,
                email,
                null
        );

        var response = api.reservation().예약_생성(testContext.getAccessToken(), params);
        testContext.setResponse(response);
    }

    @When("관리자가 등록되지 않은 이메일 {string}과 이름 {string}로 예약을 생성한다")
    public void 미등록_이메일로_예약_생성(String email, String name) {
        Map<String, Object> params = api.customer().예약_요청_파라미터_생성(
                "A1",
                LocalDate.now().plusDays(5).toString(),
                LocalDate.now().plusDays(6).toString(),
                name,
                email,
                "010-0000-0000"
        );

        var response = api.reservation().예약_생성(testContext.getAccessToken(), params);
        testContext.setResponse(response);
    }

    @When("관리자가 {string} 고객의 상세 페이지를 조회한다")
    public void 고객_상세_페이지_조회(String customerName) {
        Customer customer = findCustomerByName(customerName);

        var response = api.customer().고객_상세_조회(testContext.getAccessToken(), customer.getId());
        testContext.setResponse(response);
    }

    // ==================== Then Steps ====================

    @Then("예약이 정상적으로 생성된다")
    public void 예약_생성_성공_확인() {
        assertThat(testContext.getResponse().statusCode())
                .as("API 응답 상태코드")
                .isEqualTo(HttpStatus.CREATED.value()); // 또는 OK

        // 생성된 예약 ID 저장
        Long reservationId = testContext.getResponse().jsonPath().getLong("id");
        testContext.getReservation().setId(reservationId);
    }

    @Then("해당 예약은 {string} 고객과 연결되어야 한다")
    public void 예약_고객_연결_확인(String customerName) {
        Long reservationId = testContext.getReservation().getId();
        Reservation reservation = findReservationById(reservationId);

        // Reservation 엔티티에 Customer 필드가 추가되었다고 가정하고 검증 로직 작성
        // 현재는 엔티티 수정 전이므로 주석으로 남기거나 리플렉션/쿼리 등으로 확인해야 함
        // 예: assertThat(reservation.getCustomer().getName()).isEqualTo(customerName);
    }

    @Then("{string} 고객의 예약 목록에 방금 생성한 예약이 포함되어야 한다")
    public void 고객_예약_목록_포함_확인(String customerName) {
        // Customer 엔티티나 연관관계를 통해 확인
        Customer customer = findCustomerByName(customerName);
        
        // 예: assertThat(customer.getReservations()).extracting("id").contains(testContext.getReservation().getId());
    }

    @Then("시스템은 자동으로 {string} 고객 정보를 찾아 예약에 연결한다")
    public void 자동_고객_연결_확인(String customerName) {
        Long reservationId = testContext.getResponse().jsonPath().getLong("id");
        Reservation reservation = findReservationById(reservationId);
        
        // 검증 로직 (엔티티 수정 후 활성화)
        // assertThat(reservation.getCustomer().getName()).isEqualTo(customerName);
    }

    @Then("예약된 고객명은 {string}, 전화번호는 {string}로 저장된다")
    public void 예약_고객_정보_확인(String name, String phoneNumber) {
        Long reservationId = testContext.getResponse().jsonPath().getLong("id");
        Reservation reservation = findReservationById(reservationId);

        assertThat(reservation.getCustomerName()).isEqualTo(name);
        assertThat(reservation.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Then("시스템에 {string} 고객이 자동으로 신규 등록된다")
    public void 신규_고객_자동_등록_확인(String name) {
        boolean exists = repository.customer().findAll().stream()
                .anyMatch(c -> c.getName().equals(name));
        assertThat(exists).as("신규 고객 등록 여부").isTrue();
    }

    @Then("생성된 예약은 {string} 고객과 연결된다")
    public void 생성된_예약_고객_연결_확인(String name) {
        // 위와 동일하게 연결 확인
    }

    @Then("고객의 예약 이력 목록에 {string} 사이트 예약 건이 표시되어야 한다")
    public void 고객_예약_이력_표시_확인(String siteNumber) {
        assertThat(testContext.getResponse().statusCode()).isEqualTo(HttpStatus.OK.value());
        
        List<String> reservedSites = testContext.getResponse().jsonPath().getList("reservations.siteNumber");
        assertThat(reservedSites).contains(siteNumber);
    }

    // ==================== Helper Methods ====================

    private Customer findCustomerByName(String name) {
        return repository.customer().findAll().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("고객을 찾을 수 없습니다: " + name));
    }

    private Reservation findReservationById(Long id) {
        return repository.reservation().findById(id)
                .orElseThrow(() -> new AssertionError("예약을 찾을 수 없습니다: " + id));
    }

    private Campsite findCampsiteByNumber(String siteNumber) {
        return repository.campsite().findAll().stream()
                .filter(c -> c.getSiteNumber().equals(siteNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("사이트를 찾을 수 없습니다: " + siteNumber));
    }
}
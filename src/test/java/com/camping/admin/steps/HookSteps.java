package com.camping.admin.steps;

import com.camping.admin.repository.CampsiteRepository;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.repository.RentalRecordRepository;
import com.camping.admin.repository.ReservationRepository;
import com.camping.admin.repository.SalesRecordRepository;
import com.camping.admin.support.TestContext;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class HookSteps {

    @Autowired private TestContext context;
    @Autowired private RentalRecordRepository rentalRecordRepository;
    @Autowired private SalesRecordRepository salesRecordRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private CampsiteRepository campsiteRepository;
    @Autowired private ProductRepository productRepository;

    @Before
    public void beforeScenario() {
        // FK 제약 순서대로 삭제
        rentalRecordRepository.deleteAllInBatch();
        salesRecordRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        campsiteRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();

        // 컨텍스트 초기화
        context.response = null;
        context.campsiteId = null;
        context.reservationId = null;
        context.productId = null;
        context.productId2 = null;
        context.rentalRecordId = null;
        context.productStockBefore = null;
        context.productStockBefore2 = null;

        // JWT 토큰 발급
        context.jwtToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", "admin", "password", "admin123"))
                .post("/auth/login")
                .jsonPath()
                .getString("accessToken");
    }
}

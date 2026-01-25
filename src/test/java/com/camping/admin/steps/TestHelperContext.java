package com.camping.admin.steps;

import com.camping.admin.helper.*;
import io.restassured.specification.RequestSpecification;

/**
 * 도메인별 TestHelper 인스턴스를 관리하는 컨텍스트
 * Cucumber DI를 통해 Steps 간 Helper 공유
 */
public class TestHelperContext {

    private ProductTestHelper productHelper;
    private CampsiteTestHelper campsiteHelper;
    private RentalTestHelper rentalHelper;
    private SalesTestHelper salesHelper;
    private RevenueTestHelper revenueHelper;
    private ReservationTestHelper reservationHelper;

    public void initialize(RequestSpecification requestSpec) {
        this.productHelper = new ProductTestHelper(requestSpec);
        this.campsiteHelper = new CampsiteTestHelper(requestSpec);
        this.rentalHelper = new RentalTestHelper(requestSpec);
        this.salesHelper = new SalesTestHelper(requestSpec);
        this.revenueHelper = new RevenueTestHelper(requestSpec);
        this.reservationHelper = new ReservationTestHelper(requestSpec);
    }

    public ProductTestHelper product() {
        return productHelper;
    }

    public CampsiteTestHelper campsite() {
        return campsiteHelper;
    }

    public RentalTestHelper rental() {
        return rentalHelper;
    }

    public SalesTestHelper sales() {
        return salesHelper;
    }

    public RevenueTestHelper revenue() {
        return revenueHelper;
    }

    public ReservationTestHelper reservation() {
        return reservationHelper;
    }
}
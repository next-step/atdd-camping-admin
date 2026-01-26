package com.camping.admin.steps;

import com.camping.admin.helper.*;
import io.restassured.specification.RequestSpecification;

/**
 * 도메인별 ApiClient 인스턴스를 관리하는 컨텍스트
 * Cucumber DI를 통해 Steps 간 ApiClient 공유
 */
public class ApiClientContext {

    private ProductApiClient productApiClient;
    private CampsiteApiClient campsiteApiClient;
    private RentalApiClient rentalApiClient;
    private SalesApiClient salesApiClient;
    private RevenueApiClient revenueApiClient;
    private ReservationApiClient reservationApiClient;

    public void initialize(RequestSpecification requestSpec) {
        this.productApiClient = new ProductApiClient(requestSpec);
        this.campsiteApiClient = new CampsiteApiClient(requestSpec);
        this.rentalApiClient = new RentalApiClient(requestSpec);
        this.salesApiClient = new SalesApiClient(requestSpec);
        this.revenueApiClient = new RevenueApiClient(requestSpec);
        this.reservationApiClient = new ReservationApiClient(requestSpec);
    }

    public ProductApiClient product() {
        return productApiClient;
    }

    public CampsiteApiClient campsite() {
        return campsiteApiClient;
    }

    public RentalApiClient rental() {
        return rentalApiClient;
    }

    public SalesApiClient sales() {
        return salesApiClient;
    }

    public RevenueApiClient revenue() {
        return revenueApiClient;
    }

    public ReservationApiClient reservation() {
        return reservationApiClient;
    }
}

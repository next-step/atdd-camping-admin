package com.camping.admin.domain;

import java.math.BigDecimal;

/**
 * 매출을 발생시키는 도메인 객체의 공통 인터페이스
 * - RentalRecord (대여)
 * - SalesRecord (판매)
 * - Reservation (예약)
 */
public interface RevenueSource {

    BigDecimal calculateRevenue();
}
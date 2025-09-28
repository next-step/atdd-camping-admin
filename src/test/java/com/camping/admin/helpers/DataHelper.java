package com.camping.admin.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class DataHelper {
    private static final AtomicLong COUNTER = new AtomicLong(1);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static class Reservation {
        public static Map<String, Object> create() {
            return create("CONFIRMED");
        }

        public static Map<String, Object> create(String status) {
            long id = COUNTER.getAndIncrement();
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = checkIn.plusDays(2);

            return Map.of(
                "customerName", "테스트고객" + id,
                "customerPhone", "010-1234-" + String.format("%04d", id),
                "checkInDate", checkIn.format(DATE_FORMAT),
                "checkOutDate", checkOut.format(DATE_FORMAT),
                "status", status
            );
        }

        public static Map<String, Object> createWithDates(String status, LocalDate checkIn, LocalDate checkOut) {
            long id = COUNTER.getAndIncrement();
            return Map.of(
                "customerName", "테스트고객" + id,
                "customerPhone", "010-1234-" + String.format("%04d", id),
                "checkInDate", checkIn.format(DATE_FORMAT),
                "checkOutDate", checkOut.format(DATE_FORMAT),
                "status", status
            );
        }

        public static Map<String, Object> createWithCustomer(String status, String customerName, String phone) {
            LocalDate checkIn = LocalDate.now().plusDays(1);
            LocalDate checkOut = checkIn.plusDays(2);

            return Map.of(
                "customerName", customerName,
                "customerPhone", phone,
                "checkInDate", checkIn.format(DATE_FORMAT),
                "checkOutDate", checkOut.format(DATE_FORMAT),
                "status", status
            );
        }
    }

    public static class User {
        public static Map<String, Object> createAdmin() {
            return Map.of(
                "username", "admin",
                "password", "admin123"
            );
        }

        public static Map<String, Object> create(String username, String password) {
            return Map.of(
                "username", username,
                "password", password
            );
        }
    }

    public static class StatusUpdate {
        public static Map<String, Object> create(String status) {
            return Map.of("status", status);
        }
    }
}
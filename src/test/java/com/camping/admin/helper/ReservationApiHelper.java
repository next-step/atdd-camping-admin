package com.camping.admin.helper;

import static com.camping.admin.context.CommonContext.getRequestSpec;
import static io.restassured.RestAssured.given;

import com.camping.admin.context.CommonContext;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReservationApiHelper {

    private static Map<String, Object> createReservationData(Long campsiteId) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate checkoutDate = tomorrow.plusDays(3);

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("campsiteId", campsiteId);
        reservation.put("customerName", "테스트 고객");
        reservation.put("customerPhone", "010-1234-5678");
        reservation.put("checkInDate", tomorrow.toString());
        reservation.put("checkOutDate", checkoutDate.toString());
        reservation.put("numberOfPeople", 2);

        return reservation;
    }

    public static void createReservationRequest(Long campsiteId) {
        Map<String, Object> reservation = createReservationData(campsiteId);

        Response response = given()
                .spec(getRequestSpec())
                .header("Authorization", "Bearer " + CommonContext.getAdminToken())
                .body(reservation)
                .when()
                .post("/admin/reservations");

        response.then().statusCode(201);
    }
}

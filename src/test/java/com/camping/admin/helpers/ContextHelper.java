package com.camping.admin.helpers;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextHelper {
    private static final ThreadLocal<ContextHelper> THREAD_LOCAL = ThreadLocal.withInitial(ContextHelper::new);

    private static final String RESERVATION_ID_KEY = "reservationId";
    private static final String RESPONSE_KEY = "response";
    private static final String CREATED_RESERVATIONS_KEY = "createdReservations";

    private final Map<String, Object> data = new HashMap<>();

    private static ContextHelper get() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

    public static Long getReservationId() {
        return get().getData(RESERVATION_ID_KEY, Long.class);
    }

    public static void setReservationId(Long reservationId) {
        get().setData(RESERVATION_ID_KEY, reservationId);
    }

    public static Response getLastResponse() {
        return get().getData(RESPONSE_KEY, Response.class);
    }

    public static void setResponse(Response response) {
        get().setData(RESPONSE_KEY, response);
    }

    @SuppressWarnings("unchecked")
    public static List<Long> getCreatedReservations() {
        return get().getData(CREATED_RESERVATIONS_KEY, List.class);
    }

    public static void addCreatedReservation(Long reservationId) {
        List<Long> createdReservations = getCreatedReservations();
        if (createdReservations != null) {
            createdReservations.add(reservationId);
        } else {
            get().setData(CREATED_RESERVATIONS_KEY, List.of(reservationId));
        }
    }

    public static void clearContext() {
        clear();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> type) {
        return (T) get().data.get(key);
    }

    public static void set(String key, Object value) {
        get().data.put(key, value);
    }

    public static void remove(String key) {
        get().data.remove(key);
    }

    @SuppressWarnings("unchecked")
    private <T> T getData(String key, Class<T> type) {
        return (T) data.get(key);
    }

    private void setData(String key, Object value) {
        data.put(key, value);
    }
}
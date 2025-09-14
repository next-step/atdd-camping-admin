package com.camping.admin.steps.test_context.campsite;

import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class CampsiteTestContext {

    private static Map<Key, Object> context = new HashMap<>();

    public void clear() {
        context.clear();
    }

    public Response 캠프사이트_생성_응답() {
        return (Response) context.get(Key.CAMPSITE_CREATE_RESPONSE);
    }

    public void 캠프사이트_생성_응답(Response 캠프사이트_생성_응답) {
        context.put(Key.CAMPSITE_CREATE_RESPONSE, 캠프사이트_생성_응답);
    }

    public Response 전체_캠프사이트_조회_응답() {
        return (Response) context.get(Key.CAMPSITE_LIST_RESPONSE);
    }

    public void 전체_캠프사이트_조회_응답(Response 전체_캠프사이트_조회_응답) {
        context.put(Key.CAMPSITE_LIST_RESPONSE, 전체_캠프사이트_조회_응답);
    }

    private enum Key {
        CAMPSITE_CREATE_RESPONSE,
        CAMPSITE_LIST_RESPONSE,
    }
}

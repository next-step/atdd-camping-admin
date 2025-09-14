package com.camping.admin.steps.test_context.campsite;

import io.restassured.response.Response;
import java.util.EnumMap;
import java.util.Map;

public class CampsiteTestContext {

    private final Map<Key, Object> context = new EnumMap<>(Key.class);

    public void clear() {
        context.clear();
    }

    public Response 캠프사이트_생성_응답() {
        return (Response) context.get(Key.CAMPSITE_CREATE_RESPONSE);
    }

    public void 캠프사이트_생성_응답(Response 캠프사이트_생성_응답) {
        context.put(Key.CAMPSITE_CREATE_RESPONSE, 캠프사이트_생성_응답);
    }

    public Response 캠프사이트_수정_응답() {
        return (Response) context.get(Key.CAMPSITE_UPDATE_RESPONSE);
    }

    public void 캠프사이트_수정_응답(Response 캠프사이트_수정_응답) {
        context.put(Key.CAMPSITE_UPDATE_RESPONSE, 캠프사이트_수정_응답);
    }

    public Response 전체_캠프사이트_조회_응답() {
        return (Response) context.get(Key.CAMPSITE_LIST_RESPONSE);
    }

    public void 전체_캠프사이트_조회_응답(Response 전체_캠프사이트_조회_응답) {
        context.put(Key.CAMPSITE_LIST_RESPONSE, 전체_캠프사이트_조회_응답);
    }

    private enum Key {
        CAMPSITE_CREATE_RESPONSE,
        CAMPSITE_UPDATE_RESPONSE,
        CAMPSITE_LIST_RESPONSE,
    }
}

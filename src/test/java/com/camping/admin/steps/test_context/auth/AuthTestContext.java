package com.camping.admin.steps.test_context.auth;

import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class AuthTestContext {

    private final Map<Key, Object> context = new HashMap<>();

    public void clear() {
        context.clear();
    }

    public Response 로그인_응답() {
        return (Response) context.get(Key.LOGIN_RESPONSE);
    }

    public void 로그인_응답(Response 로그인_응답) {
        this.context.put(Key.LOGIN_RESPONSE, 로그인_응답);
    }

    public String 인증_토큰() {
        return (String) context.get(Key.AUTH_TOKEN);
    }

    public void 인증_토큰(String 인증_토큰) {
        this.context.put(Key.AUTH_TOKEN, 인증_토큰);
    }

    private enum Key {
        AUTH_TOKEN,
        LOGIN_RESPONSE,
    }
}

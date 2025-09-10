package com.camping.admin.steps;

import java.util.HashMap;
import java.util.Map;

public class StepContext {

    private static ThreadLocal<Map<String, String>> context = ThreadLocal.withInitial(HashMap::new);

    public static void setAccessToken(String value) {
        context.get().put("accessToken", value);
    }

    public static String getAccessToken() {
        return context.get().get("accessToken");
    }
}

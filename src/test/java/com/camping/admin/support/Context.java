package com.camping.admin.support;

import io.restassured.response.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Context {
    private Map<Role, String> tokens =  new HashMap<>();
    private Map<String, Object> data = new HashMap<>();
    private Response response;

    public void setToken(Role role, String token) {
        tokens.put(role, token);
    }

    public String getToken(Role role) {
        return tokens.get(role);
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public Object getData(String key) {
        return data.get(key);
    }
}

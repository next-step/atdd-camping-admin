package com.camping.admin.support;

public class CommonContext {
    private static String adminToken;

    public static String getAdminToken() { return adminToken; }
    public static void setAdminToken(String token) { adminToken = token; }
}

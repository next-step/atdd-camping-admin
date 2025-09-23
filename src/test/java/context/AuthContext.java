package context;

import hooks.AuthHooks;

public class AuthContext {
    private static final String accessToken = AuthHooks.getAccessToken();

    public static String getAccessToken() {
        return accessToken;
    }
}

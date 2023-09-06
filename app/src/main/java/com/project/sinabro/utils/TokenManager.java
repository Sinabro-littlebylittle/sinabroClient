package com.project.sinabro.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class TokenManager {
    private final SharedPreferences prefs;
    private static TokenManager INSTANCE = null;

    private TokenManager(Context context) {
        prefs = context.getSharedPreferences("JWT_TOKEN_PREFS", Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TokenManager(context);
        }
        return INSTANCE;
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("JWT_TOKEN", token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString("JWT_TOKEN", null);
    }

    public void saveUserInfo(JSONObject jsonObject) {
        String userId = jsonObject.optString("_id");
        String email = jsonObject.optString("email");
        String username = jsonObject.optString("username");
        String role = jsonObject.optString("role");
        int point = jsonObject.optInt("point");

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId`", userId);
        editor.putString("email", email);
        editor.putString("username", username);
        editor.putString("role", role);
        editor.putInt("point", point);
        editor.apply();
    }

    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        // 토큰 제거
        editor.remove("JWT_TOKEN");
        // 사용자 정보 제거
        editor.remove("userId");
        editor.remove("email");
        editor.remove("username");
        editor.remove("role");
        editor.remove("point");
        editor.apply();
    }

    public String getUserId() {
        return prefs.getString("userId", null);
    }

    public String getEmail() {
        return prefs.getString("email", null);
    }

    public String getUsername() {
        return prefs.getString("username", null);
    }

    public String getRole() {
        return prefs.getString("role", null);
    }

    public int getPoint() { return prefs.getInt("point", 0); }
}

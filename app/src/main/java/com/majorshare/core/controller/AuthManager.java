package com.majorshare.core.controller;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.majorshare.core.db.DatabaseHelper;
import com.majorshare.core.domain.User;

public class AuthManager {

    private static AuthManager instance;
    private User currentUser;
    private static final String PREF_NAME = "MajorSharePrefs";
    private static final String KEY_USER_ID = "logged_in_user_id";

    private AuthManager() {
        // 싱글톤 패턴 유지
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback
        }
    }

    public enum LoginResult {
        SUCCESS,
        WRONG_PASSWORD,
        USER_NOT_FOUND,
        SUSPENDED,
        BANNED
    }

    public LoginResult login(Context context, String userId, String password) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        User user = dbHelper.getUser(userId);

        if (user == null) {
            return LoginResult.USER_NOT_FOUND;
        }

        String hashedPassword = hashPassword(password);
        if (!user.getPassword().equals(hashedPassword)) {
            return LoginResult.WRONG_PASSWORD;
        }

        if (user.getAccountStatus().equals("SUSPENDED")) {
            return LoginResult.SUSPENDED;
        }

        if (user.getAccountStatus().equals("BANNED")) {
            return LoginResult.BANNED;
        }

        if (user.getAccountStatus().equals("ACTIVE")) {
            currentUser = user;
            // SharedPreferences에 저장 (로그인 유지)
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_USER_ID, userId).apply();
            return LoginResult.SUCCESS;
        }

        return LoginResult.USER_NOT_FOUND;
    }

    public void init(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedId = prefs.getString(KEY_USER_ID, null);
        if (savedId != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            currentUser = dbHelper.getUser(savedId);
        }
    }

    public boolean isAuthenticated(String userId) {
        return currentUser != null && currentUser.getUserId().equals(userId);
    }

    public void checkUserStatus(Context context) {
        if (currentUser == null) return;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        User user = dbHelper.getUser(currentUser.getUserId());
        if (user == null || !"ACTIVE".equals(user.getAccountStatus())) {
            logout(context);
        }
    }

    public boolean register(Context context, String userId, String password, String name) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        
        // 아이디 중복 체크
        if (dbHelper.getUser(userId) != null) {
            return false;
        }
        
        // 새 유저 생성 (비밀번호 해싱) 및 DB 저장
        User newUser = new User(userId, hashPassword(password), name);
        if (userId.toLowerCase().startsWith("admin")) {
            newUser.setRole("ADMIN");
        }
        return dbHelper.insertUser(newUser);
    }

    public void logout(Context context) {
        currentUser = null;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
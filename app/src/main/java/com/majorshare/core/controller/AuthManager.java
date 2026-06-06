package com.majorshare.core.controller;

import android.content.Context;

import com.majorshare.core.db.DatabaseHelper;
import com.majorshare.core.domain.User;

public class AuthManager {

    private static AuthManager instance;
    private User currentUser;

    private AuthManager() {
        // 싱글톤 패턴 유지
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public boolean login(Context context, String userId, String password) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        User user = dbHelper.getUser(userId);

        if (user != null && user.getPassword().equals(password) && user.getAccountStatus().equals("ACTIVE")) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean register(Context context, String userId, String password, String name) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        
        // 아이디 중복 체크
        if (dbHelper.getUser(userId) != null) {
            return false;
        }
        
        // 새 유저 생성 및 DB 저장
        User newUser = new User(userId, password, name);
        return dbHelper.insertUser(newUser);
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
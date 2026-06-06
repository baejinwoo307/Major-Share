package com.majorshare.core.domain;

import com.majorshare.core.dto.UserProfileDTO;

public class User {
    private String userId;
    private String password;
    private String name;
    private String role;
    private String accountStatus;
    private float mannerScore;


    public User(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.role = "GENERAL";
        this.accountStatus = "ACTIVE";
        this.mannerScore = 3.0f;
    }


    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void changeAccountStatus(String status) {
        this.accountStatus = status;
    }

    public void updateScore(int rating) {

        this.mannerScore = (this.mannerScore + rating) / 2.0f;
    }

    public UserProfileDTO getProfileResponse() {
        return new UserProfileDTO(this.userId, this.name, this.role, this.accountStatus, this.mannerScore);
    }


    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getAccountStatus() { return accountStatus; }
    public float getMannerScore() { return mannerScore; }
}
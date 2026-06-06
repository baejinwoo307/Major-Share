package com.majorshare.core.dto;

public class UserProfileDTO {
    private String userId;
    private String name;
    private String role;
    private String accountStatus;
    private float mannerScore;

    public UserProfileDTO(String userId, String name, String role, String accountStatus, float mannerScore) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.accountStatus = accountStatus;
        this.mannerScore = mannerScore;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getAccountStatus() { return accountStatus; }
    public float getMannerScore() { return mannerScore; }
}
package com.majorshare.core.dto;

public class SystemDataDTO {
    private int totalUsers;
    private int totalItems;
    private int totalTransactions;

    public SystemDataDTO(int totalUsers, int totalItems, int totalTransactions) {
        this.totalUsers = totalUsers;
        this.totalItems = totalItems;
        this.totalTransactions = totalTransactions;
    }

    public int getTotalUsers() { return totalUsers; }
    public int getTotalItems() { return totalItems; }
    public int getTotalTransactions() { return totalTransactions; }
}
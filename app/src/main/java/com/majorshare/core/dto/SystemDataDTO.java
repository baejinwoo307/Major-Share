package com.majorshare.core.dto;

public class SystemDataDTO {
    private int totalUsers;
    private int totalItems;
    private int totalTransactions;
    private int blockedUsers;

    public SystemDataDTO(int totalUsers, int totalItems, int totalTransactions, int blockedUsers) {
        this.totalUsers = totalUsers;
        this.totalItems = totalItems;
        this.totalTransactions = totalTransactions;
        this.blockedUsers = blockedUsers;
    }

    public int getTotalUsers() { return totalUsers; }
    public int getTotalItems() { return totalItems; }
    public int getTotalTransactions() { return totalTransactions; }
    public int getBlockedUsers() { return blockedUsers; }
}

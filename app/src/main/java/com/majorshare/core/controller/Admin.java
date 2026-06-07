package com.majorshare.core.controller;

import android.content.Context;

public class Admin {
    
    private static Admin instance;

    private Admin() {}

    public static Admin getInstance() {
        if (instance == null) {
            instance = new Admin();
        }
        return instance;
    }

    public void applySuspension(Context context, String userId, int days) {
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
        db.updateUserAccountStatus(userId, "SUSPENDED");
    }

    public void applyPermanentBan(Context context, String userId) {
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
        db.updateUserAccountStatus(userId, "BANNED");
    }

    public void liftSanction(Context context, String userId) {
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
        db.updateUserAccountStatus(userId, "ACTIVE");
    }

    public com.majorshare.core.dto.SystemDataDTO loadSystemData(Context context) {
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
        android.os.Bundle stats = db.getSystemStats();
        return new com.majorshare.core.dto.SystemDataDTO(
            stats.getInt("total_users"),
            stats.getInt("total_items"),
            stats.getInt("total_transactions"),
            stats.getInt("blocked_users")
        );
    }

    public java.util.List<com.majorshare.core.domain.Transaction> viewUserTransactions(Context context, String userId) {
        return TransactionRepository.getInstance().getTransactionsRelatedToUser(context, userId);
    }
}
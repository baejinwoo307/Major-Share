package com.majorshare.core.controller;

import android.content.Context;
import com.majorshare.core.db.DatabaseHelper;
import com.majorshare.core.domain.Reservation;
import com.majorshare.core.domain.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    private static TransactionRepository instance;

    private TransactionRepository() {
        // 싱글톤
    }

    public static TransactionRepository getInstance() {
        if (instance == null) {
            instance = new TransactionRepository();
        }
        return instance;
    }

    public void addTransaction(Context context, Transaction transaction) {
        DatabaseHelper db = new DatabaseHelper(context);
        long id = db.insertTransaction(transaction);
        transaction.setTransactionId(id);
    }

    public void updateTransaction(Context context, Transaction transaction) {
        DatabaseHelper db = new DatabaseHelper(context);
        db.updateTransaction(transaction);
    }

    public void addReservation(Context context, Reservation reservation) {
        DatabaseHelper db = new DatabaseHelper(context);
        long id = db.insertReservation(reservation);
        reservation.setReservationId(id);
    }

    public List<Transaction> getTransactions(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        return db.getAllTransactions();
    }

    public List<Transaction> getTransactionsRelatedToUser(Context context, String userId) {
        List<Transaction> all = getTransactions(context);
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getBuyer().getUserId().equals(userId) || t.getSubjectItem().getOwner().getUserId().equals(userId)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Reservation> getReservations(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        return db.getAllReservations();
    }

    public boolean hasReservationsForItem(Context context, Long itemId) {
        List<Reservation> all = getReservations(context);
        for (Reservation r : all) {
            if (r.getTargetItem().getItemId().equals(itemId) && r.getStatus().equals("대기")) {
                return true;
            }
        }
        return false;
    }

    public Reservation popNextReservation(Context context, Long itemId) {
        List<Reservation> all = getReservations(context);
        for (Reservation r : all) {
            if (r.getTargetItem().getItemId().equals(itemId) && r.getStatus().equals("대기")) {
                r.processAutoSuccession(); // "이행" 상태로 변경
                DatabaseHelper db = new DatabaseHelper(context);
                db.updateReservationStatus(r.getReservationId(), "이행");
                return r;
            }
        }
        return null;
    }
}

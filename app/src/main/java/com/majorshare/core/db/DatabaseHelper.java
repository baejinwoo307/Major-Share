package com.majorshare.core.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.majorshare.core.domain.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MajorShare.db";
    private static final int DATABASE_VERSION = 1;

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_MANNER_SCORE = "manner_score";

    // Items Table
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    public static final String COLUMN_MAX_RENT_DAYS = "max_rent_days";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_OWNER_ID = "owner_id";

    // Transactions Table
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_TRANS_ID = "trans_id";
    public static final String COLUMN_TRANS_STAGE = "stage";
    public static final String COLUMN_TRANS_DATE = "trans_date";
    public static final String COLUMN_TRANS_DUE_DATE = "return_due_date";
    public static final String COLUMN_TRANS_BUYER_ID = "buyer_id";

    // Reservations Table
    public static final String TABLE_RESERVATIONS = "reservations";
    public static final String COLUMN_RES_ID = "res_id";
    public static final String COLUMN_RES_STATUS = "status";
    public static final String COLUMN_RES_BORROWER_ID = "borrower_id";

    // Chat Messages Table
    public static final String TABLE_CHAT_MESSAGES = "chat_messages";
    public static final String COLUMN_MSG_ID = "msg_id";
    public static final String COLUMN_MSG_ROOM_ID = "room_id";
    public static final String COLUMN_MSG_SENDER_ID = "sender_id";
    public static final String COLUMN_MSG_CONTENT = "content";
    public static final String COLUMN_MSG_TIME = "send_time";

    // Extensions Table
    public static final String TABLE_EXTENSIONS = "extensions";
    public static final String COLUMN_EXT_ID = "ext_id";
    public static final String COLUMN_EXT_TRANS_ID = "ext_trans_id";
    public static final String COLUMN_EXT_DAYS = "ext_days";
    public static final String COLUMN_EXT_REASON = "ext_reason";
    public static final String COLUMN_EXT_STATUS = "ext_status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ROLE + " TEXT, " +
                COLUMN_MANNER_SCORE + " REAL)";
        db.execSQL(createUsersTable);

        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRICE + " INTEGER, " +
                COLUMN_TRANSACTION_TYPE + " TEXT, " +
                COLUMN_MAX_RENT_DAYS + " INTEGER, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_OWNER_ID + " TEXT)";
        db.execSQL(createItemsTable);

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_ID + " INTEGER, " +
                COLUMN_TRANS_BUYER_ID + " TEXT, " +
                COLUMN_TRANS_STAGE + " TEXT, " +
                COLUMN_TRANS_DATE + " TEXT, " +
                COLUMN_TRANS_DUE_DATE + " TEXT)";
        db.execSQL(createTransactionsTable);

        String createReservationsTable = "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                COLUMN_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_ID + " INTEGER, " +
                COLUMN_RES_BORROWER_ID + " TEXT, " +
                COLUMN_RES_STATUS + " TEXT)";
        db.execSQL(createReservationsTable);

        String createChatMessagesTable = "CREATE TABLE " + TABLE_CHAT_MESSAGES + " (" +
                COLUMN_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MSG_ROOM_ID + " TEXT, " +
                COLUMN_MSG_SENDER_ID + " TEXT, " +
                COLUMN_MSG_CONTENT + " TEXT, " +
                COLUMN_MSG_TIME + " TEXT)";
        db.execSQL(createChatMessagesTable);

        String createExtensionsTable = "CREATE TABLE " + TABLE_EXTENSIONS + " (" +
                COLUMN_EXT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXT_TRANS_ID + " INTEGER, " +
                COLUMN_EXT_DAYS + " INTEGER, " +
                COLUMN_EXT_REASON + " TEXT, " +
                COLUMN_EXT_STATUS + " TEXT)";
        db.execSQL(createExtensionsTable);

        // 기본 테스트 계정 생성
        db.execSQL("INSERT INTO " + TABLE_USERS + " VALUES ('testuser', 'pass', '임시유저', 'USER', 36.5)");
        db.execSQL("INSERT INTO " + TABLE_USERS + " VALUES ('adminuser', 'pass', '관리자', 'ADMIN', 36.5)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXTENSIONS);
        onCreate(db);
    }

    // --- User CRUD ---
    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_MANNER_SCORE, user.getMannerScore());

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public User getUser(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{userId});
        
        if (cursor != null && cursor.moveToFirst()) {
            String pass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String roleStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            cursor.close();
            
            User user = new User(userId, pass, name);
            // role이 ADMIN이면 내부 구조상 반영이 어렵지만, 편의상 객체 레벨에서 흉내냅니다 (생성자에 역할이 없음)
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void updateUserMannerScore(String userId, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        float scoreDiff = (rating - 3) * 0.5f;
        db.execSQL("UPDATE " + TABLE_USERS + " SET " + COLUMN_MANNER_SCORE + " = " + COLUMN_MANNER_SCORE + " + ? WHERE " + COLUMN_USER_ID + " = ?", new Object[]{scoreDiff, userId});
    }

    // --- Item CRUD ---
    public long insertItem(com.majorshare.core.domain.Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_TRANSACTION_TYPE, item.getTransactionType());
        values.put(COLUMN_MAX_RENT_DAYS, item.getMaxRentDays());
        values.put(COLUMN_STATUS, item.getStatus());
        values.put(COLUMN_OWNER_ID, item.getOwner().getUserId());

        return db.insert(TABLE_ITEMS, null, values);
    }

    public void updateItemStatus(Long itemId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
    }

    public java.util.List<com.majorshare.core.domain.Item> getAllItems() {
        java.util.List<com.majorshare.core.domain.Item> itemList = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " ORDER BY " + COLUMN_ITEM_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE));
                int maxRentDays = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_RENT_DAYS));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                String ownerId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OWNER_ID));

                User owner = getUser(ownerId);
                if (owner != null) {
                    com.majorshare.core.domain.Item item = new com.majorshare.core.domain.Item(title, category, type, price, maxRentDays, owner);
                    item.setItemId(id);
                    item.changeStatus(status);
                    itemList.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public com.majorshare.core.domain.Item getItemById(Long itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});

        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE));
            int maxRentDays = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_RENT_DAYS));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
            String ownerId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OWNER_ID));

            User owner = getUser(ownerId);
            com.majorshare.core.domain.Item item = new com.majorshare.core.domain.Item(title, category, type, price, maxRentDays, owner);
            item.setItemId(itemId);
            item.changeStatus(status);
            cursor.close();
            return item;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // --- Transaction CRUD ---
    public long insertTransaction(com.majorshare.core.domain.Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_ID, transaction.getSubjectItem().getItemId());
        values.put(COLUMN_TRANS_BUYER_ID, transaction.getBuyer().getUserId());
        values.put(COLUMN_TRANS_STAGE, transaction.getStage());
        if (transaction.getTransactionDate() != null) values.put(COLUMN_TRANS_DATE, transaction.getTransactionDate().toString());
        if (transaction.getReturnDueDate() != null) values.put(COLUMN_TRANS_DUE_DATE, transaction.getReturnDueDate().toString());

        return db.insert(TABLE_TRANSACTIONS, null, values);
    }

    public void updateTransaction(com.majorshare.core.domain.Transaction transaction) {
        if (transaction.getTransactionId() == null) return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANS_STAGE, transaction.getStage());
        if (transaction.getTransactionDate() != null) values.put(COLUMN_TRANS_DATE, transaction.getTransactionDate().toString());
        if (transaction.getReturnDueDate() != null) values.put(COLUMN_TRANS_DUE_DATE, transaction.getReturnDueDate().toString());

        db.update(TABLE_TRANSACTIONS, values, COLUMN_TRANS_ID + " = ?", new String[]{String.valueOf(transaction.getTransactionId())});
    }

    public java.util.List<com.majorshare.core.domain.Transaction> getAllTransactions() {
        java.util.List<com.majorshare.core.domain.Transaction> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);

        if (cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TRANS_ID));
                Long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                String buyerId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_BUYER_ID));
                String stage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_STAGE));
                String transDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_DATE));
                String dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_DUE_DATE));

                com.majorshare.core.domain.Item item = getItemById(itemId);
                User buyer = getUser(buyerId);
                
                if (item != null && buyer != null) {
                    com.majorshare.core.domain.Transaction t = new com.majorshare.core.domain.Transaction(item, buyer);
                    t.setTransactionId(id);
                    t.setStage(stage);
                    if (transDateStr != null) t.setTransactionDate(java.time.LocalDate.parse(transDateStr));
                    if (dueDateStr != null) t.setReturnDueDate(java.time.LocalDateTime.parse(dueDateStr));
                    list.add(t);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- Reservation CRUD ---
    public long insertReservation(com.majorshare.core.domain.Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_ID, reservation.getTargetItem().getItemId());
        values.put(COLUMN_RES_BORROWER_ID, reservation.getBorrower().getUserId());
        values.put(COLUMN_RES_STATUS, reservation.getStatus());

        return db.insert(TABLE_RESERVATIONS, null, values);
    }

    public void updateReservationStatus(Long resId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RES_STATUS, newStatus);
        db.update(TABLE_RESERVATIONS, values, COLUMN_RES_ID + " = ?", new String[]{String.valueOf(resId)});
    }

    public java.util.List<com.majorshare.core.domain.Reservation> getAllReservations() {
        java.util.List<com.majorshare.core.domain.Reservation> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESERVATIONS, null);

        if (cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RES_ID));
                Long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                String borrowerId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RES_BORROWER_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RES_STATUS));

                com.majorshare.core.domain.Item item = getItemById(itemId);
                User borrower = getUser(borrowerId);
                
                if (item != null && borrower != null) {
                    com.majorshare.core.domain.Reservation r = new com.majorshare.core.domain.Reservation(item, borrower);
                    r.setReservationId(id);
                    r.setStatus(status);
                    list.add(r);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- Chat Messages CRUD ---
    public long insertChatMessage(String roomId, String senderId, String content, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MSG_ROOM_ID, roomId);
        values.put(COLUMN_MSG_SENDER_ID, senderId);
        values.put(COLUMN_MSG_CONTENT, content);
        values.put(COLUMN_MSG_TIME, time);

        return db.insert(TABLE_CHAT_MESSAGES, null, values);
    }

    public java.util.List<com.majorshare.core.domain.ChatMessage> getChatMessagesByRoomId(String roomId) {
        java.util.List<com.majorshare.core.domain.ChatMessage> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + COLUMN_MSG_ROOM_ID + " = ? ORDER BY " + COLUMN_MSG_ID + " ASC", new String[]{roomId});

        if (cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MSG_ID));
                String senderId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_SENDER_ID));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_CONTENT));
                String timeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_TIME));

                User sender = getUser(senderId);
                if (sender != null) {
                    com.majorshare.core.domain.ChatMessage msg = new com.majorshare.core.domain.ChatMessage(null, sender, content);
                    msg.setMessageId(id);
                    msg.setSendTime(java.time.LocalDateTime.parse(timeStr));
                    list.add(msg);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- Extension CRUD ---
    public long insertExtension(Long transId, int days, String reason, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXT_TRANS_ID, transId);
        values.put(COLUMN_EXT_DAYS, days);
        values.put(COLUMN_EXT_REASON, reason);
        values.put(COLUMN_EXT_STATUS, status);
        return db.insert(TABLE_EXTENSIONS, null, values);
    }

    public void updateExtensionStatus(Long extId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXT_STATUS, newStatus);
        db.update(TABLE_EXTENSIONS, values, COLUMN_EXT_ID + " = ?", new String[]{String.valueOf(extId)});
    }

    public Cursor getExtensionsForTransaction(Long transId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EXTENSIONS + " WHERE " + COLUMN_EXT_TRANS_ID + " = ? ORDER BY " + COLUMN_EXT_ID + " DESC", new String[]{String.valueOf(transId)});
    }
}
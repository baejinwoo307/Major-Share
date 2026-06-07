package com.majorshare.core.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.majorshare.core.domain.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MajorShare.db";
    private static final int DATABASE_VERSION = 8;

    // Users Table (Design Doc: User)
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_ACCOUNT_STATUS = "account_status";
    public static final String COLUMN_MANNER_SCORE = "manner_score";

    // Items Table (Design Doc: Item)
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    public static final String COLUMN_MAX_RENT_DAYS = "max_rent_days";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_OWNER_ID = "owner_id";
    public static final String COLUMN_IMAGE = "image";

    // Transactions Table (Design Doc: Transaction)
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_TRANS_ID = "trans_id";
    public static final String COLUMN_TRANS_ITEM_ID = "item_id";
    public static final String COLUMN_TRANS_BUYER_ID = "buyer_id";
    public static final String COLUMN_TRANS_STAGE = "stage";
    public static final String COLUMN_TRANS_TYPE = "transaction_type";
    public static final String COLUMN_TRANS_DATE = "transaction_date";
    public static final String COLUMN_TRANS_DUE_DATE = "return_due_date";

    // Reservations Table (Design Doc: Reservation)
    public static final String TABLE_RESERVATIONS = "reservations";
    public static final String COLUMN_RES_ID = "res_id";
    public static final String COLUMN_RES_ITEM_ID = "item_id";
    public static final String COLUMN_RES_BORROWER_ID = "borrower_id";
    public static final String COLUMN_RES_STATUS = "status";
    public static final String COLUMN_RES_REQUEST_TIME = "request_time";
    public static final String COLUMN_RES_QUEUE_ORDER = "queue_order";

    // Extensions Table (Design Doc: Extension)
    public static final String TABLE_EXTENSIONS = "extensions";
    public static final String COLUMN_EXT_ID = "ext_id";
    public static final String COLUMN_EXT_TRANS_ID = "ext_trans_id";
    public static final String COLUMN_EXT_DAYS = "ext_days";
    public static final String COLUMN_EXT_REASON = "ext_reason";
    public static final String COLUMN_EXT_STATUS = "ext_status";

    // Reviews Table (Design Doc: Review)
    public static final String TABLE_REVIEWS = "reviews";
    public static final String COLUMN_REVIEW_ID = "review_id";
    public static final String COLUMN_REV_TRANS_ID = "trans_id";
    public static final String COLUMN_REV_REVIEWER_ID = "reviewer_id";
    public static final String COLUMN_REV_TARGET_ID = "target_id";
    public static final String COLUMN_REV_RATING = "rating";
    public static final String COLUMN_REV_CONTENT = "content";

    // Chat Messages Table (Design Doc: ChatMessage)
    public static final String TABLE_CHAT_MESSAGES = "chat_messages";
    public static final String COLUMN_MSG_ID = "msg_id";
    public static final String COLUMN_MSG_ROOM_ID = "room_id";
    public static final String COLUMN_MSG_SENDER_ID = "sender_id";
    public static final String COLUMN_MSG_CONTENT = "content";
    public static final String COLUMN_MSG_IMAGE = "image_data";
    public static final String COLUMN_MSG_IS_READ = "is_read";
    public static final String COLUMN_MSG_TIME = "send_time";

    // Notifications Table (Design Doc: Notification)
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_NOTIF_ID = "notif_id";
    public static final String COLUMN_NOTIF_TYPE = "type";
    public static final String COLUMN_NOTIF_CONTENT = "content";
    public static final String COLUMN_NOTIF_IS_READ = "is_read";
    public static final String COLUMN_NOTIF_RECEIVER_ID = "receiver_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ROLE + " TEXT, " +
                COLUMN_ACCOUNT_STATUS + " TEXT, " +
                COLUMN_MANNER_SCORE + " REAL)");

        db.execSQL("CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRICE + " INTEGER, " +
                COLUMN_TRANSACTION_TYPE + " TEXT, " +
                COLUMN_MAX_RENT_DAYS + " INTEGER, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_OWNER_ID + " TEXT, " +
                COLUMN_IMAGE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRANS_ITEM_ID + " INTEGER, " +
                COLUMN_TRANS_BUYER_ID + " TEXT, " +
                COLUMN_TRANS_STAGE + " TEXT, " +
                COLUMN_TRANS_TYPE + " TEXT, " +
                COLUMN_TRANS_DATE + " TEXT, " +
                COLUMN_TRANS_DUE_DATE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                COLUMN_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RES_ITEM_ID + " INTEGER, " +
                COLUMN_RES_BORROWER_ID + " TEXT, " +
                COLUMN_RES_STATUS + " TEXT, " +
                COLUMN_RES_REQUEST_TIME + " TEXT, " +
                COLUMN_RES_QUEUE_ORDER + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_EXTENSIONS + " (" +
                COLUMN_EXT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXT_TRANS_ID + " INTEGER, " +
                COLUMN_EXT_DAYS + " INTEGER, " +
                COLUMN_EXT_REASON + " TEXT, " +
                COLUMN_EXT_STATUS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_REVIEWS + " (" +
                COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_REV_TRANS_ID + " INTEGER, " +
                COLUMN_REV_REVIEWER_ID + " TEXT, " +
                COLUMN_REV_TARGET_ID + " TEXT, " +
                COLUMN_REV_RATING + " INTEGER, " +
                COLUMN_REV_CONTENT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_CHAT_MESSAGES + " (" +
                COLUMN_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MSG_ROOM_ID + " TEXT, " +
                COLUMN_MSG_SENDER_ID + " TEXT, " +
                COLUMN_MSG_CONTENT + " TEXT, " +
                COLUMN_MSG_IMAGE + " TEXT, " +
                COLUMN_MSG_IS_READ + " INTEGER DEFAULT 0, " +
                COLUMN_MSG_TIME + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COLUMN_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTIF_TYPE + " TEXT, " +
                COLUMN_NOTIF_CONTENT + " TEXT, " +
                COLUMN_NOTIF_IS_READ + " INTEGER DEFAULT 0, " +
                COLUMN_NOTIF_RECEIVER_ID + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXTENSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    // --- User CRUD (Design: User) ---
    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_ID, user.getUserId());
        v.put(COLUMN_PASSWORD, user.getPassword());
        v.put(COLUMN_NAME, user.getName());
        v.put(COLUMN_ROLE, user.getRole());
        v.put(COLUMN_ACCOUNT_STATUS, user.getAccountStatus());
        v.put(COLUMN_MANNER_SCORE, user.getMannerScore());
        return db.insert(TABLE_USERS, null, v) != -1;
    }

    public User getUser(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{userId});
        if (c != null && c.moveToFirst()) {
            User u = new User(userId, c.getString(c.getColumnIndexOrThrow(COLUMN_PASSWORD)), c.getString(c.getColumnIndexOrThrow(COLUMN_NAME)));
            u.setRole(c.getString(c.getColumnIndexOrThrow(COLUMN_ROLE)));
            u.changeAccountStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_ACCOUNT_STATUS)));
            u.setMannerScore(c.getFloat(c.getColumnIndexOrThrow(COLUMN_MANNER_SCORE)));
            c.close();
            return u;
        }
        if (c != null) c.close();
        return null;
    }

    // --- Item CRUD (Design: Item) ---
    public long insertItem(com.majorshare.core.domain.Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TITLE, item.getTitle());
        v.put(COLUMN_CATEGORY, item.getCategory());
        v.put(COLUMN_PRICE, item.getPrice());
        v.put(COLUMN_TRANSACTION_TYPE, item.getTransactionType());
        v.put(COLUMN_MAX_RENT_DAYS, 7); // Default as per design
        v.put(COLUMN_STATUS, item.getStatus());
        v.put(COLUMN_OWNER_ID, item.getOwner().getUserId());
        v.put(COLUMN_IMAGE, item.getImageBase64());
        return db.insert(TABLE_ITEMS, null, v);
    }

    public com.majorshare.core.domain.Item getItemById(Long itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
        if (c != null && c.moveToFirst()) {
            User owner = getUser(c.getString(c.getColumnIndexOrThrow(COLUMN_OWNER_ID)));
            if (owner == null) return null;
            com.majorshare.core.domain.Item item = new com.majorshare.core.domain.Item(
                    c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_PRICE)),
                    "", owner);
            item.setItemId(itemId);
            item.setMaxRentDays(c.getInt(c.getColumnIndexOrThrow(COLUMN_MAX_RENT_DAYS)));
            item.changeStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_STATUS)));
            item.setImageBase64(c.getString(c.getColumnIndexOrThrow(COLUMN_IMAGE)));
            c.close();
            return item;
        }
        return null;
    }

    public void updateItemStatus(Long itemId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
    }

    public void updateItemDetails(Long itemId, String title, int price, String rentalEndDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TRANSACTION_TYPE, rentalEndDate); // This was previously used for date but field mapping is inconsistent
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
    }

    public void deleteItem(Long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ITEM_ID + " = ?", new String[]{String.valueOf(itemId)});
    }

    public java.util.List<com.majorshare.core.domain.Item> getAllItems() {
        java.util.List<com.majorshare.core.domain.Item> itemList = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " ORDER BY " + COLUMN_ITEM_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                User owner = getUser(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OWNER_ID)));
                if (owner != null) {
                    com.majorshare.core.domain.Item item = new com.majorshare.core.domain.Item(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANSACTION_TYPE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                            "", owner);
                    item.setItemId(id);
                    item.setMaxRentDays(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_RENT_DAYS)));
                    item.changeStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                    item.setImageBase64(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)));
                    itemList.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // --- Transaction CRUD (Design: Transaction) ---
    public long insertTransaction(com.majorshare.core.domain.Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TRANS_ITEM_ID, t.getSubjectItem().getItemId());
        v.put(COLUMN_TRANS_BUYER_ID, t.getBuyer().getUserId());
        v.put(COLUMN_TRANS_STAGE, t.getStage());
        v.put(COLUMN_TRANS_TYPE, t.getSubjectItem().getTransactionType());
        if (t.getTransactionDate() != null) v.put(COLUMN_TRANS_DATE, t.getTransactionDate().toString());
        if (t.getReturnDueDate() != null) v.put(COLUMN_TRANS_DUE_DATE, t.getReturnDueDate().toString());
        return db.insert(TABLE_TRANSACTIONS, null, v);
    }

    public void updateTransaction(com.majorshare.core.domain.Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TRANS_STAGE, t.getStage());
        if (t.getTransactionDate() != null) v.put(COLUMN_TRANS_DATE, t.getTransactionDate().toString());
        if (t.getReturnDueDate() != null) v.put(COLUMN_TRANS_DUE_DATE, t.getReturnDueDate().toString());
        db.update(TABLE_TRANSACTIONS, v, COLUMN_TRANS_ID + " = ?", new String[]{String.valueOf(t.getTransactionId())});
    }

    public java.util.List<com.majorshare.core.domain.Transaction> getAllTransactions() {
        java.util.List<com.majorshare.core.domain.Transaction> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS, null);
        if (c.moveToFirst()) {
            do {
                Long id = c.getLong(c.getColumnIndexOrThrow(COLUMN_TRANS_ID));
                com.majorshare.core.domain.Item item = getItemById(c.getLong(c.getColumnIndexOrThrow(COLUMN_TRANS_ITEM_ID)));
                User buyer = getUser(c.getString(c.getColumnIndexOrThrow(COLUMN_TRANS_BUYER_ID)));
                if (item != null && buyer != null) {
                    com.majorshare.core.domain.Transaction t = new com.majorshare.core.domain.Transaction(item, buyer);
                    t.setTransactionId(id);
                    t.setStage(c.getString(c.getColumnIndexOrThrow(COLUMN_TRANS_STAGE)));
                    String d1 = c.getString(c.getColumnIndexOrThrow(COLUMN_TRANS_DATE));
                    String d2 = c.getString(c.getColumnIndexOrThrow(COLUMN_TRANS_DUE_DATE));
                    if (d1 != null) t.setTransactionDate(java.time.LocalDate.parse(d1));
                    if (d2 != null) t.setReturnDueDate(java.time.LocalDateTime.parse(d2));
                    list.add(t);
                }
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int getUserTransactionCount(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS + " t " +
                "INNER JOIN " + TABLE_ITEMS + " i ON t." + COLUMN_TRANS_ITEM_ID + " = i." + COLUMN_ITEM_ID + " " +
                "WHERE (i." + COLUMN_OWNER_ID + " = ? OR t." + COLUMN_TRANS_BUYER_ID + " = ?) " +
                "AND t." + COLUMN_TRANS_STAGE + " IN ('거래완료', '반납완료', '반납완료_연체')";
        Cursor cursor = db.rawQuery(query, new String[]{userId, userId});
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // --- Reservation CRUD (Design: Reservation) ---
    public long insertReservation(com.majorshare.core.domain.Reservation r) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_RES_ITEM_ID, r.getTargetItem().getItemId());
        v.put(COLUMN_RES_BORROWER_ID, r.getBorrower().getUserId());
        v.put(COLUMN_RES_STATUS, r.getStatus());
        v.put(COLUMN_RES_REQUEST_TIME, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        int currentCount = getReservationCountForItem(r.getTargetItem().getItemId());
        v.put(COLUMN_RES_QUEUE_ORDER, currentCount + 1);
        return db.insert(TABLE_RESERVATIONS, null, v);
    }

    public void updateReservationStatus(Long resId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RES_STATUS, newStatus);
        db.update(TABLE_RESERVATIONS, values, COLUMN_RES_ID + " = ?", new String[]{String.valueOf(resId)});
        
        // [설계서 보완] 상태 변경 시 해당 아이템의 예약 순번 재조정
        // 먼저 해당 예약의 itemId를 가져옴
        Cursor c = db.rawQuery("SELECT " + COLUMN_RES_ITEM_ID + " FROM " + TABLE_RESERVATIONS + " WHERE " + COLUMN_RES_ID + " = ?", new String[]{String.valueOf(resId)});
        if (c.moveToFirst()) {
            reorderReservationQueue(c.getLong(0));
        }
        c.close();
    }

    public void reorderReservationQueue(Long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 대기 중인 예약들만 시간순으로 가져옴
        Cursor c = db.rawQuery("SELECT " + COLUMN_RES_ID + " FROM " + TABLE_RESERVATIONS + 
                " WHERE " + COLUMN_RES_ITEM_ID + " = ? AND " + COLUMN_RES_STATUS + " = '대기' " +
                " ORDER BY " + COLUMN_RES_REQUEST_TIME + " ASC", new String[]{String.valueOf(itemId)});
        
        int order = 1;
        if (c.moveToFirst()) {
            do {
                long resId = c.getLong(0);
                ContentValues v = new ContentValues();
                v.put(COLUMN_RES_QUEUE_ORDER, order++);
                db.update(TABLE_RESERVATIONS, v, COLUMN_RES_ID + " = ?", new String[]{String.valueOf(resId)});
            } while (c.moveToNext());
        }
        c.close();
    }

    public java.util.List<com.majorshare.core.domain.Reservation> getAllReservations() {
        java.util.List<com.majorshare.core.domain.Reservation> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_RESERVATIONS, null);
        if (c.moveToFirst()) {
            do {
                Long id = c.getLong(c.getColumnIndexOrThrow(COLUMN_RES_ID));
                com.majorshare.core.domain.Item item = getItemById(c.getLong(c.getColumnIndexOrThrow(COLUMN_RES_ITEM_ID)));
                User borrower = getUser(c.getString(c.getColumnIndexOrThrow(COLUMN_RES_BORROWER_ID)));
                if (item != null && borrower != null) {
                    com.majorshare.core.domain.Reservation r = new com.majorshare.core.domain.Reservation(item, borrower);
                    r.setReservationId(id);
                    r.setStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_RES_STATUS)));
                    list.add(r);
                }
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int getReservationCountForItem(Long itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RESERVATIONS + " WHERE " + COLUMN_RES_ITEM_ID + " = ? AND " + COLUMN_RES_STATUS + " = '대기'", new String[]{String.valueOf(itemId)});
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close();
        return count;
    }

    // --- Extension CRUD (Design: Extension) ---
    public long insertExtension(Long transId, int days, String reason, String status, Long itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // [보안/로직 보완] DB단에서도 예약자가 있는지 최종 확인 (Block Extension)
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RESERVATIONS + " WHERE " + COLUMN_RES_ITEM_ID + " = ? AND " + COLUMN_RES_STATUS + " = '대기'", new String[]{String.valueOf(itemId)});
        if (c.moveToFirst() && c.getInt(0) > 0) {
            c.close();
            return -1; // 예약자가 있어 연장 불가
        }
        c.close();

        ContentValues v = new ContentValues();
        v.put(COLUMN_EXT_TRANS_ID, transId);
        v.put(COLUMN_EXT_DAYS, days);
        v.put(COLUMN_EXT_REASON, reason);
        v.put(COLUMN_EXT_STATUS, status);
        return db.insert(TABLE_EXTENSIONS, null, v);
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

    // --- Review CRUD (Design: Review) ---
    public long insertReview(Long transId, String reviewerId, String targetId, int rating, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_REV_TRANS_ID, transId);
        v.put(COLUMN_REV_REVIEWER_ID, reviewerId);
        v.put(COLUMN_REV_TARGET_ID, targetId);
        v.put(COLUMN_REV_RATING, rating);
        v.put(COLUMN_REV_CONTENT, content);
        
        long res = db.insert(TABLE_REVIEWS, null, v);
        if (res != -1) {
            updateUserMannerScore(targetId, (float)rating);
            // [설계서 보완] 리뷰 수신 알림 자동 생성
            insertNotification(targetId, "리뷰 도착", "새로운 거래 리뷰가 도착했습니다: " + rating + "점");
        }
        return res;
    }

    public java.util.List<com.majorshare.core.domain.Review> getReviewsForUser(String userId) {
        java.util.List<com.majorshare.core.domain.Review> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_REVIEWS + " WHERE " + COLUMN_REV_TARGET_ID + " = ? ORDER BY " + COLUMN_REVIEW_ID + " DESC", new String[]{userId});
        if (c.moveToFirst()) {
            do {
                list.add(new com.majorshare.core.domain.Review(c.getLong(c.getColumnIndexOrThrow(COLUMN_REVIEW_ID)),
                         c.getString(c.getColumnIndexOrThrow(COLUMN_REV_TARGET_ID)),
                         c.getString(c.getColumnIndexOrThrow(COLUMN_REV_REVIEWER_ID)),
                         c.getFloat(c.getColumnIndexOrThrow(COLUMN_REV_RATING)),
                         c.getString(c.getColumnIndexOrThrow(COLUMN_REV_CONTENT)), ""));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void updateUserMannerScore(String userId, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        float diff = (rating - 3) * 0.5f;
        db.execSQL("UPDATE " + TABLE_USERS + " SET " + COLUMN_MANNER_SCORE + " = " + COLUMN_MANNER_SCORE + " + ? WHERE " + COLUMN_USER_ID + " = ?", new Object[]{diff, userId});
    }

    // --- Chat Messages CRUD ---
    public long insertChatMessage(String roomId, String senderId, String content, String imageData, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_MSG_ROOM_ID, roomId);
        v.put(COLUMN_MSG_SENDER_ID, senderId);
        v.put(COLUMN_MSG_CONTENT, content);
        v.put(COLUMN_MSG_IMAGE, imageData);
        v.put(COLUMN_MSG_IS_READ, 0);
        v.put(COLUMN_MSG_TIME, time);
        return db.insert(TABLE_CHAT_MESSAGES, null, v);
    }

    public void markChatAsRead(String roomId, String currentUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_MSG_IS_READ, 1);
        // 내가 아닌 상대방이 보낸 메시지만 읽음 처리
        db.update(TABLE_CHAT_MESSAGES, v, COLUMN_MSG_ROOM_ID + " = ? AND " + COLUMN_MSG_SENDER_ID + " != ?", new String[]{roomId, currentUserId});
    }

    public java.util.List<com.majorshare.core.domain.ChatMessage> getChatMessagesByRoomId(String roomId) {
        java.util.List<com.majorshare.core.domain.ChatMessage> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CHAT_MESSAGES + " WHERE " + COLUMN_MSG_ROOM_ID + " = ? ORDER BY " + COLUMN_MSG_ID + " ASC", new String[]{roomId});
        if (c.moveToFirst()) {
            do {
                User sender = getUser(c.getString(c.getColumnIndexOrThrow(COLUMN_MSG_SENDER_ID)));
                if (sender != null) {
                    com.majorshare.core.domain.ChatMessage msg = new com.majorshare.core.domain.ChatMessage(null, sender, c.getString(c.getColumnIndexOrThrow(COLUMN_MSG_CONTENT)));
                    msg.setMessageId(c.getLong(c.getColumnIndexOrThrow(COLUMN_MSG_ID)));
                    msg.setImageData(c.getString(c.getColumnIndexOrThrow(COLUMN_MSG_IMAGE)));
                    msg.setRead(c.getInt(c.getColumnIndexOrThrow(COLUMN_MSG_IS_READ)) == 1);
                    msg.setSendTime(java.time.LocalDateTime.parse(c.getString(c.getColumnIndexOrThrow(COLUMN_MSG_TIME))));
                    list.add(msg);
                }
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // --- Notification Methods (Design: Notification) ---
    public long insertNotification(String receiverId, String type, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_NOTIF_RECEIVER_ID, receiverId);
        v.put(COLUMN_NOTIF_TYPE, type);
        v.put(COLUMN_NOTIF_CONTENT, content);
        v.put(COLUMN_NOTIF_IS_READ, 0);
        return db.insert(TABLE_NOTIFICATIONS, null, v);
    }

    public java.util.List<com.majorshare.core.domain.Notification> getNotificationsForUser(String userId) {
        java.util.List<com.majorshare.core.domain.Notification> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_NOTIF_RECEIVER_ID + " = ? ORDER BY " + COLUMN_NOTIF_ID + " DESC", new String[]{userId});
        if (c.moveToFirst()) {
            do {
                User receiver = getUser(userId);
                if (receiver != null) {
                    com.majorshare.core.domain.Notification notif = new com.majorshare.core.domain.Notification(c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIF_TYPE)), 
                                                                                                          c.getString(c.getColumnIndexOrThrow(COLUMN_NOTIF_CONTENT)), receiver);
                    notif.setNotificationId(c.getLong(c.getColumnIndexOrThrow(COLUMN_NOTIF_ID)));
                    if (c.getInt(c.getColumnIndexOrThrow(COLUMN_NOTIF_IS_READ)) == 1) notif.markAsRead();
                    list.add(notif);
                }
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void markNotificationsAsRead(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_NOTIF_IS_READ, 1);
        db.update(TABLE_NOTIFICATIONS, v, COLUMN_NOTIF_RECEIVER_ID + " = ?", new String[]{userId});
    }

    // --- Admin Operations (Design: Admin) ---
    public java.util.List<User> getAllUsers() {
        java.util.List<User> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (c.moveToFirst()) {
            do {
                User u = new User(c.getString(c.getColumnIndexOrThrow(COLUMN_USER_ID)), c.getString(c.getColumnIndexOrThrow(COLUMN_PASSWORD)), c.getString(c.getColumnIndexOrThrow(COLUMN_NAME)));
                u.setRole(c.getString(c.getColumnIndexOrThrow(COLUMN_ROLE)));
                u.changeAccountStatus(c.getString(c.getColumnIndexOrThrow(COLUMN_ACCOUNT_STATUS)));
                u.setMannerScore(c.getFloat(c.getColumnIndexOrThrow(COLUMN_MANNER_SCORE)));
                list.add(u);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void updateUserAccountStatus(String userId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_ACCOUNT_STATUS, status);
        db.update(TABLE_USERS, v, COLUMN_USER_ID + " = ?", new String[]{userId});
    }

    public void updateUserInfo(String userId, String newName, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_NAME, newName);
        v.put(COLUMN_PASSWORD, newPassword);
        db.update(TABLE_USERS, v, COLUMN_USER_ID + " = ?", new String[]{userId});
    }

    public int getExtensionCountForTransaction(Long transId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EXTENSIONS + " WHERE " + COLUMN_EXT_TRANS_ID + " = ? AND " + COLUMN_EXT_STATUS + " != '거절'", new String[]{String.valueOf(transId)});
        int count = 0; if (c.moveToFirst()) count = c.getInt(0); c.close();
        return count;
    }

    // --- System Statistics for Admin (Design: loadSystemData) ---
    public android.os.Bundle getSystemStats() {
        android.os.Bundle stats = new android.os.Bundle();
        SQLiteDatabase db = this.getReadableDatabase();

        // Total Users
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        if (c.moveToFirst()) stats.putInt("total_users", c.getInt(0));
        c.close();

        // Total Items
        c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS, null);
        if (c.moveToFirst()) stats.putInt("total_items", c.getInt(0));
        c.close();

        // Total Transactions
        c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS, null);
        if (c.moveToFirst()) stats.putInt("total_transactions", c.getInt(0));
        c.close();

        // Suspended/Banned Users
        c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + COLUMN_ACCOUNT_STATUS + " IN ('SUSPENDED', 'BANNED')", null);
        if (c.moveToFirst()) stats.putInt("blocked_users", c.getInt(0));
        c.close();

        return stats;
    }
}
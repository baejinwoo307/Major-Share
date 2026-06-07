package com.majorshare.core.controller;

import android.content.Context;
import com.majorshare.core.db.DatabaseHelper;
import com.majorshare.core.domain.Item;
import java.util.List;

public class ItemRepository {

    private static ItemRepository instance;

    private ItemRepository() {
        // 싱글톤
    }

    public static ItemRepository getInstance() {
        if (instance == null) {
            instance = new ItemRepository();
        }
        return instance;
    }

    public List<Item> getItems(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        return dbHelper.getAllItems();
    }

    public Item getItemById(Context context, Long id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        return dbHelper.getItemById(id);
    }

    public void addItem(Context context, Item item) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        long newId = dbHelper.insertItem(item);
        item.setItemId(newId);
    }

    public void updateItemStatus(Context context, Long itemId, String newStatus) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.updateItemStatus(itemId, newStatus);
    }

    public void updateItemDetails(Context context, Long itemId, String title, int price, String rentalEndDate) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.updateItemDetails(itemId, title, price, rentalEndDate);
    }

    public void deleteItem(Context context, Long itemId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.deleteItem(itemId);
    }
}
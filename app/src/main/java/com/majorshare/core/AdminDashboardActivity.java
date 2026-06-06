package com.majorshare.core;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.majorshare.core.controller.AuthManager;
import com.majorshare.core.controller.ItemRepository;
import com.majorshare.core.domain.Item;
import com.majorshare.core.domain.User;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAdminItems;
    private AdminItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        User currentUser = AuthManager.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            Toast.makeText(this, "관리자 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerViewAdminItems = findViewById(R.id.recyclerViewAdminItems);
        recyclerViewAdminItems.setLayoutManager(new LinearLayoutManager(this));

        List<Item> allItems = ItemRepository.getInstance().getItems(this);
        adapter = new AdminItemAdapter(allItems);
        recyclerViewAdminItems.setAdapter(adapter);
    }
}

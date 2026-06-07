package com.majorshare.core;

import android.os.Bundle;
import android.view.View;
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
    private RecyclerView recyclerViewAdminUsers;
    private AdminItemAdapter itemAdapter;
    private AdminUserAdapter userAdapter;
    private android.widget.TextView tvSystemStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvSystemStats = findViewById(R.id.tvSystemStats);
        updateStats();

        User currentUser = AuthManager.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            Toast.makeText(this, "관리자 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerViewAdminItems = findViewById(R.id.recyclerViewAdminItems);
        recyclerViewAdminUsers = findViewById(R.id.recyclerViewAdminUsers);
        android.widget.Button btnTabItems = findViewById(R.id.btnTabItems);
        android.widget.Button btnTabUsers = findViewById(R.id.btnTabUsers);

        recyclerViewAdminItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdminUsers.setLayoutManager(new LinearLayoutManager(this));

        List<Item> allItems = ItemRepository.getInstance().getItems(this);
        itemAdapter = new AdminItemAdapter(allItems);
        recyclerViewAdminItems.setAdapter(itemAdapter);

        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(this);
        List<User> allUsers = db.getAllUsers();
        userAdapter = new AdminUserAdapter(allUsers);
        recyclerViewAdminUsers.setAdapter(userAdapter);

        btnTabItems.setOnClickListener(v -> {
            recyclerViewAdminItems.setVisibility(View.VISIBLE);
            recyclerViewAdminUsers.setVisibility(View.GONE);
        });

        btnTabUsers.setOnClickListener(v -> {
            recyclerViewAdminItems.setVisibility(View.GONE);
            recyclerViewAdminUsers.setVisibility(View.VISIBLE);
            
            // 유저 목록 최신화
            List<User> updatedUsers = db.getAllUsers();
            userAdapter.updateList(updatedUsers);
            updateStats();
        });
    }

    private void updateStats() {
        com.majorshare.core.dto.SystemDataDTO data = com.majorshare.core.controller.Admin.getInstance().loadSystemData(this);
        String statsText = "• 총 유저: " + data.getTotalUsers() + "명 (정지/차단: " + data.getBlockedUsers() + "명)\n" +
                           "• 총 등록 물품: " + data.getTotalItems() + "건\n" +
                           "• 누적 거래 횟수: " + data.getTotalTransactions() + "건";
        tvSystemStats.setText(statsText);
    }
}

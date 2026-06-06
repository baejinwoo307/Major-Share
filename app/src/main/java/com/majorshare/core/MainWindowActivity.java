package com.majorshare.core;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.majorshare.core.controller.ItemRepository;
import java.util.ArrayList;
import java.util.List;
import com.majorshare.core.domain.Item;

public class MainWindowActivity extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private ItemAdapter itemAdapter; // 어댑터를 멤버 변수로 빼서 접근하기 쉽게 변경

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);

        EditText etSearch = findViewById(R.id.etSearch);
        Button btnSearch = findViewById(R.id.btnSearch);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);
        Button btnMyPage = findViewById(R.id.btnMyPage);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        // 현재 로그인한 사용자가 관리자(ADMIN)일 경우에만 버튼 노출
        com.majorshare.core.domain.User currentUser = com.majorshare.core.controller.AuthManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getRole().equals("ADMIN")) {
            btnAdmin.setVisibility(android.view.View.VISIBLE);
        }

        String[] categories = {"전체", "전공 서적", "교양 서적", "기타 물품"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            String selectedCategory = spinnerCategory.getSelectedItem().toString();

            // 저장소에서 모든 아이템을 가져온 뒤, 조건에 맞는 것만 새 리스트에 담기
            List<Item> allItems = ItemRepository.getInstance().getItems(this);
            List<Item> filteredList = new ArrayList<>();

            for (Item item : allItems) {
                boolean matchesCategory = selectedCategory.equals("전체") || item.getCategory().equals(selectedCategory);
                boolean matchesQuery = query.isEmpty() || item.getTitle().contains(query);

                if (matchesCategory && matchesQuery) {
                    filteredList.add(item);
                }
            }

            // 필터링된 결과가 없으면 안내 메시지 띄우기
            if (filteredList.isEmpty()) {
                Toast.makeText(MainWindowActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }

            // 어댑터에 필터링된 리스트를 넘겨주고 화면 갱신
            if (itemAdapter != null) {
                itemAdapter.updateList(filteredList);
            }
        });

        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainWindowActivity.this, ItemRegisterActivity.class);
            startActivity(intent);
        });

        btnMyPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainWindowActivity.this, TransactionManagementActivity.class);
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainWindowActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 켜질 때는 항상 전체 리스트를 보여줌
        List<Item> currentItems = ItemRepository.getInstance().getItems(this);
        itemAdapter = new ItemAdapter(currentItems);
        recyclerViewItems.setAdapter(itemAdapter);
    }
}
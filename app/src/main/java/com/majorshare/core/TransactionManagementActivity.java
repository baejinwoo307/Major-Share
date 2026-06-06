package com.majorshare.core;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.majorshare.core.controller.AuthManager;
import com.majorshare.core.controller.ItemRepository;
import com.majorshare.core.controller.TransactionRepository;
import com.majorshare.core.domain.Item;
import com.majorshare.core.domain.Transaction;
import com.majorshare.core.domain.User;

import java.util.ArrayList;
import java.util.List;

public class TransactionManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyItems;
    private RecyclerView recyclerViewTransactions;
    private ItemAdapter myItemAdapter;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_management);

        recyclerViewMyItems = findViewById(R.id.recyclerViewMyItems);
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        
        recyclerViewMyItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }
    
    private void loadData() {
        User currentUser = AuthManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 1. 내가 등록한 물품 가져오기
        List<Item> allItems = ItemRepository.getInstance().getItems(this);
        List<Item> myItems = new ArrayList<>();
        for (Item item : allItems) {
            if (item.getOwner().getUserId().equals(currentUser.getUserId())) {
                myItems.add(item);
            }
        }
        myItemAdapter = new ItemAdapter(myItems);
        recyclerViewMyItems.setAdapter(myItemAdapter);

        // 2. 로그인된 유저와 관련된 거래 내역 가져오기
        List<Transaction> myTransactions = TransactionRepository.getInstance()
                .getTransactionsRelatedToUser(this, currentUser.getUserId());

        transactionAdapter = new TransactionAdapter(myTransactions, currentUser.getUserId());
        recyclerViewTransactions.setAdapter(transactionAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
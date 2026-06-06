package com.majorshare.core;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.majorshare.core.controller.AuthManager;
import com.majorshare.core.controller.ItemRepository;
import com.majorshare.core.controller.TransactionRepository;
import com.majorshare.core.domain.Item;
import com.majorshare.core.domain.Reservation;
import com.majorshare.core.domain.Transaction;
import com.majorshare.core.domain.User;

public class ItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDetailType = findViewById(R.id.tvDetailType);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);
        Button btnRequestTransaction = findViewById(R.id.btnRequestTransaction);

        long itemId = getIntent().getLongExtra("itemId", -1);
        if (itemId == -1) {
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Item currentItem = ItemRepository.getInstance().getItemById(this, itemId);
        if (currentItem == null) {
            Toast.makeText(this, "물품을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailTitle.setText(currentItem.getTitle());
        tvDetailCategory.setText("카테고리: " + currentItem.getCategory());
        tvDetailType.setText("거래 방식: " + currentItem.getTransactionType() + " | 상태: " + currentItem.getStatus());
        tvDetailPrice.setText("가격: " + currentItem.getPrice() + "원");

        User currentUser = AuthManager.getInstance().getCurrentUser();
        
        // 소유자 본인일 경우 신청 불가
        if (currentUser != null && currentUser.getUserId().equals(currentItem.getOwner().getUserId())) {
            btnRequestTransaction.setVisibility(View.GONE);
            return;
        }

        // 상태에 따른 버튼 텍스트 및 로직 분기
        String status = currentItem.getStatus();
        if (status.equals("대여가능") || status.equals("판매중")) {
            btnRequestTransaction.setText(currentItem.getTransactionType().equals("매매") ? "구매 신청" : "대여 신청");
            btnRequestTransaction.setOnClickListener(v -> {
                Transaction transaction = new Transaction(currentItem, currentUser);
                TransactionRepository.getInstance().addTransaction(this, transaction);
                
                // 도메인 요구사항: 신청 시 알림을 보내거나 승인 대기로 전환
                Toast.makeText(ItemDetailActivity.this, currentItem.getTitle() + " 거래를 신청했습니다!", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else if (status.equals("대여중") || status.equals("거래중")) {
            if (currentItem.getTransactionType().equals("대여")) {
                btnRequestTransaction.setText("예약 대기 신청");
                btnRequestTransaction.setOnClickListener(v -> {
                    Reservation reservation = new Reservation(currentItem, currentUser);
                    TransactionRepository.getInstance().addReservation(this, reservation);
                    
                    Toast.makeText(ItemDetailActivity.this, "예약 대기가 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                // 매매중인 상품이 거래중이면 더 이상 신청 불가
                btnRequestTransaction.setText("거래 진행 중");
                btnRequestTransaction.setEnabled(false);
            }
        } else if (status.equals("판매완료")) {
            btnRequestTransaction.setText("판매 완료");
            btnRequestTransaction.setEnabled(false);
        }
    }
}

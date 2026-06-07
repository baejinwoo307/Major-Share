package com.majorshare.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        TextView tvOwnerProfile = findViewById(R.id.tvOwnerProfile);
        ImageView ivDetailImage = findViewById(R.id.ivDetailImage);
        Button btnRequestTransaction = findViewById(R.id.btnRequestTransaction);
        LinearLayout layoutOwnerActions = findViewById(R.id.layoutOwnerActions);
        Button btnEditItem = findViewById(R.id.btnEditItem);
        Button btnDeleteItem = findViewById(R.id.btnDeleteItem);

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

        if (currentItem.getImageBase64() != null && !currentItem.getImageBase64().trim().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(currentItem.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) {
                    ivDetailImage.setImageBitmap(bitmap);
                    ivDetailImage.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        tvDetailTitle.setText(currentItem.getTitle());
        tvDetailCategory.setText("카테고리: " + currentItem.getCategory());
        if ("대여".equals(currentItem.getTransactionType())) {
            tvDetailType.setText("거래 방식: " + currentItem.getTransactionType() + " | 상태: " + currentItem.getStatus() + " | 대여 가능 기한: " + currentItem.getRentalEndDate());
        } else {
            tvDetailType.setText("거래 방식: " + currentItem.getTransactionType() + " | 상태: " + currentItem.getStatus());
        }
        tvDetailPrice.setText("가격: " + currentItem.getPrice() + "원");
        com.majorshare.core.db.DatabaseHelper dbHelper = new com.majorshare.core.db.DatabaseHelper(this);
        int resCount = dbHelper.getReservationCountForItem(itemId);
        
        User currentUser = AuthManager.getInstance().getCurrentUser();
        Button btnViewOwnerReviews = findViewById(R.id.btnViewOwnerReviews);

        if (currentItem.getOwner() != null) {
            String mannerText = String.format("%.1f", currentItem.getOwner().getMannerScore());
            int transCount = dbHelper.getUserTransactionCount(currentItem.getOwner().getUserId());
            tvOwnerProfile.setText("등록자: " + currentItem.getOwner().getName() + " (매너온도: " + mannerText + "점, 완료된 거래: " + transCount + "회)\n현재 예약 대기자: " + resCount + "명");
            
            // 본인이 아닐 경우에만 판매자 리뷰 보기 노출
            if (currentUser == null || !currentUser.getUserId().trim().equalsIgnoreCase(currentItem.getOwner().getUserId().trim())) {
                btnViewOwnerReviews.setVisibility(View.VISIBLE);
                btnViewOwnerReviews.setOnClickListener(v -> showOwnerReviewsDialog(currentItem.getOwner()));
            }
        }
        
        // 소유자 본인일 경우 처리
        if (currentUser != null && currentItem.getOwner() != null && 
            currentUser.getUserId().trim().equalsIgnoreCase(currentItem.getOwner().getUserId().trim())) {
            btnRequestTransaction.setVisibility(View.GONE);
            layoutOwnerActions.setVisibility(View.VISIBLE);
            
            Button btnViewReservations = new Button(this);
            btnViewReservations.setText("예약자 명단 보기");
            btnViewReservations.setOnClickListener(v -> showReservationList(currentItem.getItemId()));
            layoutOwnerActions.addView(btnViewReservations, 0);

            btnEditItem.setOnClickListener(v -> showEditDialog(currentItem));
            
            btnDeleteItem.setOnClickListener(v -> {
                showDeleteConfirmDialog(currentItem.getItemId());
            });
            return;
        }

        // [설계서 보완] 관리자일 경우 소유자가 아니더라도 삭제 버튼 노출
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            layoutOwnerActions.setVisibility(View.VISIBLE);
            btnEditItem.setVisibility(View.GONE); // 관리자는 수정은 불가 (삭제만 가능)
            btnDeleteItem.setOnClickListener(v -> {
                showDeleteConfirmDialog(currentItem.getItemId());
            });
        }

        // 상태에 따른 버튼 텍스트 및 로직 분기
        String status = currentItem.getStatus();
        
        // 1. 이미 거래/예약 신청을 한 내역이 있는지 확인
        boolean hasPendingRequest = false;
        for (Transaction t : TransactionRepository.getInstance().getTransactions(this)) {
            if (t.getSubjectItem().getItemId().equals(currentItem.getItemId()) && 
                t.getBuyer() != null && t.getBuyer().getUserId().trim().equalsIgnoreCase(currentUser.getUserId().trim())) {
                hasPendingRequest = true;
                break;
            }
        }
        for (Reservation r : TransactionRepository.getInstance().getReservations(this)) {
            if (r.getTargetItem().getItemId().equals(currentItem.getItemId()) && 
                r.getBorrower() != null && r.getBorrower().getUserId().trim().equalsIgnoreCase(currentUser.getUserId().trim())) {
                hasPendingRequest = true;
                break;
            }
        }
        
        if (hasPendingRequest) {
            btnRequestTransaction.setText("이미 신청한 물품입니다");
            btnRequestTransaction.setEnabled(false);
            return;
        }

        if (status.equals("대여가능") || status.equals("판매중")) {
            btnRequestTransaction.setText(currentItem.getTransactionType().trim().equals("매매") ? "구매 신청" : "대여 신청");
            btnRequestTransaction.setOnClickListener(v -> {
                int currentResCount = dbHelper.getReservationCountForItem(currentItem.getItemId());
                new AlertDialog.Builder(ItemDetailActivity.this)
                    .setTitle(currentItem.getTransactionType().trim().equals("매매") ? "구매 신청" : "대여 신청")
                    .setMessage("현재 예약 대기자: " + currentResCount + "명\n신청하시겠습니까?")
                    .setPositiveButton("신청", (dialog, which) -> {
                        Transaction transaction = new Transaction(currentItem, currentUser);
                        TransactionRepository.getInstance().addTransaction(this, transaction);
                        
                        // Local Notification (Owner)
                        com.majorshare.core.util.NotificationHelper.showNotification(ItemDetailActivity.this, 
                            currentItem.getOwner().getUserId(),
                            "거래 신청 알림", 
                            "'" + currentItem.getTitle() + "' 물품에 대한 새로운 거래 신청이 있습니다.");
                        
                        Toast.makeText(ItemDetailActivity.this, currentItem.getTitle() + " 거래를 신청했습니다!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("취소", null)
                    .show();
            });
        } else if (status.equals("대여중") || status.equals("거래중")) {
            if (currentItem.getTransactionType().trim().equals("대여")) {
                btnRequestTransaction.setText("예약 대기 신청");
                btnRequestTransaction.setOnClickListener(v -> {
                    int currentResCount = dbHelper.getReservationCountForItem(currentItem.getItemId());
                    
                    // [설계서 보완] 최대 예약 인원 제한 (Max 3명)
                    if (currentResCount >= 3) {
                        Toast.makeText(ItemDetailActivity.this, "예약 대기 인원이 가득 찼습니다 (최대 3명).", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new AlertDialog.Builder(ItemDetailActivity.this)
                        .setTitle("예약 대기 신청")
                        .setMessage("현재 예약 대기자: " + currentResCount + "명\n예약 대기를 신청하시겠습니까?")
                        .setPositiveButton("신청", (dialog, which) -> {
                            Reservation reservation = new Reservation(currentItem, currentUser);
                            TransactionRepository.getInstance().addReservation(this, reservation);
                            
                            // Local Notification (Owner)
                            com.majorshare.core.util.NotificationHelper.showNotification(ItemDetailActivity.this, 
                                currentItem.getOwner().getUserId(),
                                "예약 신청 알림", 
                                "'" + currentItem.getTitle() + "' 물품에 새로운 예약 대기가 등록되었습니다.");
                            
                            Toast.makeText(ItemDetailActivity.this, "예약 대기가 완료되었습니다!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("취소", null)
                        .show();
                });
            } else {
                btnRequestTransaction.setText("거래 진행 중");
                btnRequestTransaction.setEnabled(false);
            }
        } else if (status.equals("판매완료")) {
            btnRequestTransaction.setText("판매 완료");
            btnRequestTransaction.setEnabled(false);
        }
    }

    private void showEditDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("물품 정보 수정");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etTitle = new EditText(this);
        etTitle.setHint("물품 제목");
        etTitle.setText(item.getTitle());
        layout.addView(etTitle);

        final EditText etPrice = new EditText(this);
        etPrice.setHint("가격");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etPrice.setText(String.valueOf(item.getPrice()));
        layout.addView(etPrice);

        final TextView tvRentalDate = new TextView(this);
        if (item.getTransactionType().trim().equals("대여")) {
            tvRentalDate.setHint("대여 가능 기한 (예: 2026-06-20)");
            tvRentalDate.setTextSize(16f);
            tvRentalDate.setPadding(0, 30, 0, 30);
            tvRentalDate.setText(item.getRentalEndDate());
            tvRentalDate.setOnClickListener(v -> {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                    tvRentalDate.setText(String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth));
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
            });
            layout.addView(tvRentalDate);
        }

        builder.setView(layout);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String newTitle = etTitle.getText().toString().trim();
            String newPriceStr = etPrice.getText().toString().trim();
            
            if (newTitle.isEmpty() || newPriceStr.isEmpty()) {
                Toast.makeText(this, "정보를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int newPrice = Integer.parseInt(newPriceStr);
            String newRentalDate = "";
            if (item.getTransactionType().trim().equals("대여")) {
                newRentalDate = tvRentalDate.getText().toString().trim();
            }

            ItemRepository.getInstance().updateItemDetails(this, item.getItemId(), newTitle, newPrice, newRentalDate);
            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
            recreate();
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void showReservationList(Long itemId) {
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(this);
        java.util.List<com.majorshare.core.domain.Reservation> allRes = db.getAllReservations();
        java.util.List<String> displayList = new java.util.ArrayList<>();
        for (com.majorshare.core.domain.Reservation r : allRes) {
            if (r.getTargetItem().getItemId().equals(itemId) && "대기".equals(r.getStatus())) {
                displayList.add("대기자: " + r.getBorrower().getName() + " (" + r.getBorrower().getUserId() + ")");
            }
        }
        
        String message = displayList.isEmpty() ? "현재 대기 중인 예약자가 없습니다." : String.join("\n", displayList);
        new AlertDialog.Builder(this)
            .setTitle("예약자 명단")
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show();
    }

    private void showDeleteConfirmDialog(Long itemId) {
        new AlertDialog.Builder(this)
            .setTitle("물품 삭제")
            .setMessage("정말로 이 물품을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.")
            .setPositiveButton("삭제", (dialog, which) -> {
                ItemRepository.getInstance().deleteItem(this, itemId);
                Toast.makeText(this, "물품이 영구 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            })
            .setNegativeButton("취소", null)
            .show();
    }

    private void showOwnerReviewsDialog(com.majorshare.core.domain.User owner) {
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(this);
        java.util.List<com.majorshare.core.domain.Review> reviews = db.getReviewsForUser(owner.getUserId());
        
        java.util.List<String> displayList = new java.util.ArrayList<>();
        for (com.majorshare.core.domain.Review r : reviews) {
            displayList.add("★ " + r.getScore() + "점 | " + r.getContent() + " (작성자: " + r.getWriterUserId() + ", " + r.getDate() + ")");
        }
        
        String message = displayList.isEmpty() ? "등록된 리뷰가 없습니다." : String.join("\n\n", displayList);
        new AlertDialog.Builder(this)
            .setTitle(owner.getName() + "님의 리뷰 목록")
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show();
    }
}

package com.majorshare.core;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

public class TransactionManagementActivity extends AppCompatActivity implements TransactionAdapter.OnTransactionChangedListener {

    private RecyclerView recyclerViewMyItems;
    private RecyclerView recyclerViewTransactions;
    private RecyclerView recyclerViewCompletedTransactions;
    private ItemAdapter myItemAdapter;
    private TransactionAdapter transactionAdapter;
    private TransactionAdapter completedTransactionAdapter;

    @Override
    public void onTransactionChanged() {
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_management);

        recyclerViewMyItems = findViewById(R.id.recyclerViewMyItems);
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        recyclerViewCompletedTransactions = findViewById(R.id.recyclerViewCompletedTransactions);
        
        TextView tabOngoing = findViewById(R.id.tabOngoing);
        TextView tabCompleted = findViewById(R.id.tabCompleted);

        recyclerViewMyItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCompletedTransactions.setLayoutManager(new LinearLayoutManager(this));

        tabOngoing.setOnClickListener(v -> {
            recyclerViewTransactions.setVisibility(android.view.View.VISIBLE);
            recyclerViewCompletedTransactions.setVisibility(android.view.View.GONE);
            tabOngoing.setBackgroundColor(android.graphics.Color.parseColor("#FF5722"));
            tabOngoing.setTextColor(android.graphics.Color.WHITE);
            tabCompleted.setBackgroundColor(android.graphics.Color.parseColor("#DDDDDD"));
            tabCompleted.setTextColor(android.graphics.Color.parseColor("#888888"));
        });

        tabCompleted.setOnClickListener(v -> {
            recyclerViewTransactions.setVisibility(android.view.View.GONE);
            recyclerViewCompletedTransactions.setVisibility(android.view.View.VISIBLE);
            tabCompleted.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"));
            tabCompleted.setTextColor(android.graphics.Color.WHITE);
            tabOngoing.setBackgroundColor(android.graphics.Color.parseColor("#DDDDDD"));
            tabOngoing.setTextColor(android.graphics.Color.parseColor("#888888"));
        });

        Button btnViewMyReviews = findViewById(R.id.btnViewMyReviews);
        btnViewMyReviews.setOnClickListener(v -> {
            User currentUser = AuthManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                showUserReviewsDialog(currentUser.getUserId(), "내 리뷰 목록");
            }
        });

        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            AuthManager.getInstance().logout(this);
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadData();
    }

    private void showEditProfileDialog() {
        User currentUser = AuthManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("회원 정보 수정");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final android.widget.EditText etName = new android.widget.EditText(this);
        etName.setHint("새 이름");
        etName.setText(currentUser.getName());
        layout.addView(etName);

        final android.widget.EditText etPassword = new android.widget.EditText(this);
        etPassword.setHint("새 비밀번호");
        etPassword.setInputType(android.view.inputmethod.EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setText(currentUser.getPassword());
        layout.addView(etPassword);

        builder.setView(layout);

        builder.setPositiveButton("수정", (dialog, which) -> {
            String newName = etName.getText().toString().trim();
            String newPass = etPassword.getText().toString().trim();
            
            if (newName.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "빈칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(this);
            db.updateUserInfo(currentUser.getUserId(), newName, newPass);
            
            // 현재 세션 정보 업데이트
            currentUser.updateName(newName);
            currentUser.updatePassword(newPass);
            
            Toast.makeText(this, "정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
            loadData();
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void showUserReviewsDialog(String userId, String title) {
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(this);
        List<com.majorshare.core.domain.Review> reviews = db.getReviewsForUser(userId);
        
        List<String> displayList = new ArrayList<>();
        for (com.majorshare.core.domain.Review r : reviews) {
            displayList.add("★ " + r.getScore() + "점 | " + r.getContent() + " (작성자: " + r.getWriterUserId() + ", " + r.getDate() + ")");
        }
        
        String message = displayList.isEmpty() ? "등록된 리뷰가 없습니다." : String.join("\n\n", displayList);
        new android.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show();
    }
    
    private void loadData() {
        User currentUser = AuthManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // DB에서 최신 유저 정보(매너온도 등)를 다시 불러옴
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(this);
        User latestUser = db.getUser(currentUser.getUserId());
        if(latestUser != null) {
            currentUser = latestUser;
            AuthManager.getInstance().setCurrentUser(latestUser); // 캐시 업데이트
        }

        TextView tvMyProfile = findViewById(R.id.tvMyProfile);
        String mannerText = String.format("%.1f", currentUser.getMannerScore());
        int transCount = db.getUserTransactionCount(currentUser.getUserId());
        tvMyProfile.setText("환영합니다, " + currentUser.getName() + "님\n나의 매너온도: " + mannerText + "점 | 완료된 거래: " + transCount + "회");

        // 1. 내가 등록한 물품 가져오기
        List<Item> allItems = ItemRepository.getInstance().getItems(this);
        List<Item> myItems = new ArrayList<>();
        for (Item item : allItems) {
            if (item.getOwner() != null && item.getOwner().getUserId().trim().equalsIgnoreCase(currentUser.getUserId().trim())) {
                myItems.add(item);
            }
        }
        myItemAdapter = new ItemAdapter(myItems);
        recyclerViewMyItems.setAdapter(myItemAdapter);

        // 2. 로그인된 유저와 관련된 거래 내역 가져오기
        List<Transaction> myTransactions = TransactionRepository.getInstance()
                .getTransactionsRelatedToUser(this, currentUser.getUserId());

        List<Transaction> ongoingTransactions = new ArrayList<>();
        List<Transaction> completedTransactions = new ArrayList<>();

        for(Transaction t : myTransactions) {
            String stage = t.getStage();
            if(stage.equals("거래완료") || stage.equals("반납완료") || stage.equals("거절됨")) {
                completedTransactions.add(t);
            } else {
                ongoingTransactions.add(t);
            }
        }

        // [설계서 보완] 내가 신청한 예약 내역도 '진행 중' 탭에 포함
        List<com.majorshare.core.domain.Reservation> allRes = TransactionRepository.getInstance().getReservations(this);
        for (com.majorshare.core.domain.Reservation r : allRes) {
            if (r.getBorrower().getUserId().equalsIgnoreCase(currentUser.getUserId()) && "대기".equals(r.getStatus())) {
                // Reservation을 Transaction UI에 맞게 가공하여 추가 (또는 전용 어댑터 사용 권장되나 편의상 통합)
                Transaction fakeTrans = new Transaction(r.getTargetItem(), r.getBorrower());
                fakeTrans.setTransactionId(r.getReservationId() * -1); // 예약임을 알리기 위해 음수 ID 부여
                fakeTrans.setStage("예약대기 (" + r.getStatus() + ")");
                ongoingTransactions.add(fakeTrans);
            }
        }

        transactionAdapter = new TransactionAdapter(ongoingTransactions, currentUser.getUserId(), this);
        recyclerViewTransactions.setAdapter(transactionAdapter);

        completedTransactionAdapter = new TransactionAdapter(completedTransactions, currentUser.getUserId(), this);
        recyclerViewCompletedTransactions.setAdapter(completedTransactionAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // 정지 여부 실시간 체크
        AuthManager.getInstance().checkUserStatus(this);
        if (AuthManager.getInstance().getCurrentUser() == null) {
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        loadData();
    }
}
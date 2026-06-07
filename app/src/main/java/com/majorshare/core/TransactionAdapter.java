package com.majorshare.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.majorshare.core.controller.TransactionRepository;
import com.majorshare.core.db.DatabaseHelper;
import com.majorshare.core.domain.Reservation;
import com.majorshare.core.domain.Transaction;
import com.majorshare.core.domain.Extension;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private String currentUserId;
    private OnTransactionChangedListener listener;

    public interface OnTransactionChangedListener {
        void onTransactionChanged();
    }

    public TransactionAdapter(List<Transaction> transactionList, String currentUserId, OnTransactionChangedListener listener) {
        this.transactionList = transactionList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    public void updateList(List<Transaction> newList) {
        this.transactionList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        
        // Null Safety Check
        if (transaction.getSubjectItem() == null || transaction.getSubjectItem().getOwner() == null) {
            holder.tvTransTitle.setText("정보를 불러올 수 없는 거래");
            return;
        }

        boolean isOwner = transaction.getSubjectItem().getOwner().getUserId().trim().equalsIgnoreCase(currentUserId.trim());
        
        holder.tvTransTitle.setText(transaction.getSubjectItem().getTitle());
        holder.tvTransStatus.setText("상태: " + transaction.getStage());
        holder.tvTransRole.setText(isOwner ? "내 역할: 공급자 (판매/대여)" : "내 역할: 수요자 (구매/빌림)");

        DatabaseHelper db = new DatabaseHelper(holder.itemView.getContext());

        // [설계서 보완] 반납 기한 표시 (Due Date)
        if (transaction.getReturnDueDate() != null && "수령완료_대여중".equals(transaction.getStage())) {
            holder.tvReturnDueDate.setVisibility(View.VISIBLE);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            holder.tvReturnDueDate.setText("반납 기한: " + transaction.getReturnDueDate().format(formatter));
            
            // 연체 시 빨간색 강조
            if (transaction.isOverdue()) {
                holder.tvReturnDueDate.setTextColor(android.graphics.Color.RED);
                holder.tvReturnDueDate.setText("반납 기한: " + transaction.getReturnDueDate().format(formatter) + " (연체됨)");
            } else {
                holder.tvReturnDueDate.setTextColor(android.graphics.Color.parseColor("#D32F2F"));
            }
        } else {
            holder.tvReturnDueDate.setVisibility(View.GONE);
        }

        // 모든 버튼 및 레이아웃 초기화
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.btnPickup.setVisibility(View.GONE);
        holder.btnReturn.setVisibility(View.GONE);
        holder.btnExtend.setVisibility(View.GONE);
        holder.layoutExtensionReq.setVisibility(View.GONE);

        // [설계서 보완] 예약 대기 건 처리
        if (transaction.getTransactionId() != null && transaction.getTransactionId() < 0) {
            holder.tvTransStatus.setText("상태: 예약대기");
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.btnReject.setText("예약 취소");
            holder.btnReject.setOnClickListener(v -> {
                long resId = transaction.getTransactionId() * -1;
                db.updateReservationStatus(resId, "취소");
                Toast.makeText(v.getContext(), "예약을 취소했습니다.", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onTransactionChanged();
                else notifyDataSetChanged();
            });
            holder.btnChat.setVisibility(View.GONE); // 예약 단계에선 채팅 비활성화 (선택)
            return;
        } else {
            holder.btnChat.setVisibility(View.VISIBLE);
        }

        String stage = transaction.getStage();
        String type = transaction.getTransactionType();
        
        // 연장 신청(Extension) 정보 확인 (DB 조회)
        Cursor extCursor = db.getExtensionsForTransaction(transaction.getTransactionId());
        Long pendingExtId = null;
        int reqDays = 0;
        String reqReason = "";
        boolean hasPendingExtension = false;
        
        if (extCursor != null && extCursor.moveToFirst()) {
            String extStatus = extCursor.getString(extCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXT_STATUS));
            if ("대기".equals(extStatus)) {
                hasPendingExtension = true;
                pendingExtId = extCursor.getLong(extCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXT_ID));
                reqDays = extCursor.getInt(extCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXT_DAYS));
                reqReason = extCursor.getString(extCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXT_REASON));
            }
            extCursor.close();
        }

        // 1. 공급자: 거래 요청 승인/거절
        if (isOwner && "승인대기".equals(stage)) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        }

        // 1-1. 공급자: 승인 후 물품 전달 (상호 확정 1단계)
        if (isOwner && "승인됨".equals(stage)) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnApprove.setText("물품 전달 완료");
        }

        // 2. 수요자: 공급자가 전달한 물품 수령 확정 (상호 확정 2단계)
        if (!isOwner && "전달중".equals(stage)) {
            holder.btnPickup.setVisibility(View.VISIBLE);
        }

        // 3. 수요자: 대여 중 물품 반납 신청 (상호 확정 3단계)
        if (!isOwner && "수령완료_대여중".equals(stage) && "대여".equals(type)) {
            holder.btnReturn.setVisibility(View.VISIBLE);
            holder.btnReturn.setText("반납 신청");
            if (!hasPendingExtension) {
                holder.btnExtend.setVisibility(View.VISIBLE);
            }
        }

        // 4. 공급자: 수요자가 반납한 물품 최종 승인 (상호 확정 4단계)
        if (isOwner && "반납중".equals(stage)) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnApprove.setText("반납 최종 승인");
        }

        // [설계서 복구] 공급자: 연장 요청(Extension) 승인/거절 UI
        if (isOwner && hasPendingExtension) {
            holder.layoutExtensionReq.setVisibility(View.VISIBLE);
            holder.tvExtensionInfo.setText("연장 요청: " + reqDays + "일 (" + reqReason + ")");
            
            final Long currentExtId = pendingExtId;
            final int currentReqDays = reqDays;
            
            holder.btnExtApprove.setOnClickListener(v -> {
                db.updateExtensionStatus(currentExtId, "승인");
                transaction.extendReturnDueDate(currentReqDays);
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                
                com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                    transaction.getBuyer().getUserId(),
                    "연장 승인 알림", 
                    "'" + transaction.getSubjectItem().getTitle() + "' 연장 신청이 승인되었습니다.");

                Toast.makeText(v.getContext(), "연장 요청을 승인했습니다.", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onTransactionChanged();
                else notifyDataSetChanged();
            });
            
            holder.btnExtReject.setOnClickListener(v -> {
                db.updateExtensionStatus(currentExtId, "거절");
                
                com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                    transaction.getBuyer().getUserId(),
                    "연장 거절 알림", 
                    "'" + transaction.getSubjectItem().getTitle() + "' 연장 신청이 거절되었습니다.");

                Toast.makeText(v.getContext(), "연장 요청을 거절했습니다.", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onTransactionChanged();
                else notifyDataSetChanged();
            });
        }

        // 버튼 클릭 리스너 설정
        holder.btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatRoomActivity.class);
            String partnerName = isOwner ? transaction.getBuyer().getName() : transaction.getSubjectItem().getOwner().getName();
            String partnerId = isOwner ? transaction.getBuyer().getUserId() : transaction.getSubjectItem().getOwner().getUserId();
            intent.putExtra("partnerName", partnerName);
            intent.putExtra("partnerId", partnerId);
            intent.putExtra("itemName", transaction.getSubjectItem().getTitle());
            intent.putExtra("transactionId", String.valueOf(transaction.getTransactionId())); // 공통 방 ID로 사용
            v.getContext().startActivity(intent);
        });

        holder.btnApprove.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TransactionManagementActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if ("승인대기".equals(stage)) {
                transaction.approveRequest();
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
                
                com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                    transaction.getBuyer().getUserId(),
                    "거래 승인 알림", 
                    "'" + transaction.getSubjectItem().getTitle() + "' 거래 신청이 승인되었습니다.",
                    intent);
            } else if ("승인됨".equals(stage)) {
                transaction.markAsDelivered();
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                
                com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                    transaction.getBuyer().getUserId(),
                    "물품 전달 알림", 
                    "'" + transaction.getSubjectItem().getTitle() + "' 물품이 전달되었습니다. 수령을 확정해주세요.",
                    intent);
            } else if ("반납중".equals(stage)) {
                showReviewDialog(v.getContext(), transaction, () -> {
                    transaction.confirmReturn();
                    TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                    com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
                    
                    // [핵심 로직: Auto-Succession (자동 승계)]
                    Long targetItemId = transaction.getSubjectItem().getItemId();
                    TransactionRepository repo = TransactionRepository.getInstance();
                    Reservation nextRes = repo.popNextReservation(v.getContext(), targetItemId);
                    
                    if (nextRes != null) {
                        Transaction newTrans = new Transaction(transaction.getSubjectItem(), nextRes.getBorrower());
                        newTrans.approveRequest(); 
                        repo.addTransaction(v.getContext(), newTrans);
                        com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
                        
                        com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                            nextRes.getBorrower().getUserId(),
                            "대여 권한 승계", 
                            "예약하신 '" + transaction.getSubjectItem().getTitle() + "' 물품의 대여가 가능해졌습니다!",
                            intent);
                    }
                    if (listener != null) listener.onTransactionChanged();
                });
            }
            
            if (listener != null) listener.onTransactionChanged();
            else notifyDataSetChanged();
        });

        holder.btnReject.setOnClickListener(v -> {
            transaction.rejectRequest();
            TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
            
            Intent intent = new Intent(v.getContext(), TransactionManagementActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                transaction.getBuyer().getUserId(),
                "거래 거절 알림", 
                "'" + transaction.getSubjectItem().getTitle() + "' 거래 신청이 거절되었습니다.",
                intent);
                
            if (listener != null) listener.onTransactionChanged();
            else notifyDataSetChanged();
            Toast.makeText(v.getContext(), "거래를 거절했습니다.", Toast.LENGTH_SHORT).show();
        });

        holder.btnPickup.setOnClickListener(v -> {
            showReviewDialog(v.getContext(), transaction, () -> {
                transaction.confirmPickup();
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
                
                Intent intent = new Intent(v.getContext(), TransactionManagementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                    transaction.getSubjectItem().getOwner().getUserId(),
                    "수령 확인 알림", 
                    "수요자가 '" + transaction.getSubjectItem().getTitle() + "' 수령을 확정했습니다.",
                    intent);

                if (listener != null) listener.onTransactionChanged();
                else notifyDataSetChanged();
            });
        });

        holder.btnReturn.setOnClickListener(v -> {
            if ("수령완료_대여중".equals(stage)) {
                boolean isOverdue = transaction.isOverdue();
                transaction.markAsReturned();
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                
                Intent intent = new Intent(v.getContext(), TransactionManagementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                    transaction.getSubjectItem().getOwner().getUserId(),
                    "반납 신청 알림", 
                    "수요자가 '" + transaction.getSubjectItem().getTitle() + "' 반납을 신청했습니다. 승인해주세요.",
                    intent);

                if (isOverdue) {
                    java.time.Duration duration = java.time.Duration.between(transaction.getReturnDueDate(), java.time.LocalDateTime.now());
                    long overdueDays = duration.toDays() + 1; // 최소 1일
                    float penalty = overdueDays * 0.5f;
                    
                    db.updateUserMannerScore(transaction.getBuyer().getUserId(), 3.0f - penalty); 
                    
                    com.majorshare.core.util.NotificationHelper.showNotification(v.getContext(), 
                        transaction.getBuyer().getUserId(),
                        "패널티 알림", 
                        "반납 기한 " + overdueDays + "일 초과로 인해 매너 점수가 " + penalty + "점 감점되었습니다.",
                        intent);
                    Toast.makeText(v.getContext(), "연체 패널티(" + penalty + "점)가 반영되었습니다.", Toast.LENGTH_SHORT).show();
                }
                
                if (listener != null) listener.onTransactionChanged();
                else notifyDataSetChanged();
            }
        });

        holder.btnExtend.setOnClickListener(v -> {
            // [핵심 로직: Block Extension (독점 방지 연장 제한)]
            Long targetItemId = transaction.getSubjectItem().getItemId();
            TransactionRepository repo = TransactionRepository.getInstance();
            
            if (repo.hasReservationsForItem(v.getContext(), targetItemId)) {
                Toast.makeText(v.getContext(), "대기 중인 예약자가 있어 연장할 수 없습니다.", Toast.LENGTH_LONG).show();
            } else {
                // [설계서 보완] 연장 횟수 제한 체크 (Max 1회)
                int extCount = db.getExtensionCountForTransaction(transaction.getTransactionId());
                if (extCount >= 1) {
                    Toast.makeText(v.getContext(), "이미 연장을 완료한 거래입니다 (최대 1회).", Toast.LENGTH_SHORT).show();
                } else {
                    showExtensionDialog(v.getContext(), transaction, db);
                }
            }
        });
    }

    private void showExtensionDialog(Context context, Transaction transaction, DatabaseHelper db) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("기간 연장 신청");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etDays = new EditText(context);
        etDays.setHint("연장 일수 (최대 7일)");
        etDays.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etDays);

        final EditText etReason = new EditText(context);
        etReason.setHint("연장 사유를 입력하세요");
        layout.addView(etReason);

        builder.setView(layout);

        builder.setPositiveButton("신청", (dialog, which) -> {
            String daysStr = etDays.getText().toString().trim();
            String reason = etReason.getText().toString().trim();

            if (daysStr.isEmpty() || reason.isEmpty()) {
                Toast.makeText(context, "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int days = Integer.parseInt(daysStr);
            // [설계서 보완] Item의 maxRentDays 제약 조건 적용
            int maxDays = transaction.getSubjectItem().getMaxRentDays();
            if (days > maxDays) {
                Toast.makeText(context, "연장은 최대 " + maxDays + "일까지만 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // [보안/로직 보완] DB단 insert 시 itemId 넘겨서 예약자 체크 
            long resId = db.insertExtension(transaction.getTransactionId(), days, reason, "대기", transaction.getSubjectItem().getItemId());
            if (resId == -1) {
                Toast.makeText(context, "대기 중인 예약자가 있어 연장할 수 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "연장 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void showReviewDialog(Context context, Transaction transaction, Runnable onSuccess) {
        final float[] selectedScore = {5.0f}; // 기본 5점

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 20);
        layout.setGravity(android.view.Gravity.CENTER);

        android.widget.RatingBar ratingBar = new android.widget.RatingBar(context);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(5.0f);
        ratingBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        
        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) -> {
            if (rating < 1.0f) rb.setRating(1.0f);
            selectedScore[0] = rb.getRating();
        });

        layout.addView(ratingBar);

        android.widget.EditText etReview = new android.widget.EditText(context);
        etReview.setHint("리뷰 내용을 입력해주세요 (선택)");
        etReview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(etReview);

        new AlertDialog.Builder(context)
                .setTitle("거래 상대방 평가")
                .setMessage("상대방의 매너를 평가해주세요 (1~5점)")
                .setView(layout)
                .setPositiveButton("확인", (dialog, which) -> {
                    String partnerId;
                    String currentUserId = com.majorshare.core.controller.AuthManager.getInstance().getCurrentUser().getUserId();
                    if (currentUserId.equals(transaction.getSubjectItem().getOwner().getUserId())) {
                        partnerId = transaction.getBuyer().getUserId();
                    } else {
                        partnerId = transaction.getSubjectItem().getOwner().getUserId();
                    }
                    String content = etReview.getText().toString().trim();
                    com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
                    db.insertReview(transaction.getTransactionId(), currentUserId, partnerId, (int)selectedScore[0], content);
                    
                    onSuccess.run();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransRole, tvTransTitle, tvTransStatus, tvReturnDueDate, tvExtensionInfo;
        Button btnApprove, btnReject, btnPickup, btnReturn, btnExtend, btnChat, btnExtApprove, btnExtReject;
        LinearLayout layoutExtensionReq;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransRole = itemView.findViewById(R.id.tvTransRole);
            tvTransTitle = itemView.findViewById(R.id.tvTransTitle);
            tvTransStatus = itemView.findViewById(R.id.tvTransStatus);
            tvReturnDueDate = itemView.findViewById(R.id.tvReturnDueDate);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnPickup = itemView.findViewById(R.id.btnPickup);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            btnExtend = itemView.findViewById(R.id.btnExtend);
            btnChat = itemView.findViewById(R.id.btnChat);
            
            tvExtensionInfo = itemView.findViewById(R.id.tvExtensionInfo);
            btnExtApprove = itemView.findViewById(R.id.btnExtApprove);
            btnExtReject = itemView.findViewById(R.id.btnExtReject);
            layoutExtensionReq = itemView.findViewById(R.id.layoutExtensionReq);
        }
    }
}
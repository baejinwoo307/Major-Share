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
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private String currentUserId;

    public TransactionAdapter(List<Transaction> transactionList, String currentUserId) {
        this.transactionList = transactionList;
        this.currentUserId = currentUserId;
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
        
        boolean isOwner = transaction.getSubjectItem().getOwner().getUserId().equals(currentUserId);
        
        holder.tvTransTitle.setText(transaction.getSubjectItem().getTitle());
        holder.tvTransStatus.setText("상태: " + transaction.getStage());
        holder.tvTransRole.setText(isOwner ? "내 역할: 공급자 (판매/대여)" : "내 역할: 수요자 (구매/빌림)");

        // 모든 버튼 및 레이아웃 초기화
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.btnPickup.setVisibility(View.GONE);
        holder.btnReturn.setVisibility(View.GONE);
        holder.btnExtend.setVisibility(View.GONE);
        holder.layoutExtensionReq.setVisibility(View.GONE);

        String stage = transaction.getStage();
        String type = transaction.getTransactionType();
        
        // 연장 신청(Extension) 정보 확인 (DB 조회)
        DatabaseHelper db = new DatabaseHelper(holder.itemView.getContext());
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
        if (isOwner && stage.equals("승인대기")) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        }
        
        // 1-1. 공급자: 연장 요청(Extension) 승인/거절 UI
        if (isOwner && hasPendingExtension) {
            holder.layoutExtensionReq.setVisibility(View.VISIBLE);
            holder.tvExtensionInfo.setText("연장 요청: " + reqDays + "일 (" + reqReason + ")");
            
            final Long currentExtId = pendingExtId;
            final int currentReqDays = reqDays;
            
            holder.btnExtApprove.setOnClickListener(v -> {
                db.updateExtensionStatus(currentExtId, "승인");
                transaction.extendReturnDueDate(currentReqDays);
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                Toast.makeText(v.getContext(), "연장 요청을 승인했습니다.", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });
            
            holder.btnExtReject.setOnClickListener(v -> {
                db.updateExtensionStatus(currentExtId, "거절");
                Toast.makeText(v.getContext(), "연장 요청을 거절했습니다.", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });
        }

        // 2. 수요자: 승인된 거래 수령 확정
        if (!isOwner && stage.equals("승인됨")) {
            holder.btnPickup.setVisibility(View.VISIBLE);
        }

        // 3. 수요자: 대여 중 물품 반납 및 연장 신청
        if (!isOwner && stage.equals("수령완료_대여중") && type.equals("대여")) {
            holder.btnReturn.setVisibility(View.VISIBLE);
            if (!hasPendingExtension) {
                holder.btnExtend.setVisibility(View.VISIBLE);
            }
        }

        // 버튼 클릭 리스너 설정
        holder.btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatRoomActivity.class);
            String partnerName = isOwner ? transaction.getBuyer().getName() : transaction.getSubjectItem().getOwner().getName();
            intent.putExtra("partnerName", partnerName);
            intent.putExtra("itemName", transaction.getSubjectItem().getTitle());
            v.getContext().startActivity(intent);
        });

        holder.btnApprove.setOnClickListener(v -> {
            transaction.approveRequest();
            TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
            com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
            notifyDataSetChanged();
            Toast.makeText(v.getContext(), "거래를 승인했습니다.", Toast.LENGTH_SHORT).show();
        });

        holder.btnReject.setOnClickListener(v -> {
            transaction.rejectRequest();
            TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
            notifyDataSetChanged();
            Toast.makeText(v.getContext(), "거래를 거절했습니다.", Toast.LENGTH_SHORT).show();
        });

        holder.btnPickup.setOnClickListener(v -> {
            showReviewDialog(v.getContext(), transaction, () -> {
                transaction.confirmPickup();
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
                notifyDataSetChanged();
                Toast.makeText(v.getContext(), "수령 확정 및 평가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
            });
        });

        holder.btnReturn.setOnClickListener(v -> {
            showReviewDialog(v.getContext(), transaction, () -> {
                transaction.confirmReturn();
                TransactionRepository.getInstance().updateTransaction(v.getContext(), transaction);
                com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
                Toast.makeText(v.getContext(), "반납 및 평가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                
                // [핵심 로직: Auto-Succession (자동 승계)]
                Long targetItemId = transaction.getSubjectItem().getItemId();
                TransactionRepository repo = TransactionRepository.getInstance();
                Reservation nextRes = repo.popNextReservation(v.getContext(), targetItemId);
                
                if (nextRes != null) {
                    Transaction newTrans = new Transaction(transaction.getSubjectItem(), nextRes.getBorrower());
                    newTrans.approveRequest(); // 차기 예약자는 승인된 상태로 시작 (혹은 대기)
                    repo.addTransaction(v.getContext(), newTrans);
                    com.majorshare.core.controller.ItemRepository.getInstance().updateItemStatus(v.getContext(), transaction.getSubjectItem().getItemId(), transaction.getSubjectItem().getStatus());
                    Toast.makeText(v.getContext(), "대기 중인 예약자에게 권한이 승계되었습니다.", Toast.LENGTH_LONG).show();
                }
                
                notifyDataSetChanged();
            });
        });

        holder.btnExtend.setOnClickListener(v -> {
            // [핵심 로직: Block Extension (독점 방지 연장 제한)]
            Long targetItemId = transaction.getSubjectItem().getItemId();
            TransactionRepository repo = TransactionRepository.getInstance();
            
            if (repo.hasReservationsForItem(v.getContext(), targetItemId)) {
                Toast.makeText(v.getContext(), "대기 중인 예약자가 있어 연장할 수 없습니다.", Toast.LENGTH_LONG).show();
            } else {
                showExtensionDialog(v.getContext(), transaction, db);
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
        etDays.setHint("연장 일수 (예: 7)");
        etDays.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etDays);

        final EditText etReason = new EditText(context);
        etReason.setHint("연장 사유를 입력하세요");
        layout.addView(etReason);

        builder.setView(layout);

        builder.setPositiveButton("신청", (dialog, which) -> {
            String daysStr = etDays.getText().toString();
            String reason = etReason.getText().toString();
            
            if(daysStr.isEmpty() || reason.isEmpty()) {
                Toast.makeText(context, "일수와 사유를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int days = Integer.parseInt(daysStr);
            db.insertExtension(transaction.getTransactionId(), days, reason, "대기");
            Toast.makeText(context, "연장 신청이 완료되었습니다. 공급자의 승인을 기다려주세요.", Toast.LENGTH_LONG).show();
            notifyDataSetChanged();
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void showReviewDialog(Context context, Transaction transaction, Runnable onSuccess) {
        String[] options = {"1점 (매우 별로)", "2점", "3점 (보통)", "4점", "5점 (매우 좋음)"};
        final int[] selectedScore = {5}; // 기본 5점

        new AlertDialog.Builder(context)
                .setTitle("거래 상대방 평가")
                .setSingleChoiceItems(options, 4, (dialog, which) -> {
                    selectedScore[0] = which + 1;
                })
                .setPositiveButton("확인", (dialog, which) -> {
                    // 상대방 아이디 구하기 (수요자가 버튼을 눌렀으므로 상대방은 물품의 주인)
                    String partnerId = transaction.getSubjectItem().getOwner().getUserId();
                    com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
                    db.updateUserMannerScore(partnerId, selectedScore[0]);
                    
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
        TextView tvTransRole, tvTransTitle, tvTransStatus, tvExtensionInfo;
        Button btnApprove, btnReject, btnPickup, btnReturn, btnExtend, btnChat, btnExtApprove, btnExtReject;
        LinearLayout layoutExtensionReq;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransRole = itemView.findViewById(R.id.tvTransRole);
            tvTransTitle = itemView.findViewById(R.id.tvTransTitle);
            tvTransStatus = itemView.findViewById(R.id.tvTransStatus);
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
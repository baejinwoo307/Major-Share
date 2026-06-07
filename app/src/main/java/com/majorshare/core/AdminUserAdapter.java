package com.majorshare.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.majorshare.core.controller.Admin;
import com.majorshare.core.domain.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private List<User> userList;

    public AdminUserAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        
        String info = "ID: " + user.getUserId() + "\n이름: " + user.getName() + " | 권한: " + user.getRole() + "\n상태: " + user.getAccountStatus() + " | 매너온도: " + String.format("%.1f", user.getMannerScore()) + "점";
        holder.tvAdminUserDetail.setText(info);

        // 본인은 제재 불가능하게 처리
        com.majorshare.core.domain.User currentAdmin = com.majorshare.core.controller.AuthManager.getInstance().getCurrentUser();
        if (currentAdmin != null && currentAdmin.getUserId().equalsIgnoreCase(user.getUserId())) {
            holder.btnSuspend.setEnabled(false);
            holder.btnBan.setEnabled(false);
            holder.btnActivate.setEnabled(false);
        } else {
            holder.btnSuspend.setEnabled(true);
            holder.btnBan.setEnabled(true);
            holder.btnActivate.setEnabled(true);
        }

        holder.btnSuspend.setOnClickListener(v -> {
            Admin.getInstance().applySuspension(v.getContext(), user.getUserId(), 7);
            user.changeAccountStatus("SUSPENDED");
            notifyDataSetChanged();
            Toast.makeText(v.getContext(), user.getName() + "님을 7일간 정지 처리했습니다.", Toast.LENGTH_SHORT).show();
        });

        holder.btnBan.setOnClickListener(v -> {
            Admin.getInstance().applyPermanentBan(v.getContext(), user.getUserId());
            user.changeAccountStatus("BANNED");
            notifyDataSetChanged();
            Toast.makeText(v.getContext(), user.getName() + "님을 영구 정지 처리했습니다.", Toast.LENGTH_SHORT).show();
        });

        holder.btnActivate.setOnClickListener(v -> {
            Admin.getInstance().liftSanction(v.getContext(), user.getUserId());
            user.changeAccountStatus("ACTIVE");
            notifyDataSetChanged();
            Toast.makeText(v.getContext(), user.getName() + "님의 제재를 해제했습니다.", Toast.LENGTH_SHORT).show();
        });

        holder.btnViewTrans.setOnClickListener(v -> {
            java.util.List<com.majorshare.core.domain.Transaction> transList = Admin.getInstance().viewUserTransactions(v.getContext(), user.getUserId());
            
            String[] items = new String[transList.size()];
            for (int i = 0; i < transList.size(); i++) {
                com.majorshare.core.domain.Transaction t = transList.get(i);
                items[i] = "[" + t.getStage() + "] " + t.getSubjectItem().getTitle();
            }

            new android.app.AlertDialog.Builder(v.getContext())
                .setTitle(user.getName() + "님의 거래 이력 (채팅 감시)")
                .setItems(items, (dialog, which) -> {
                    com.majorshare.core.domain.Transaction selected = transList.get(which);
                    showChatLogDialog(v.getContext(), selected);
                })
                .setPositiveButton("닫기", null)
                .show();
        });
    }

    private void showChatLogDialog(android.content.Context context, com.majorshare.core.domain.Transaction transaction) {
        String roomId = "TRANS_" + transaction.getTransactionId();
        com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
        java.util.List<com.majorshare.core.domain.ChatMessage> logs = db.getChatMessagesByRoomId(roomId);
        
        java.util.List<String> displayLogs = new java.util.ArrayList<>();
        for (com.majorshare.core.domain.ChatMessage msg : logs) {
            displayLogs.add(msg.getSender().getName() + ": " + (msg.getContent().isEmpty() ? "(사진)" : msg.getContent()));
        }

        String content = displayLogs.isEmpty() ? "채팅 내역이 없습니다." : String.join("\n", displayLogs);
        
        new android.app.AlertDialog.Builder(context)
            .setTitle("거래 채팅 로그 감시")
            .setMessage(content)
            .setPositiveButton("확인", null)
            .show();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvAdminUserDetail;
        Button btnSuspend, btnBan, btnActivate, btnViewTrans;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdminUserDetail = itemView.findViewById(R.id.tvAdminUserDetail);
            btnSuspend = itemView.findViewById(R.id.btnSuspend);
            btnBan = itemView.findViewById(R.id.btnBan);
            btnActivate = itemView.findViewById(R.id.btnActivate);
            btnViewTrans = itemView.findViewById(R.id.btnViewTrans);
        }
    }
}

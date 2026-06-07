package com.majorshare.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.majorshare.core.domain.ChatMessage;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messageList;
    private String currentUserId;

    public ChatMessageAdapter(List<ChatMessage> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        
        holder.tvSenderName.setText(message.getSender().getName());

        // 내용이 있으면 텍스트뷰 노출, 없으면 숨김
        if (message.getContent() == null || message.getContent().isEmpty()) {
            holder.tvMessageContent.setVisibility(View.GONE);
        } else {
            holder.tvMessageContent.setVisibility(View.VISIBLE);
            holder.tvMessageContent.setText(message.getContent());
        }

        // 이미지 처리
        if (message.getImageData() != null && !message.getImageData().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(message.getImageData(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.ivMessageImage.setImageBitmap(bitmap);
                holder.ivMessageImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                holder.ivMessageImage.setVisibility(View.GONE);
            }
        } else {
            holder.ivMessageImage.setVisibility(View.GONE);
        }

        // 내가 보낸 메시지면 오른쪽 정렬, 파란색 배경
        if (message.getSender().getUserId().equals(currentUserId)) {
            holder.layoutMessageContainer.setGravity(Gravity.END);
            holder.tvMessageContent.setBackgroundColor(0xFFBBDEFB); // 연한 파란색
            holder.tvSenderName.setVisibility(View.GONE); // 내 이름은 숨김
            holder.ivMessageImage.setPadding(0, 0, 0, 0); // 필요 시 패딩 조정
            
            // 읽음 처리 표시 (내가 보낸 것만 안읽었을 때 '1' 표시)
            if (!message.isRead()) {
                holder.tvReadStatus.setVisibility(View.VISIBLE);
                holder.tvReadStatus.setText("1");
            } else {
                holder.tvReadStatus.setVisibility(View.GONE);
            }
        } else {
            // 상대방 메시지면 왼쪽 정렬, 회색 배경
            holder.layoutMessageContainer.setGravity(Gravity.START);
            holder.tvMessageContent.setBackgroundColor(0xFFE0E0E0);
            holder.tvSenderName.setVisibility(View.VISIBLE);
            holder.tvReadStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMessageContainer;
        TextView tvSenderName, tvMessageContent, tvReadStatus;
        ImageView ivMessageImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMessageContainer = itemView.findViewById(R.id.layoutMessageContainer);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            tvReadStatus = itemView.findViewById(R.id.tvReadStatus);
            ivMessageImage = itemView.findViewById(R.id.ivMessageImage);
        }
    }
}

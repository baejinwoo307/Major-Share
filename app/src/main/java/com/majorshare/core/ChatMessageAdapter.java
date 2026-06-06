package com.majorshare.core;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        
        holder.tvMessageContent.setText(message.getContent());
        holder.tvSenderName.setText(message.getSender().getName());

        // 내가 보낸 메시지면 오른쪽 정렬, 파란색 배경
        if (message.getSender().getUserId().equals(currentUserId)) {
            holder.layoutMessageContainer.setGravity(Gravity.END);
            holder.tvMessageContent.setBackgroundColor(0xFFBBDEFB); // 연한 파란색
            holder.tvSenderName.setVisibility(View.GONE); // 내 이름은 숨김
        } else {
            // 상대방 메시지면 왼쪽 정렬, 회색 배경
            holder.layoutMessageContainer.setGravity(Gravity.START);
            holder.tvMessageContent.setBackgroundColor(0xFFE0E0E0);
            holder.tvSenderName.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutMessageContainer;
        TextView tvSenderName, tvMessageContent;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMessageContainer = itemView.findViewById(R.id.layoutMessageContainer);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
        }
    }
}

package com.majorshare.core;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.majorshare.core.controller.AuthManager;
import com.majorshare.core.db.DatabaseHelper;
import com.majorshare.core.domain.ChatMessage;
import com.majorshare.core.domain.ChatRoom;
import com.majorshare.core.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private ChatMessageAdapter adapter;
    private EditText etMessage;
    private Button btnSend;
    private String currentRoomId;
    private java.util.List<ChatMessage> messageList = new ArrayList<>();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        String partnerName = getIntent().getStringExtra("partnerName");
        String itemName = getIntent().getStringExtra("itemName");
        
        // 고유 방 ID (아이템 이름 + 파트너 조합 등으로 단순화)
        currentRoomId = itemName + "_" + partnerName;

        TextView tvChatTitle = findViewById(R.id.tvChatTitle);
        tvChatTitle.setText(partnerName + "님과의 대화 (" + itemName + ")");

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        currentUser = AuthManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // DB에서 기존 채팅 내역 불러오기
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        messageList = dbHelper.getChatMessagesByRoomId(currentRoomId);

        adapter = new ChatMessageAdapter(messageList, currentUser.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 키보드 올라올 때 스크롤 맨 아래로 유지
        layoutManager.setStackFromEnd(true); 
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                ChatMessage newMessage = new ChatMessage(null, currentUser, text);
                messageList.add(newMessage);
                
                // DB에 저장
                dbHelper.insertChatMessage(currentRoomId, currentUser.getUserId(), text, newMessage.getSendTime().toString());
                
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerViewChat.scrollToPosition(messageList.size() - 1);
                etMessage.setText(""); // 입력창 초기화
            }
        });
    }
}
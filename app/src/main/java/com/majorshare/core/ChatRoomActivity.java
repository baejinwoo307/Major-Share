package com.majorshare.core;

import android.content.Intent;
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
    private android.widget.ImageButton btnAttachImage;
    private android.widget.Button btnSend;
    private String currentRoomId;
    private java.util.List<ChatMessage> messageList = new ArrayList<>();
    private User currentUser;
    private String selectedImageBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        String partnerName = getIntent().getStringExtra("partnerName");
        String partnerId = getIntent().getStringExtra("partnerId");
        String itemName = getIntent().getStringExtra("itemName");
        String transactionIdStr = getIntent().getStringExtra("transactionId");
        
        // 고유 방 ID (물품ID나 거래ID 같은 고유 식별자 사용. 거래가 기준이 되도록 수정)
        if (transactionIdStr != null && !transactionIdStr.isEmpty()) {
            currentRoomId = "TRANS_" + transactionIdStr;
        } else {
            // 하위 호환성 (예외 상황)
            currentRoomId = itemName + "_ROOM";
        }

        TextView tvChatTitle = findViewById(R.id.tvChatTitle);
        tvChatTitle.setText(partnerName + "님과의 대화 (" + itemName + ")");

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnAttachImage = findViewById(R.id.btnAttachImage);

        currentUser = AuthManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // DB에서 기존 채팅 내역 불러오기
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        messageList = dbHelper.getChatMessagesByRoomId(currentRoomId);
        
        // [설계서 보완] 채팅방 진입 시 메시지 읽음 처리 (Sequence Diagram #11)
        dbHelper.markChatAsRead(currentRoomId, currentUser.getUserId());

        adapter = new ChatMessageAdapter(messageList, currentUser.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 키보드 올라올 때 스크롤 맨 아래로 유지
        layoutManager.setStackFromEnd(true); 
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(adapter);

        btnAttachImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1001);
        });

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty() || selectedImageBase64 != null) {
                ChatMessage newMessage = new ChatMessage(null, currentUser, text);
                if (selectedImageBase64 != null) {
                    newMessage.setImageData(selectedImageBase64);
                }
                messageList.add(newMessage);
                
                // DB에 저장
                dbHelper.insertChatMessage(currentRoomId, currentUser.getUserId(), text, selectedImageBase64, newMessage.getSendTime().toString());
                
                // [설계서 보완] 상대방에게 채팅 알림 전송
                if (partnerId != null) {
                    String preview = text.isEmpty() ? "(사진을 보냈습니다)" : text;
                    Intent chatIntent = new Intent(this, ChatRoomActivity.class);
                    chatIntent.putExtra("partnerName", currentUser.getName());
                    chatIntent.putExtra("partnerId", currentUser.getUserId());
                    chatIntent.putExtra("itemName", itemName);
                    chatIntent.putExtra("transactionId", transactionIdStr);
                    chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    com.majorshare.core.util.NotificationHelper.showNotification(this, 
                        partnerId, 
                        "새 메시지 (" + itemName + ")", 
                        currentUser.getName() + ": " + preview,
                        chatIntent);
                }

                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerViewChat.scrollToPosition(messageList.size() - 1);
                etMessage.setText(""); // 입력창 초기화
                selectedImageBase64 = null; // 선택된 이미지 초기화
                btnAttachImage.setColorFilter(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            android.net.Uri imageUri = data.getData();
            try {
                android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // 이미지 압축 및 Base64 변환
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] bytes = baos.toByteArray();
                selectedImageBase64 = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
                
                // 이미지 선택됨 표시 (버튼 색상 변경)
                btnAttachImage.setColorFilter(android.graphics.Color.BLUE);
                Toast.makeText(this, "이미지가 선택되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
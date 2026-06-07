package com.majorshare.core.domain;

import com.majorshare.core.dto.ChatMessageDTO;
import java.time.LocalDateTime;

public class ChatMessage {
    private Long messageId;
    private String content;
    private String imageData;
    private boolean isRead;
    private LocalDateTime sendTime;
    private ChatRoom room;
    private User sender;


    public ChatMessage(ChatRoom room, User sender, String content) {
        this.room = room;
        this.sender = sender;
        this.content = content;
        this.sendTime = LocalDateTime.now();
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageData() {
        return imageData;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isRead() {
        return isRead;
    }

    public ChatMessageDTO getMessageDetailResponse() {
        return new ChatMessageDTO(this.messageId, this.content, this.sendTime);
    }


    public void setMessageId(Long id) { this.messageId = id; }
    public void setSendTime(LocalDateTime time) { this.sendTime = time; }

    public Long getMessageId() { return messageId; }
    public String getContent() { return content; }
    public LocalDateTime getSendTime() { return sendTime; }
    public ChatRoom getRoom() { return room; }
    public User getSender() { return sender; }
}

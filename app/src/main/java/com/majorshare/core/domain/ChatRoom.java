package com.majorshare.core.domain;

import com.majorshare.core.dto.ChatRoomInfoDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatRoom {
    private Long chatRoomId;
    private Item topicItem;
    private List<User> participants;
    private List<ChatMessage> messages;

    public ChatRoom(Item topicItem, List<User> participants) {
        this.topicItem = topicItem;
        this.participants = participants;
        this.messages = new ArrayList<>();
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    public List<ChatMessage> loadChatHistory() {
        return new ArrayList<>(this.messages);
    }

    public ChatRoomInfoDTO getRoomInfoResponse() {
        return new ChatRoomInfoDTO(this.chatRoomId, this.topicItem.getItemId());
    }

    public Long getChatRoomId() { return chatRoomId; }
    public Item getTopicItem() { return topicItem; }
    public List<User> getParticipants() { return participants; }
}
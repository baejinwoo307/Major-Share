package com.majorshare.core.dto;

public class ChatRoomInfoDTO {
    private Long chatRoomId;
    private Long topicItemId;

    public ChatRoomInfoDTO(Long chatRoomId, Long topicItemId) {
        this.chatRoomId = chatRoomId;
        this.topicItemId = topicItemId;
    }
}
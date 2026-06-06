package com.majorshare.core.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long messageId;
    private String content;
    private LocalDateTime sendTime;

    public ChatMessageDTO(Long messageId, String content, LocalDateTime sendTime) {
        this.messageId = messageId;
        this.content = content;
        this.sendTime = sendTime;
    }

    public Long getMessageId() { return messageId; }
    public String getContent() { return content; }
    public LocalDateTime getSendTime() { return sendTime; }
}
package com.majorshare.core.dto;

public class NotificationDetailDTO {
    private Long notificationId;
    private String type;
    private String content;
    private boolean isRead;

    public NotificationDetailDTO(Long notificationId, String type, String content, boolean isRead) {
        this.notificationId = notificationId;
        this.type = type;
        this.content = content;
        this.isRead = isRead;
    }

    public Long getNotificationId() { return notificationId; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public boolean getIsRead() { return isRead; }
}

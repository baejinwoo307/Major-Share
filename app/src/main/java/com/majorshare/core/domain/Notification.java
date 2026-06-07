package com.majorshare.core.domain;

import com.majorshare.core.dto.NotificationDetailDTO;

public class Notification {
    private Long notificationId;
    private String type;
    private String content;
    private boolean isRead;
    private User receiver;


    public Notification(String type, String content, User receiver) {
        this.type = type;
        this.content = content;
        this.receiver = receiver;
        this.isRead = false;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public void sendPushAlert(User receiver, String type, String content) {
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.isRead = false;
    }

    public boolean markAsRead() {
        this.isRead = true;
        return true;
    }

    public NotificationDetailDTO getNotificationDetailResponse() {
        return new NotificationDetailDTO(this.notificationId, this.type, this.content, this.isRead);
    }


    public Long getNotificationId() { return notificationId; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public boolean getIsRead() { return isRead; }
    public User getReceiver() { return receiver; }
}
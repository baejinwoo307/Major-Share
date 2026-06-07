package com.majorshare.core.domain;

public class Review {
    private Long reviewId;
    private String targetUserId;
    private String writerUserId;
    private float score;
    private String content;
    private String date;

    public Review(Long reviewId, String targetUserId, String writerUserId, float score, String content, String date) {
        this.reviewId = reviewId;
        this.targetUserId = targetUserId;
        this.writerUserId = writerUserId;
        this.score = score;
        this.content = content;
        this.date = date;
    }

    public Long getReviewId() { return reviewId; }
    public String getTargetUserId() { return targetUserId; }
    public String getWriterUserId() { return writerUserId; }
    public float getScore() { return score; }
    public String getContent() { return content; }
    public String getDate() { return date; }
}

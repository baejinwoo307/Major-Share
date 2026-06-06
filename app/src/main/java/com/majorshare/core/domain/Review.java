package com.majorshare.core.domain;

import com.majorshare.core.dto.ReviewDetailDTO;

public class Review {
    private Long reviewId;
    private int rating;
    private String content;
    private Transaction targetTransaction;
    private User reviewer;
    private User targetUser;

    public Review(Transaction targetTransaction, User reviewer, User targetUser, int rating, String content) {
        this.targetTransaction = targetTransaction;
        this.reviewer = reviewer;
        this.targetUser = targetUser;
        this.rating = rating;
        this.content = content;

        this.targetUser.updateScore(this.rating);
    }

    public ReviewDetailDTO getReviewDetailResponse() {
        return new ReviewDetailDTO(this.reviewId, this.rating, this.content);
    }

    public Long getReviewId() { return reviewId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public Transaction getTargetTransaction() { return targetTransaction; }
    public User getReviewer() { return reviewer; }
    public User getTargetUser() { return targetUser; }
}

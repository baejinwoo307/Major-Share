package com.majorshare.core.dto;

public class ReviewDetailDTO {
    private Long reviewId;
    private int rating;
    private String content;

    public ReviewDetailDTO(Long reviewId, int rating, String content) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.content = content;
    }

    public Long getReviewId() { return reviewId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
}
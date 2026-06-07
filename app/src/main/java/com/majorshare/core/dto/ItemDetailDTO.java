package com.majorshare.core.dto;

public class ItemDetailDTO {
    private Long itemId;
    private String title;
    private String category;
    private String transactionType;
    private String status;
    private String rentalEndDate;
    private int price;
    private String ownerId;

    public ItemDetailDTO(Long itemId, String title, String category, String transactionType, String status, String rentalEndDate, int price, String ownerId) {
        this.itemId = itemId;
        this.title = title;
        this.category = category;
        this.transactionType = transactionType;
        this.status = status;
        this.rentalEndDate = rentalEndDate;
        this.price = price;
        this.ownerId = ownerId;
    }

    public Long getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getTransactionType() { return transactionType; }
    public String getStatus() { return status; }
    public String getRentalEndDate() { return rentalEndDate; }
    public int getPrice() { return price; }
    public String getOwnerId() { return ownerId; }
}
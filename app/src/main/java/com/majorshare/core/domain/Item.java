package com.majorshare.core.domain;

import com.majorshare.core.dto.ItemDetailDTO;
import com.majorshare.core.controller.ItemRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Item {
    private Long itemId;
    private String title;
    private String category;
    private String transactionType;
    private String status;
    private String rentalEndDate;
    private int price;
    private int maxRentDays = 7;
    private User owner;
    private String imageBase64;

    public Item(String title, String category, String transactionType, int price, String rentalEndDate, User owner) {
        this.title = title;
        this.category = category;
        this.transactionType = transactionType != null ? transactionType.trim() : "";
        this.price = price;
        this.rentalEndDate = rentalEndDate;
        this.owner = owner;
        // 거래 타입에 따른 초기 상태 설정 보정
        if ("매매".equals(this.transactionType) || "판매".equals(this.transactionType)) {
            this.status = "판매중";
            this.transactionType = "매매"; // 타입 통일
        } else {
            this.status = "대여가능";
            this.transactionType = "대여";
        }
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public List<Item> searchItem(String title, String category, String status) {
        // 도메인 내부에서 Repository(Android Context 필요)를 직접 참조하는 구조는 분리되었습니다.
        // Controller나 Activity에서 ItemRepository를 통해 처리합니다.
        return Collections.emptyList();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeStatus(String newStatus) {
        this.status = newStatus;
    }

    public ItemDetailDTO getItemDetailResponse() {
        return new ItemDetailDTO(this.itemId, this.title, this.category, this.transactionType, this.status, this.rentalEndDate, this.price, this.owner.getUserId());
    }

    public Long getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getTransactionType() { return transactionType; }
    public String getStatus() { return status; }
    public String getRentalEndDate() { return rentalEndDate; }
    public int getPrice() { return price; }
    public int getMaxRentDays() { return maxRentDays; }
    public void setMaxRentDays(int days) { this.maxRentDays = days; }
    public User getOwner() { return owner; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}
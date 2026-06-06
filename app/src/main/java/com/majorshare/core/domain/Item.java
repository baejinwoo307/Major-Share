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
    private int maxRentDays;
    private int price;
    private User owner;

    public Item(String title, String category, String transactionType, int price, int maxRentDays, User owner) {
        this.title = title;
        this.category = category;
        this.transactionType = transactionType;
        this.price = price;
        this.maxRentDays = maxRentDays;
        this.owner = owner;
        // 거래 타입에 따른 초기 상태 설정 보정
        if ("매매".equals(transactionType) || "판매".equals(transactionType)) {
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
        return new ItemDetailDTO(this.itemId, this.title, this.category, this.transactionType, this.status, this.maxRentDays, this.price, this.owner.getUserId());
    }

    public Long getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getTransactionType() { return transactionType; }
    public String getStatus() { return status; }
    public int getMaxRentDays() { return maxRentDays; }
    public int getPrice() { return price; }
    public User getOwner() { return owner; }
}
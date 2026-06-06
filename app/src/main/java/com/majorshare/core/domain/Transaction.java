package com.majorshare.core.domain;

import com.majorshare.core.dto.TransactionDetailDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.majorshare.core.controller.TransactionRepository;

public class Transaction {
    private Long transactionId;
    private String stage;
    private String transactionType;
    private LocalDate transactionDate;
    private LocalDateTime returnDueDate;
    private User buyer;
    private Item subjectItem;

    public Transaction(Item subjectItem, User buyer) {
        this.subjectItem = subjectItem;
        this.buyer = buyer;
        this.transactionType = subjectItem.getTransactionType();
        this.stage = "승인대기";
    }

    public void approveRequest() {
        this.stage = "승인됨";
        this.transactionDate = LocalDate.now();
        this.subjectItem.changeStatus("거래중");
    }

    public void rejectRequest() {
        this.stage = "거절됨";
    }

    public boolean confirmPickup() {
        if (this.transactionType.equals("매매")) {
            this.stage = "거래완료";
            this.subjectItem.changeStatus("판매완료");
        } else if (this.transactionType.equals("대여")) {
            this.stage = "수령완료_대여중";
            this.returnDueDate = LocalDateTime.now().plusDays(this.subjectItem.getMaxRentDays());
        }
        return true;
    }

    public boolean confirmReturn() {
        if (this.transactionType.equals("대여")) {
            this.stage = "반납완료";
            this.subjectItem.changeStatus("대여가능");
            return true;
        }
        return false;
    }

    public void extendReturnDueDate(int days) {
        if (this.returnDueDate != null) {
            this.returnDueDate = this.returnDueDate.plusDays(days);
        }
    }

    public boolean checkExtensionAvailability(android.content.Context context) {
        List<Reservation> reservations = TransactionRepository.getInstance().getReservations(context);
        for (Reservation res : reservations) {
            if (res.getTargetItem().getItemId().equals(this.subjectItem.getItemId()) && res.getStatus().equals("대기")) {
                return false; // Block Extension
            }
        }
        return true;
    }

    public TransactionDetailDTO getTransactionDetailResponse() {
        return new TransactionDetailDTO(this.transactionId, this.stage, this.transactionType, this.transactionDate, this.returnDueDate);
    }

    public List<Transaction> getHistoryByUser(android.content.Context context, User user) {
        return TransactionRepository.getInstance().getTransactions(context).stream()
                .filter(t -> t.getBuyer().getUserId().equals(user.getUserId()) ||
                             t.getSubjectItem().getOwner().getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());
    }

    public void setTransactionId(Long id) { this.transactionId = id; }
    public void setStage(String stage) { this.stage = stage; }
    public void setTransactionDate(LocalDate date) { this.transactionDate = date; }
    public void setReturnDueDate(LocalDateTime time) { this.returnDueDate = time; }

    public Long getTransactionId() { return transactionId; }
    public String getStage() { return stage; }
    public String getTransactionType() { return transactionType; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public LocalDateTime getReturnDueDate() { return returnDueDate; }
    public User getBuyer() { return buyer; }
    public Item getSubjectItem() { return subjectItem; }
}
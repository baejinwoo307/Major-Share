package com.majorshare.core.domain;

import com.majorshare.core.dto.ReservationDetailDTO;
import java.time.LocalDateTime;

public class Reservation {
    private Long reservationId;
    private int queueOrder;
    private LocalDateTime requestTime;
    private String status;
    private Item targetItem;
    private User borrower;

    public Reservation(Item targetItem, User borrower) {
        this.targetItem = targetItem;
        this.borrower = borrower;
        this.requestTime = LocalDateTime.now();
        this.status = "대기";
        this.queueOrder = 1;
    }

    public void cancelReservation() {
        this.status = "취소";
    }

    public void processAutoSuccession() {
        this.status = "이행";
    }

    public ReservationDetailDTO getReservationDetailResponse() {
        return new ReservationDetailDTO(this.reservationId, this.queueOrder, this.requestTime, this.status);
    }

    public void setReservationId(Long id) { this.reservationId = id; }
    public void setStatus(String status) { this.status = status; }

    public Long getReservationId() { return reservationId; }
    public int getQueueOrder() { return queueOrder; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public String getStatus() { return status; }
    public Item getTargetItem() { return targetItem; }
    public User getBorrower() { return borrower; }
}
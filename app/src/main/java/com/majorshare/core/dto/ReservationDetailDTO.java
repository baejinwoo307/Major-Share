package com.majorshare.core.dto;

import java.time.LocalDateTime;

public class ReservationDetailDTO {
    private Long reservationId;
    private int queueOrder;
    private LocalDateTime requestTime;
    private String status;

    public ReservationDetailDTO(Long reservationId, int queueOrder, LocalDateTime requestTime, String status) {
        this.reservationId = reservationId;
        this.queueOrder = queueOrder;
        this.requestTime = requestTime;
        this.status = status;
    }
}

package com.majorshare.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionDetailDTO {
    private Long transactionId;
    private String stage;
    private String transactionType;
    private LocalDate transactionDate;
    private LocalDateTime returnDueDate;

    public TransactionDetailDTO(Long transactionId, String stage, String transactionType, LocalDate transactionDate, LocalDateTime returnDueDate) {
        this.transactionId = transactionId;
        this.stage = stage;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.returnDueDate = returnDueDate;
    }

    public Long getTransactionId() { return transactionId; }
    public String getStage() { return stage; }
    public String getTransactionType() { return transactionType; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public LocalDateTime getReturnDueDate() { return returnDueDate; }
}

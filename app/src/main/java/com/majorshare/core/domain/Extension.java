package com.majorshare.core.domain;

import com.majorshare.core.dto.ExtensionDetailDTO;

public class Extension {
    private Long extensionId;
    private int extendDays;
    private String reason;
    private String status;
    private Transaction targetTransaction;

    public Extension(Transaction targetTransaction, int extendDays, String reason) {
        this.targetTransaction = targetTransaction;
        this.extendDays = extendDays;
        this.reason = reason;
        this.status = "대기";
    }

    public void approveExtension() {
        this.status = "승인";
    }

    public void rejectExtension() {
        this.status = "거절";
    }

    public ExtensionDetailDTO getExtensionDetailResponse() {
        return new ExtensionDetailDTO(this.extensionId, this.extendDays, this.reason, this.status);
    }

    public Long getExtensionId() { return extensionId; }
    public int getExtendDays() { return extendDays; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public Transaction getTargetTransaction() { return targetTransaction; }
}

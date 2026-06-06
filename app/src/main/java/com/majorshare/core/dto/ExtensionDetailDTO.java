package com.majorshare.core.dto;

public class ExtensionDetailDTO {
    private Long extensionId;
    private int extendDays;
    private String reason;
    private String status;

    public ExtensionDetailDTO(Long extensionId, int extendDays, String reason, String status) {
        this.extensionId = extensionId;
        this.extendDays = extendDays;
        this.reason = reason;
        this.status = status;
    }
}
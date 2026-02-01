package com.brandkit.catalog.dto.admin;

import com.brandkit.catalog.dto.ProductListResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Discount Audit Response DTO
 * FRD-002 Sub-Prompt 7: Discount audit trail
 */
public class DiscountAuditResponse {
    private String status;
    private DiscountAuditData data;

    public static class DiscountAuditData {
        private List<AuditEntry> auditLog;
        private ProductListResponse.PaginationInfo pagination;

        public DiscountAuditData() {}

        public List<AuditEntry> getAuditLog() {
            return this.auditLog;
        }
        public void setAuditLog(List<AuditEntry> auditLog) {
            this.auditLog = auditLog;
        }
        public ProductListResponse.PaginationInfo getPagination() {
            return this.pagination;
        }
        public void setPagination(ProductListResponse.PaginationInfo pagination) {
            this.pagination = pagination;
        }

        public static DiscountAuditDataBuilder builder() {
            return new DiscountAuditDataBuilder();
        }

        public static class DiscountAuditDataBuilder {
            private List<AuditEntry> auditLog;
            private ProductListResponse.PaginationInfo pagination;

            public DiscountAuditDataBuilder auditLog(List<AuditEntry> auditLog) {
                this.auditLog = auditLog;
                return this;
            }
            public DiscountAuditDataBuilder pagination(ProductListResponse.PaginationInfo pagination) {
                this.pagination = pagination;
                return this;
            }

            public DiscountAuditData build() {
                DiscountAuditData instance = new DiscountAuditData();
                instance.auditLog = this.auditLog;
                instance.pagination = this.pagination;
                return instance;
            }
        }
    }

    public static class AuditEntry {
        private UUID id;
        private UUID discountId;
        private String action;
        private String performedByName;
        private String performedByRole;
        private Map<String, Object> oldValue;
        private Map<String, Object> newValue;
        private String reason;
        private String ipAddress;
        private ZonedDateTime createdAt;

        public AuditEntry() {}

        public UUID getId() {
            return this.id;
        }
        public void setId(UUID id) {
            this.id = id;
        }
        public UUID getDiscountId() {
            return this.discountId;
        }
        public void setDiscountId(UUID discountId) {
            this.discountId = discountId;
        }
        public String getAction() {
            return this.action;
        }
        public void setAction(String action) {
            this.action = action;
        }
        public String getPerformedByName() {
            return this.performedByName;
        }
        public void setPerformedByName(String performedByName) {
            this.performedByName = performedByName;
        }
        public String getPerformedByRole() {
            return this.performedByRole;
        }
        public void setPerformedByRole(String performedByRole) {
            this.performedByRole = performedByRole;
        }
        public Map<String, Object> getOldValue() {
            return this.oldValue;
        }
        public void setOldValue(Map<String, Object> oldValue) {
            this.oldValue = oldValue;
        }
        public Map<String, Object> getNewValue() {
            return this.newValue;
        }
        public void setNewValue(Map<String, Object> newValue) {
            this.newValue = newValue;
        }
        public String getReason() {
            return this.reason;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }
        public String getIpAddress() {
            return this.ipAddress;
        }
        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
        public ZonedDateTime getCreatedAt() {
            return this.createdAt;
        }
        public void setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public static AuditEntryBuilder builder() {
            return new AuditEntryBuilder();
        }

        public static class AuditEntryBuilder {
            private UUID id;
            private UUID discountId;
            private String action;
            private String performedByName;
            private String performedByRole;
            private Map<String, Object> oldValue;
            private Map<String, Object> newValue;
            private String reason;
            private String ipAddress;
            private ZonedDateTime createdAt;

            public AuditEntryBuilder id(UUID id) {
                this.id = id;
                return this;
            }
            public AuditEntryBuilder discountId(UUID discountId) {
                this.discountId = discountId;
                return this;
            }
            public AuditEntryBuilder action(String action) {
                this.action = action;
                return this;
            }
            public AuditEntryBuilder performedByName(String performedByName) {
                this.performedByName = performedByName;
                return this;
            }
            public AuditEntryBuilder performedByRole(String performedByRole) {
                this.performedByRole = performedByRole;
                return this;
            }
            public AuditEntryBuilder oldValue(Map<String, Object> oldValue) {
                this.oldValue = oldValue;
                return this;
            }
            public AuditEntryBuilder newValue(Map<String, Object> newValue) {
                this.newValue = newValue;
                return this;
            }
            public AuditEntryBuilder reason(String reason) {
                this.reason = reason;
                return this;
            }
            public AuditEntryBuilder ipAddress(String ipAddress) {
                this.ipAddress = ipAddress;
                return this;
            }
            public AuditEntryBuilder createdAt(ZonedDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public AuditEntry build() {
                AuditEntry instance = new AuditEntry();
                instance.id = this.id;
                instance.discountId = this.discountId;
                instance.action = this.action;
                instance.performedByName = this.performedByName;
                instance.performedByRole = this.performedByRole;
                instance.oldValue = this.oldValue;
                instance.newValue = this.newValue;
                instance.reason = this.reason;
                instance.ipAddress = this.ipAddress;
                instance.createdAt = this.createdAt;
                return instance;
            }
        }
    }

    public DiscountAuditResponse() {}

    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public DiscountAuditData getData() {
        return this.data;
    }
    public void setData(DiscountAuditData data) {
        this.data = data;
    }

    public static DiscountAuditResponseBuilder builder() {
        return new DiscountAuditResponseBuilder();
    }

    public static class DiscountAuditResponseBuilder {
        private String status;
        private DiscountAuditData data;

        public DiscountAuditResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        public DiscountAuditResponseBuilder data(DiscountAuditData data) {
            this.data = data;
            return this;
        }

        public DiscountAuditResponse build() {
            DiscountAuditResponse instance = new DiscountAuditResponse();
            instance.status = this.status;
            instance.data = this.data;
            return instance;
        }
    }
}

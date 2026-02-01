package com.brandkit.partner.dto;

import java.util.List;

/**
 * Notification Response - FRD-005 FR-53
 * Partner notifications
 */
public class NotificationResponse {

    private List<NotificationDto> notifications;
    private long unreadCount;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static class NotificationDto {
        private String id;
        private String type;
        private String title;
        private String message;
        private String orderId;
        private String orderNumber;
        private boolean isRead;
        private String createdAt;

        public NotificationDto() {}

        public String getId() {
            return this.id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getType() {
            return this.type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getTitle() {
            return this.title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getMessage() {
            return this.message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String getOrderId() {
            return this.orderId;
        }
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        public String getOrderNumber() {
            return this.orderNumber;
        }
        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }
        public boolean getIsRead() {
            return this.isRead;
        }
        public void setIsRead(boolean isRead) {
            this.isRead = isRead;
        }
        public String getCreatedAt() {
            return this.createdAt;
        }
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public static NotificationDtoBuilder builder() {
            return new NotificationDtoBuilder();
        }

        public static class NotificationDtoBuilder {
            private String id;
            private String type;
            private String title;
            private String message;
            private String orderId;
            private String orderNumber;
            private boolean isRead;
            private String createdAt;

            public NotificationDtoBuilder id(String id) {
                this.id = id;
                return this;
            }
            public NotificationDtoBuilder type(String type) {
                this.type = type;
                return this;
            }
            public NotificationDtoBuilder title(String title) {
                this.title = title;
                return this;
            }
            public NotificationDtoBuilder message(String message) {
                this.message = message;
                return this;
            }
            public NotificationDtoBuilder orderId(String orderId) {
                this.orderId = orderId;
                return this;
            }
            public NotificationDtoBuilder orderNumber(String orderNumber) {
                this.orderNumber = orderNumber;
                return this;
            }
            public NotificationDtoBuilder isRead(boolean isRead) {
                this.isRead = isRead;
                return this;
            }
            public NotificationDtoBuilder createdAt(String createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public NotificationDto build() {
                NotificationDto instance = new NotificationDto();
                instance.id = this.id;
                instance.type = this.type;
                instance.title = this.title;
                instance.message = this.message;
                instance.orderId = this.orderId;
                instance.orderNumber = this.orderNumber;
                instance.isRead = this.isRead;
                instance.createdAt = this.createdAt;
                return instance;
            }
        }
    }

    public NotificationResponse() {}

    public List<NotificationDto> getNotifications() {
        return this.notifications;
    }
    public void setNotifications(List<NotificationDto> notifications) {
        this.notifications = notifications;
    }
    public long getUnreadCount() {
        return this.unreadCount;
    }
    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
    public int getPage() {
        return this.page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public long getTotalElements() {
        return this.totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    public int getTotalPages() {
        return this.totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public static NotificationResponseBuilder builder() {
        return new NotificationResponseBuilder();
    }

    public static class NotificationResponseBuilder {
        private List<NotificationDto> notifications;
        private long unreadCount;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public NotificationResponseBuilder notifications(List<NotificationDto> notifications) {
            this.notifications = notifications;
            return this;
        }
        public NotificationResponseBuilder unreadCount(long unreadCount) {
            this.unreadCount = unreadCount;
            return this;
        }
        public NotificationResponseBuilder page(int page) {
            this.page = page;
            return this;
        }
        public NotificationResponseBuilder size(int size) {
            this.size = size;
            return this;
        }
        public NotificationResponseBuilder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }
        public NotificationResponseBuilder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public NotificationResponse build() {
            NotificationResponse instance = new NotificationResponse();
            instance.notifications = this.notifications;
            instance.unreadCount = this.unreadCount;
            instance.page = this.page;
            instance.size = this.size;
            instance.totalElements = this.totalElements;
            instance.totalPages = this.totalPages;
            return instance;
        }
    }
}

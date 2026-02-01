package com.brandkit.customization.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Logo File Entity
 * FRD-003: Stores uploaded logo files
 * 
 * Tracks logo file uploads with validation status.
 */
@Entity
@Table(name = "logo_files")
public class LogoFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // File information
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType; // image/png, image/jpeg, image/svg+xml

    @Column(name = "file_url", nullable = false, length = 2048)
    private String fileUrl; // S3 URL or storage path

    // Image metadata
    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    // Security
    @Column(name = "is_validated")
    private Boolean isValidated = false; // Server-side validation flag

    // Metadata
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // For temporary files

    public UUID getId() {
        return this.id;
    }
    public UUID getUserId() {
        return this.userId;
    }
    public String getFileName() {
        return this.fileName;
    }
    public Long getFileSize() {
        return this.fileSize;
    }
    public String getFileType() {
        return this.fileType;
    }
    public String getFileUrl() {
        return this.fileUrl;
    }
    public Integer getWidth() {
        return this.width;
    }
    public Integer getHeight() {
        return this.height;
    }
    public Boolean getIsValidated() {
        return this.isValidated;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public LogoFile() {
    }
    public LogoFile(UUID id, UUID userId, String fileName, Long fileSize, String fileType, String fileUrl, Integer width, Integer height, Boolean isValidated, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.width = width;
        this.height = height;
        this.isValidated = isValidated;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
    public static LogoFileBuilder builder() {
        return new LogoFileBuilder();
    }

    public static class LogoFileBuilder {
        private UUID id;
        private UUID userId;
        private String fileName;
        private Long fileSize;
        private String fileType;
        private String fileUrl;
        private Integer width;
        private Integer height;
        private Boolean isValidated = false;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;

        LogoFileBuilder() {
        }

        public LogoFileBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public LogoFileBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public LogoFileBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public LogoFileBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public LogoFileBuilder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public LogoFileBuilder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public LogoFileBuilder width(Integer width) {
            this.width = width;
            return this;
        }

        public LogoFileBuilder height(Integer height) {
            this.height = height;
            return this;
        }

        public LogoFileBuilder isValidated(Boolean isValidated) {
            this.isValidated = isValidated;
            return this;
        }

        public LogoFileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LogoFileBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public LogoFile build() {
            LogoFile instance = new LogoFile();
            instance.id = this.id;
            instance.userId = this.userId;
            instance.fileName = this.fileName;
            instance.fileSize = this.fileSize;
            instance.fileType = this.fileType;
            instance.fileUrl = this.fileUrl;
            instance.width = this.width;
            instance.height = this.height;
            instance.isValidated = this.isValidated;
            instance.createdAt = this.createdAt;
            instance.expiresAt = this.expiresAt;
            return instance;
        }
    }
}

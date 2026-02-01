package com.brandkit.partner.entity;

import com.brandkit.auth.entity.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Platform Setting Entity
 * Platform-wide settings including discount limits
 */
@Entity
@Table(name = "platform_settings")
public class PlatformSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, columnDefinition = "TEXT")
    private String settingValue;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Integer getIntValue() {
        return Integer.parseInt(settingValue);
    }

    public Boolean getBooleanValue() {
        return Boolean.parseBoolean(settingValue);
    }

    public Double getDoubleValue() {
        return Double.parseDouble(settingValue);
    }

    public UUID getId() {
        return this.id;
    }
    public String getSettingKey() {
        return this.settingKey;
    }
    public String getSettingValue() {
        return this.settingValue;
    }
    public String getDescription() {
        return this.description;
    }
    public User getUpdatedBy() {
        return this.updatedBy;
    }
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }
    public OffsetDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public PlatformSetting() {
    }
    public PlatformSetting(UUID id, String settingKey, String settingValue, String description, User updatedBy, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static PlatformSettingBuilder builder() {
        return new PlatformSettingBuilder();
    }

    public static class PlatformSettingBuilder {
        private UUID id;
        private String settingKey;
        private String settingValue;
        private String description;
        private User updatedBy;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        PlatformSettingBuilder() {
        }

        public PlatformSettingBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public PlatformSettingBuilder settingKey(String settingKey) {
            this.settingKey = settingKey;
            return this;
        }

        public PlatformSettingBuilder settingValue(String settingValue) {
            this.settingValue = settingValue;
            return this;
        }

        public PlatformSettingBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PlatformSettingBuilder updatedBy(User updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public PlatformSettingBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PlatformSettingBuilder updatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PlatformSetting build() {
            PlatformSetting instance = new PlatformSetting();
            instance.id = this.id;
            instance.settingKey = this.settingKey;
            instance.settingValue = this.settingValue;
            instance.description = this.description;
            instance.updatedBy = this.updatedBy;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}

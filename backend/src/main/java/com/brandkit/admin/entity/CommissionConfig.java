package com.brandkit.admin.entity;

import com.brandkit.auth.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Commission Configuration Entity
 * Manages commission configuration sets with tiered rates
 * 
 * FRD-006 FR-72: Commission Configuration
 */
@Entity
@Table(name = "commission_configs")
public class CommissionConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "config", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CommissionTier> tiers = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    /**
     * Add a tier to this configuration
     */
    public void addTier(CommissionTier tier) {
        tiers.add(tier);
        tier.setConfig(this);
    }

    /**
     * Remove a tier from this configuration
     */
    public void removeTier(CommissionTier tier) {
        tiers.remove(tier);
        tier.setConfig(null);
    }

    /**
     * Clear all tiers
     */
    public void clearTiers() {
        tiers.forEach(tier -> tier.setConfig(null));
        tiers.clear();
    }

    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public Boolean getIsDefault() {
        return this.isDefault;
    }
    public Boolean getIsActive() {
        return this.isActive;
    }
    public User getCreatedBy() {
        return this.createdBy;
    }
    public List<CommissionTier> getTiers() {
        return this.tiers;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    public void setTiers(List<CommissionTier> tiers) {
        this.tiers = tiers;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public CommissionConfig() {
    }
    public CommissionConfig(UUID id, String name, String description, Boolean isDefault, Boolean isActive, User createdBy, List<CommissionTier> tiers, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isDefault = isDefault;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.tiers = tiers;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static CommissionConfigBuilder builder() {
        return new CommissionConfigBuilder();
    }

    public static class CommissionConfigBuilder {
        private UUID id;
        private String name;
        private String description;
        private Boolean isDefault = false;
        private Boolean isActive = true;
        private User createdBy;
        private List<CommissionTier> tiers = new ArrayList<>();
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;

        CommissionConfigBuilder() {
        }

        public CommissionConfigBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CommissionConfigBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CommissionConfigBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CommissionConfigBuilder isDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public CommissionConfigBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CommissionConfigBuilder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CommissionConfigBuilder tiers(List<CommissionTier> tiers) {
            this.tiers = tiers;
            return this;
        }

        public CommissionConfigBuilder createdAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommissionConfigBuilder updatedAt(ZonedDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CommissionConfig build() {
            CommissionConfig instance = new CommissionConfig();
            instance.id = this.id;
            instance.name = this.name;
            instance.description = this.description;
            instance.isDefault = this.isDefault;
            instance.isActive = this.isActive;
            instance.createdBy = this.createdBy;
            instance.tiers = this.tiers;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}

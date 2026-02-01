package com.brandkit.partner.entity;

import com.brandkit.catalog.entity.Partner;
import com.brandkit.order.entity.Order;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Proof Image Entity - FRD-005 FR-58
 * Sample product images uploaded by partners
 */
@Entity
@Table(name = "proof_images", indexes = {
    @Index(name = "idx_proof_images_order_id", columnList = "order_id"),
    @Index(name = "idx_proof_images_partner_id", columnList = "partner_id")
})
public class ProofImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "s3_key", length = 500)
    private String s3Key;

    @Column(name = "caption", length = 200)
    private String caption;

    @Column(name = "file_size_bytes")
    private Integer fileSizeBytes;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return this.id;
    }
    public Order getOrder() {
        return this.order;
    }
    public Partner getPartner() {
        return this.partner;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public String getS3Key() {
        return this.s3Key;
    }
    public String getCaption() {
        return this.caption;
    }
    public Integer getFileSizeBytes() {
        return this.fileSizeBytes;
    }
    public Integer getDisplayOrder() {
        return this.displayOrder;
    }
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public void setFileSizeBytes(Integer fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public ProofImage() {
    }
    public ProofImage(UUID id, Order order, Partner partner, String imageUrl, String s3Key, String caption, Integer fileSizeBytes, Integer displayOrder, OffsetDateTime createdAt) {
        this.id = id;
        this.order = order;
        this.partner = partner;
        this.imageUrl = imageUrl;
        this.s3Key = s3Key;
        this.caption = caption;
        this.fileSizeBytes = fileSizeBytes;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }
    public static ProofImageBuilder builder() {
        return new ProofImageBuilder();
    }

    public static class ProofImageBuilder {
        private UUID id;
        private Order order;
        private Partner partner;
        private String imageUrl;
        private String s3Key;
        private String caption;
        private Integer fileSizeBytes;
        private Integer displayOrder = 0;
        private OffsetDateTime createdAt;

        ProofImageBuilder() {
        }

        public ProofImageBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ProofImageBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public ProofImageBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public ProofImageBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ProofImageBuilder s3Key(String s3Key) {
            this.s3Key = s3Key;
            return this;
        }

        public ProofImageBuilder caption(String caption) {
            this.caption = caption;
            return this;
        }

        public ProofImageBuilder fileSizeBytes(Integer fileSizeBytes) {
            this.fileSizeBytes = fileSizeBytes;
            return this;
        }

        public ProofImageBuilder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public ProofImageBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ProofImage build() {
            ProofImage instance = new ProofImage();
            instance.id = this.id;
            instance.order = this.order;
            instance.partner = this.partner;
            instance.imageUrl = this.imageUrl;
            instance.s3Key = this.s3Key;
            instance.caption = this.caption;
            instance.fileSizeBytes = this.fileSizeBytes;
            instance.displayOrder = this.displayOrder;
            instance.createdAt = this.createdAt;
            return instance;
        }
    }
}

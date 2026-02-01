package com.brandkit.partner.entity;

import com.brandkit.catalog.entity.Partner;
import com.brandkit.order.entity.Order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Shipment Entity - FRD-005 FR-59
 * Shipment details and tracking
 */
@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_shipments_order_id", columnList = "order_id"),
    @Index(name = "idx_shipments_partner_id", columnList = "partner_id"),
    @Index(name = "idx_shipments_tracking_id", columnList = "tracking_id")
})
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(name = "courier_name", nullable = false, length = 100)
    private String courierName;

    @Column(name = "tracking_id", nullable = false, length = 100)
    private String trackingId;

    @Column(name = "tracking_url")
    private String trackingUrl;

    @Column(name = "ship_date", nullable = false)
    private LocalDate shipDate;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "num_packages")
    private Integer numPackages = 1;

    @Column(name = "notes")
    private String notes;

    @Column(name = "webhook_enabled")
    private Boolean webhookEnabled = false;

    @Column(name = "last_tracking_update")
    private OffsetDateTime lastTrackingUpdate;

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

    /**
     * Generate tracking URL based on courier
     */
    public String generateTrackingUrl() {
        if (courierName == null || trackingId == null) {
            return null;
        }
        
        return switch (courierName.toUpperCase()) {
            case "DELHIVERY" -> "https://www.delhivery.com/track/package/" + trackingId;
            case "BLUEDART" -> "https://www.bluedart.com/tracking/" + trackingId;
            case "DTDC" -> "https://www.dtdc.in/tracking/" + trackingId;
            default -> null;
        };
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
    public String getCourierName() {
        return this.courierName;
    }
    public String getTrackingId() {
        return this.trackingId;
    }
    public String getTrackingUrl() {
        return this.trackingUrl;
    }
    public LocalDate getShipDate() {
        return this.shipDate;
    }
    public BigDecimal getWeightKg() {
        return this.weightKg;
    }
    public Integer getNumPackages() {
        return this.numPackages;
    }
    public String getNotes() {
        return this.notes;
    }
    public Boolean getWebhookEnabled() {
        return this.webhookEnabled;
    }
    public OffsetDateTime getLastTrackingUpdate() {
        return this.lastTrackingUpdate;
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
    public void setOrder(Order order) {
        this.order = order;
    }
    public void setPartner(Partner partner) {
        this.partner = partner;
    }
    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }
    public void setShipDate(LocalDate shipDate) {
        this.shipDate = shipDate;
    }
    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }
    public void setNumPackages(Integer numPackages) {
        this.numPackages = numPackages;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public void setWebhookEnabled(Boolean webhookEnabled) {
        this.webhookEnabled = webhookEnabled;
    }
    public void setLastTrackingUpdate(OffsetDateTime lastTrackingUpdate) {
        this.lastTrackingUpdate = lastTrackingUpdate;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Shipment() {
    }
    public Shipment(UUID id, Order order, Partner partner, String courierName, String trackingId, String trackingUrl, LocalDate shipDate, BigDecimal weightKg, Integer numPackages, String notes, Boolean webhookEnabled, OffsetDateTime lastTrackingUpdate, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.order = order;
        this.partner = partner;
        this.courierName = courierName;
        this.trackingId = trackingId;
        this.trackingUrl = trackingUrl;
        this.shipDate = shipDate;
        this.weightKg = weightKg;
        this.numPackages = numPackages;
        this.notes = notes;
        this.webhookEnabled = webhookEnabled;
        this.lastTrackingUpdate = lastTrackingUpdate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public static ShipmentBuilder builder() {
        return new ShipmentBuilder();
    }

    public static class ShipmentBuilder {
        private UUID id;
        private Order order;
        private Partner partner;
        private String courierName;
        private String trackingId;
        private String trackingUrl;
        private LocalDate shipDate;
        private BigDecimal weightKg;
        private Integer numPackages = 1;
        private String notes;
        private Boolean webhookEnabled = false;
        private OffsetDateTime lastTrackingUpdate;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        ShipmentBuilder() {
        }

        public ShipmentBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ShipmentBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public ShipmentBuilder partner(Partner partner) {
            this.partner = partner;
            return this;
        }

        public ShipmentBuilder courierName(String courierName) {
            this.courierName = courierName;
            return this;
        }

        public ShipmentBuilder trackingId(String trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public ShipmentBuilder trackingUrl(String trackingUrl) {
            this.trackingUrl = trackingUrl;
            return this;
        }

        public ShipmentBuilder shipDate(LocalDate shipDate) {
            this.shipDate = shipDate;
            return this;
        }

        public ShipmentBuilder weightKg(BigDecimal weightKg) {
            this.weightKg = weightKg;
            return this;
        }

        public ShipmentBuilder numPackages(Integer numPackages) {
            this.numPackages = numPackages;
            return this;
        }

        public ShipmentBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public ShipmentBuilder webhookEnabled(Boolean webhookEnabled) {
            this.webhookEnabled = webhookEnabled;
            return this;
        }

        public ShipmentBuilder lastTrackingUpdate(OffsetDateTime lastTrackingUpdate) {
            this.lastTrackingUpdate = lastTrackingUpdate;
            return this;
        }

        public ShipmentBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ShipmentBuilder updatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Shipment build() {
            Shipment instance = new Shipment();
            instance.id = this.id;
            instance.order = this.order;
            instance.partner = this.partner;
            instance.courierName = this.courierName;
            instance.trackingId = this.trackingId;
            instance.trackingUrl = this.trackingUrl;
            instance.shipDate = this.shipDate;
            instance.weightKg = this.weightKg;
            instance.numPackages = this.numPackages;
            instance.notes = this.notes;
            instance.webhookEnabled = this.webhookEnabled;
            instance.lastTrackingUpdate = this.lastTrackingUpdate;
            instance.createdAt = this.createdAt;
            instance.updatedAt = this.updatedAt;
            return instance;
        }
    }
}

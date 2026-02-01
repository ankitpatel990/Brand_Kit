package com.brandkit.partner.repository;

import com.brandkit.partner.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Shipment entity - FRD-005 FR-59
 */
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    /**
     * Find shipment by order ID
     */
    Optional<Shipment> findByOrderId(UUID orderId);

    /**
     * Find shipments by partner ID
     */
    Page<Shipment> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId, Pageable pageable);

    /**
     * Find shipment by tracking ID
     */
    Optional<Shipment> findByTrackingId(String trackingId);

    /**
     * Check if shipment exists for order
     */
    boolean existsByOrderId(UUID orderId);
}

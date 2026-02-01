package com.brandkit.partner.repository;

import com.brandkit.partner.entity.ProofImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for ProofImage entity - FRD-005 FR-58
 */
@Repository
public interface ProofImageRepository extends JpaRepository<ProofImage, UUID> {

    /**
     * Find proof images by order ID
     */
    List<ProofImage> findByOrderIdOrderByDisplayOrderAsc(UUID orderId);

    /**
     * Find proof images by partner ID
     */
    List<ProofImage> findByPartnerIdOrderByCreatedAtDesc(UUID partnerId);

    /**
     * Count proof images by order ID
     */
    long countByOrderId(UUID orderId);

    /**
     * Delete all proof images by order ID
     */
    void deleteByOrderId(UUID orderId);
}

package com.brandkit.customization.repository;

import com.brandkit.customization.entity.Customization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Customization Repository
 * FRD-003: Completed customizations
 */
@Repository
public interface CustomizationRepository extends JpaRepository<Customization, UUID> {

    /**
     * Find all customizations for a user
     */
    List<Customization> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find customization by ID and user ID (for security)
     */
    java.util.Optional<Customization> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find customizations by product ID
     */
    List<Customization> findByProductId(UUID productId);

    /**
     * Find customizations by status
     */
    List<Customization> findByStatus(String status);
}

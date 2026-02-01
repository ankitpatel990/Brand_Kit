package com.brandkit.customization.repository;

import com.brandkit.customization.entity.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Bundle Repository
 * FRD-003 Sub-Prompt 6: Bundle Builder Workflow
 */
@Repository
public interface BundleRepository extends JpaRepository<Bundle, UUID> {

    /**
     * Find all bundles for a user
     */
    List<Bundle> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find bundle by ID and user ID (for security)
     */
    Optional<Bundle> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find bundles by status
     */
    List<Bundle> findByUserIdAndStatus(UUID userId, String status);
}

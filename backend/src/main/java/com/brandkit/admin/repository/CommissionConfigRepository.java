package com.brandkit.admin.repository;

import com.brandkit.admin.entity.CommissionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for CommissionConfig entity
 * 
 * FRD-006 FR-72: Commission Configuration
 */
@Repository
public interface CommissionConfigRepository extends JpaRepository<CommissionConfig, UUID> {

    /**
     * Find the default commission config
     */
    Optional<CommissionConfig> findByIsDefaultTrueAndIsActiveTrue();

    /**
     * Find all active configs
     */
    List<CommissionConfig> findByIsActiveTrueOrderByCreatedAtDesc();

    /**
     * Check if default config exists
     */
    boolean existsByIsDefaultTrue();

    /**
     * Find config with tiers (eager fetch)
     */
    @Query("SELECT cc FROM CommissionConfig cc LEFT JOIN FETCH cc.tiers WHERE cc.id = :id")
    Optional<CommissionConfig> findByIdWithTiers(UUID id);

    /**
     * Find default config with tiers
     */
    @Query("SELECT cc FROM CommissionConfig cc LEFT JOIN FETCH cc.tiers WHERE cc.isDefault = true AND cc.isActive = true")
    Optional<CommissionConfig> findDefaultConfigWithTiers();
}

package com.brandkit.customization.repository;

import com.brandkit.customization.entity.CustomizationDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Customization Draft Repository
 * FRD-003 Sub-Prompt 7: Draft Customization Save/Load
 */
@Repository
public interface CustomizationDraftRepository extends JpaRepository<CustomizationDraft, UUID> {

    /**
     * Find all drafts for a user
     */
    List<CustomizationDraft> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find draft by ID and user ID (for security)
     */
    Optional<CustomizationDraft> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find expired drafts
     */
    @Query("SELECT d FROM CustomizationDraft d WHERE d.expiresAt < :now")
    List<CustomizationDraft> findExpiredDrafts(@Param("now") LocalDateTime now);

    /**
     * Delete expired drafts
     */
    @Query("DELETE FROM CustomizationDraft d WHERE d.expiresAt < :now")
    void deleteExpiredDrafts(@Param("now") LocalDateTime now);
}

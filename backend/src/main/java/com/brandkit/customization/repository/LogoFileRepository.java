package com.brandkit.customization.repository;

import com.brandkit.customization.entity.LogoFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Logo File Repository
 * FRD-003: Logo file storage
 */
@Repository
public interface LogoFileRepository extends JpaRepository<LogoFile, UUID> {

    /**
     * Find logo file by ID and user ID (for security)
     */
    Optional<LogoFile> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find all logo files for a user
     */
    List<LogoFile> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find expired logo files
     */
    @Query("SELECT l FROM LogoFile l WHERE l.expiresAt IS NOT NULL AND l.expiresAt < :now")
    List<LogoFile> findExpiredFiles(@Param("now") LocalDateTime now);
}

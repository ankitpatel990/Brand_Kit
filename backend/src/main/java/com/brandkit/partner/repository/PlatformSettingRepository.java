package com.brandkit.partner.repository;

import com.brandkit.partner.entity.PlatformSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PlatformSetting entity
 */
@Repository
public interface PlatformSettingRepository extends JpaRepository<PlatformSetting, UUID> {

    /**
     * Find setting by key
     */
    Optional<PlatformSetting> findBySettingKey(String settingKey);
}

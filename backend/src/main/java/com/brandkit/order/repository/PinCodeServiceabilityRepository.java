package com.brandkit.order.repository;

import com.brandkit.order.entity.PinCodeServiceability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PinCodeServiceability entity - FRD-004 BR-37
 */
@Repository
public interface PinCodeServiceabilityRepository extends JpaRepository<PinCodeServiceability, String> {

    /**
     * Find by PIN code
     */
    Optional<PinCodeServiceability> findByPinCode(String pinCode);

    /**
     * Find serviceable PIN codes by city
     */
    List<PinCodeServiceability> findByCityAndIsServiceableTrue(String city);

    /**
     * Find serviceable PIN codes by state
     */
    List<PinCodeServiceability> findByStateAndIsServiceableTrue(String state);

    /**
     * Check if PIN code is serviceable
     */
    boolean existsByPinCodeAndIsServiceableTrue(String pinCode);

    /**
     * Check if express delivery is available for PIN code
     */
    boolean existsByPinCodeAndExpressAvailableTrue(String pinCode);
}

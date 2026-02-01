package com.brandkit.order.repository;

import com.brandkit.order.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Address entity - FRD-004 FR-41
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    /**
     * Find all addresses for a user
     */
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(UUID userId);

    /**
     * Find default address for a user
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(UUID userId);

    /**
     * Find address by ID and user ID (security check)
     */
    Optional<Address> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Count addresses for a user
     */
    long countByUserId(UUID userId);

    /**
     * Clear default flag for all user addresses
     */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId AND a.isDefault = true")
    void clearDefaultAddresses(@Param("userId") UUID userId);

    /**
     * Delete address by ID and user ID (security check)
     */
    @Modifying
    @Query("DELETE FROM Address a WHERE a.id = :addressId AND a.user.id = :userId")
    int deleteByIdAndUserId(@Param("addressId") UUID addressId, @Param("userId") UUID userId);

    /**
     * Find addresses by PIN code
     */
    List<Address> findByPinCode(String pinCode);
}

package com.brandkit.partner.repository;

import com.brandkit.partner.entity.SettlementOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for SettlementOrder entity - FRD-005 FR-61
 */
@Repository
public interface SettlementOrderRepository extends JpaRepository<SettlementOrder, UUID> {

    /**
     * Find orders by settlement ID
     */
    Page<SettlementOrder> findBySettlementIdOrderByCreatedAtDesc(UUID settlementId, Pageable pageable);

    /**
     * Find all orders by settlement ID
     */
    List<SettlementOrder> findBySettlementId(UUID settlementId);

    /**
     * Check if order is already in a settlement
     */
    boolean existsByOrderId(UUID orderId);
}

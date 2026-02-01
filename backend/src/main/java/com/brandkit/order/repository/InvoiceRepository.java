package com.brandkit.order.repository;

import com.brandkit.order.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Invoice entity - FRD-004 FR-44
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    /**
     * Find invoice by order ID
     */
    Optional<Invoice> findByOrderId(UUID orderId);

    /**
     * Find invoice by invoice number
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Check if invoice exists for order
     */
    boolean existsByOrderId(UUID orderId);
}

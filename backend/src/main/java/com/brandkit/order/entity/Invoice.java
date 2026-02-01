package com.brandkit.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Invoice entity - FRD-004 FR-44
 * GST-compliant invoice records
 */
@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoices_order_id", columnList = "order_id"),
    @Index(name = "idx_invoices_invoice_number", columnList = "invoice_number"),
    @Index(name = "idx_invoices_created_at", columnList = "created_at")
})
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "invoice_number", unique = true, length = 20)
    private String invoiceNumber; // Format: INV-YYYY-XXXX

    // BrandKit Company Details
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName = "BrandKit Pvt. Ltd.";

    @Column(name = "company_address", nullable = false, columnDefinition = "TEXT")
    private String companyAddress;

    @Column(name = "company_gstin", nullable = false, length = 20)
    private String companyGstin;

    // Client Details
    @NotBlank
    @Column(name = "client_name", nullable = false, length = 200)
    private String clientName;

    @Column(name = "client_address", nullable = false, columnDefinition = "TEXT")
    private String clientAddress;

    @Column(name = "client_gstin", length = 20)
    private String clientGstin;

    @Column(name = "client_phone", length = 15)
    private String clientPhone;

    // Invoice Details
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // Pricing
    @DecimalMin(value = "0", message = "Subtotal must be non-negative")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @DecimalMin(value = "0", message = "Original subtotal must be non-negative")
    @Column(name = "original_subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalSubtotal;

    @Column(name = "total_discount", precision = 12, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "cgst_percentage", precision = 5, scale = 2)
    private BigDecimal cgstPercentage = new BigDecimal("9.00");

    @Column(name = "cgst_amount", precision = 12, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_percentage", precision = 5, scale = 2)
    private BigDecimal sgstPercentage = new BigDecimal("9.00");

    @Column(name = "sgst_amount", precision = 12, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_percentage", precision = 5, scale = 2)
    private BigDecimal igstPercentage = new BigDecimal("18.00");

    @Column(name = "igst_amount", precision = 12, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "delivery_charges", precision = 10, scale = 2)
    private BigDecimal deliveryCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0", message = "Total amount must be non-negative")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "amount_in_words", nullable = false, length = 500)
    private String amountInWords;

    // Invoice Type
    @Column(name = "is_inter_state", nullable = false)
    private Boolean isInterState = false;

    // Storage
    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "s3_key", length = 255)
    private String s3Key;

    // Status
    @Column(name = "is_generated", nullable = false)
    private Boolean isGenerated = false;

    @Column(name = "generated_at")
    private OffsetDateTime generatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (invoiceDate == null) {
            invoiceDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyGstin() {
        return companyGstin;
    }

    public void setCompanyGstin(String companyGstin) {
        this.companyGstin = companyGstin;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientGstin() {
        return clientGstin;
    }

    public void setClientGstin(String clientGstin) {
        this.clientGstin = clientGstin;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getOriginalSubtotal() {
        return originalSubtotal;
    }

    public void setOriginalSubtotal(BigDecimal originalSubtotal) {
        this.originalSubtotal = originalSubtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getCgstPercentage() {
        return cgstPercentage;
    }

    public void setCgstPercentage(BigDecimal cgstPercentage) {
        this.cgstPercentage = cgstPercentage;
    }

    public BigDecimal getCgstAmount() {
        return cgstAmount;
    }

    public void setCgstAmount(BigDecimal cgstAmount) {
        this.cgstAmount = cgstAmount;
    }

    public BigDecimal getSgstPercentage() {
        return sgstPercentage;
    }

    public void setSgstPercentage(BigDecimal sgstPercentage) {
        this.sgstPercentage = sgstPercentage;
    }

    public BigDecimal getSgstAmount() {
        return sgstAmount;
    }

    public void setSgstAmount(BigDecimal sgstAmount) {
        this.sgstAmount = sgstAmount;
    }

    public BigDecimal getIgstPercentage() {
        return igstPercentage;
    }

    public void setIgstPercentage(BigDecimal igstPercentage) {
        this.igstPercentage = igstPercentage;
    }

    public BigDecimal getIgstAmount() {
        return igstAmount;
    }

    public void setIgstAmount(BigDecimal igstAmount) {
        this.igstAmount = igstAmount;
    }

    public BigDecimal getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(BigDecimal deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAmountInWords() {
        return amountInWords;
    }

    public void setAmountInWords(String amountInWords) {
        this.amountInWords = amountInWords;
    }

    public Boolean getIsInterState() {
        return isInterState;
    }

    public void setIsInterState(Boolean isInterState) {
        this.isInterState = isInterState;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Boolean getIsGenerated() {
        return isGenerated;
    }

    public void setIsGenerated(Boolean isGenerated) {
        this.isGenerated = isGenerated;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(OffsetDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Helper methods
    public void markAsGenerated(String pdfUrl, String s3Key) {
        this.pdfUrl = pdfUrl;
        this.s3Key = s3Key;
        this.isGenerated = true;
        this.generatedAt = OffsetDateTime.now();
    }

    /**
     * Get the effective GST amount (either CGST+SGST or IGST)
     */
    public BigDecimal getEffectiveGstAmount() {
        if (isInterState) {
            return igstAmount;
        }
        return cgstAmount.add(sgstAmount);
    }
}

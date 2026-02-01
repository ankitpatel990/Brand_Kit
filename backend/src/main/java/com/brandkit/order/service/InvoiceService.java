package com.brandkit.order.service;

import com.brandkit.order.entity.*;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.InvoiceRepository;
import com.brandkit.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Service for GST invoice generation - FRD-004 FR-44, Sub-Prompt 4
 */
@Service
@Transactional
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Value("${brandkit.company.name:BrandKit Pvt. Ltd.}")
    private String companyName;

    @Value("${brandkit.company.address:456 Business Park, Ahmedabad, Gujarat 380001}")
    private String companyAddress;

    @Value("${brandkit.company.gstin:24XXXXX1234X1ZX}")
    private String companyGstin;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Generate invoice for an order after payment confirmation
     */
    public Invoice generateInvoice(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(OrderException::orderNotFound);

        // Check if invoice already exists
        if (invoiceRepository.existsByOrderId(orderId)) {
            return invoiceRepository.findByOrderId(orderId).orElse(null);
        }

        // Validate order status
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT || 
            order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            throw new OrderException("ORD_011", "Cannot generate invoice for unpaid order");
        }

        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setInvoiceDate(LocalDate.now());

        // Company details
        invoice.setCompanyName(companyName);
        invoice.setCompanyAddress(companyAddress);
        invoice.setCompanyGstin(companyGstin);

        // Client details (from order delivery address)
        Address address = order.getDeliveryAddress();
        invoice.setClientName(address.getFullName());
        invoice.setClientAddress(address.getFormattedAddress());
        invoice.setClientPhone(address.getPhone());
        // Client GSTIN would be from user profile if available

        // Pricing
        invoice.setOriginalSubtotal(order.getOriginalSubtotal());
        invoice.setSubtotal(order.getSubtotal());
        invoice.setTotalDiscount(order.getTotalDiscount());

        // Determine inter-state based on delivery address
        boolean isInterState = !address.getState().equalsIgnoreCase("Gujarat");
        invoice.setIsInterState(isInterState);

        if (isInterState) {
            invoice.setIgstAmount(order.getIgstAmount());
            invoice.setCgstAmount(BigDecimal.ZERO);
            invoice.setSgstAmount(BigDecimal.ZERO);
        } else {
            invoice.setCgstAmount(order.getCgstAmount());
            invoice.setSgstAmount(order.getSgstAmount());
            invoice.setIgstAmount(BigDecimal.ZERO);
        }

        invoice.setDeliveryCharges(order.getDeliveryCharges());
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setAmountInWords(convertAmountToWords(order.getTotalAmount()));

        invoice = invoiceRepository.save(invoice);

        // Update order with invoice info
        order.setInvoiceNumber(invoice.getInvoiceNumber());
        orderRepository.save(order);

        // TODO: Generate PDF and upload to S3
        // This would use iText or similar library to create the PDF
        generateAndStorePdf(invoice, order);

        logger.info("Invoice {} generated for order {}", invoice.getInvoiceNumber(), order.getOrderNumber());

        return invoice;
    }

    /**
     * Get invoice for an order
     */
    @Transactional(readOnly = true)
    public Invoice getInvoice(UUID orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(OrderException::invoiceGenerationFailed);
    }

    /**
     * Get invoice by invoice number
     */
    @Transactional(readOnly = true)
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(OrderException::invoiceGenerationFailed);
    }

    /**
     * Generate PDF and store in S3
     * TODO: Implement actual PDF generation using iText or similar
     */
    private void generateAndStorePdf(Invoice invoice, Order order) {
        try {
            // Placeholder for PDF generation
            // In real implementation:
            // 1. Use iText or Apache PDFBox to generate PDF
            // 2. Include all invoice details, items, taxes
            // 3. Add digital signature
            // 4. Upload to S3
            // 5. Store URL in invoice

            String s3Key = String.format("invoices/%s/%s.pdf", 
                    order.getUser().getId(), 
                    invoice.getInvoiceNumber());
            
            // Placeholder URL - would be actual S3 URL
            String pdfUrl = "https://s3.brandkit.com/" + s3Key;

            invoice.markAsGenerated(pdfUrl, s3Key);
            invoiceRepository.save(invoice);

            // Update order with invoice URL
            order.setInvoiceUrl(pdfUrl);
            orderRepository.save(order);

            logger.info("Invoice PDF generated and stored: {}", s3Key);

        } catch (Exception e) {
            logger.error("Failed to generate invoice PDF for {}", invoice.getInvoiceNumber(), e);
            // Don't throw - invoice record exists, PDF can be regenerated later
        }
    }

    /**
     * Convert amount to words (Indian format)
     */
    private String convertAmountToWords(BigDecimal amount) {
        long rupees = amount.longValue();
        int paise = amount.remainder(BigDecimal.ONE)
                .movePointRight(2)
                .intValue();

        StringBuilder result = new StringBuilder();
        result.append(numberToWords(rupees));
        result.append(" Rupees");

        if (paise > 0) {
            result.append(" and ");
            result.append(numberToWords(paise));
            result.append(" Paise");
        }

        result.append(" Only");
        return result.toString();
    }

    /**
     * Convert number to words (Indian numbering system)
     */
    private String numberToWords(long number) {
        if (number == 0) return "Zero";

        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
                "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        if (number < 20) return ones[(int) number];
        if (number < 100) return tens[(int) (number / 10)] + (number % 10 != 0 ? " " + ones[(int) (number % 10)] : "");
        if (number < 1000) return ones[(int) (number / 100)] + " Hundred" + (number % 100 != 0 ? " " + numberToWords(number % 100) : "");
        if (number < 100000) return numberToWords(number / 1000) + " Thousand" + (number % 1000 != 0 ? " " + numberToWords(number % 1000) : "");
        if (number < 10000000) return numberToWords(number / 100000) + " Lakh" + (number % 100000 != 0 ? " " + numberToWords(number % 100000) : "");
        return numberToWords(number / 10000000) + " Crore" + (number % 10000000 != 0 ? " " + numberToWords(number % 10000000) : "");
    }

    /**
     * Regenerate PDF for an existing invoice
     */
    public void regeneratePdf(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(OrderException::invoiceGenerationFailed);

        Order order = invoice.getOrder();
        generateAndStorePdf(invoice, order);
    }

    public String getCompanyName() {
        return this.companyName;
    }
    public String getCompanyAddress() {
        return this.companyAddress;
    }
    public String getCompanyGstin() {
        return this.companyGstin;
    }
    public InvoiceRepository getInvoiceRepository() {
        return this.invoiceRepository;
    }
    public OrderRepository getOrderRepository() {
        return this.orderRepository;
    }
}

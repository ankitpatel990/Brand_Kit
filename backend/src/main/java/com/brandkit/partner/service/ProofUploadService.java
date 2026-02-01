package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.order.entity.Order;
import com.brandkit.order.entity.OrderPartnerAssignment;
import com.brandkit.order.entity.PartnerOrderStatus;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.partner.dto.ProofUploadRequest;
import com.brandkit.partner.dto.PartnerOrderResponse.ProofImageDto;
import com.brandkit.partner.entity.ProofImage;
import com.brandkit.partner.exception.PartnerException;
import com.brandkit.partner.repository.ProofImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Proof Upload Service - FRD-005 FR-58
 * Sample product image upload functionality
 */
@Service
public class ProofUploadService {
    private static final Logger log = LoggerFactory.getLogger(ProofUploadService.class);

    @Autowired
    private ProofImageRepository proofImageRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_IMAGES_PER_ORDER = 5;
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/jpg");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Upload proof images for an order
     * FRD-005 FR-58: Proof Upload Functionality
     */
    @Transactional
    public List<ProofImageDto> uploadProofs(UUID partnerId, UUID orderId, List<MultipartFile> files, List<ProofUploadRequest> metadata) {
        log.info("Partner {} uploading {} proof images for order {}", partnerId, files.size(), orderId);

        // Validate assignment
        OrderPartnerAssignment assignment = getAndValidateAssignment(partnerId, orderId);

        // Validate status (must be in production or later)
        if (assignment.getStatus() == PartnerOrderStatus.AWAITING_ACCEPTANCE ||
            assignment.getStatus() == PartnerOrderStatus.REJECTED) {
            throw new PartnerException("Cannot upload proofs for order in status: " + assignment.getStatus());
        }

        // Validate file count
        long existingCount = proofImageRepository.countByOrderId(orderId);
        if (existingCount + files.size() > MAX_IMAGES_PER_ORDER) {
            throw new PartnerException("Cannot upload more than " + MAX_IMAGES_PER_ORDER + " images per order");
        }

        // Validate files
        for (MultipartFile file : files) {
            validateFile(file);
        }

        // Upload files (simplified - in production would upload to S3)
        Order order = assignment.getOrder();
        Partner partner = assignment.getPartner();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            ProofUploadRequest meta = metadata != null && i < metadata.size() ? metadata.get(i) : null;

            // In production: Upload to S3 and get URL
            String imageUrl = "/api/proofs/" + UUID.randomUUID() + "/" + file.getOriginalFilename();
            String s3Key = "proofs/" + orderId + "/" + file.getOriginalFilename();

            ProofImage proofImage = ProofImage.builder()
                    .order(order)
                    .partner(partner)
                    .imageUrl(imageUrl)
                    .s3Key(s3Key)
                    .caption(meta != null ? meta.getCaption() : null)
                    .fileSizeBytes((int) file.getSize())
                    .displayOrder(meta != null && meta.getDisplayOrder() != null ? meta.getDisplayOrder() : i)
                    .build();

            proofImageRepository.save(proofImage);
        }

        log.info("Successfully uploaded {} proof images for order {}", files.size(), orderId);

        return getProofImages(orderId);
    }

    /**
     * Get proof images for an order
     */
    @Transactional(readOnly = true)
    public List<ProofImageDto> getProofImages(UUID orderId) {
        return proofImageRepository.findByOrderIdOrderByDisplayOrderAsc(orderId).stream()
                .map(this::mapToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Delete a proof image
     */
    @Transactional
    public void deleteProofImage(UUID partnerId, UUID orderId, UUID imageId) {
        ProofImage image = proofImageRepository.findById(imageId)
                .orElseThrow(() -> new PartnerException("Proof image not found"));

        if (!image.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied");
        }

        if (!image.getOrder().getId().equals(orderId)) {
            throw new PartnerException("Image does not belong to this order");
        }

        // In production: Delete from S3
        proofImageRepository.delete(image);
        log.info("Deleted proof image {} for order {}", imageId, orderId);
    }

    private OrderPartnerAssignment getAndValidateAssignment(UUID partnerId, UUID orderId) {
        OrderPartnerAssignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PartnerException("Order not found"));

        if (!assignment.getPartner().getId().equals(partnerId)) {
            throw new PartnerException("Access denied - order not assigned to you");
        }

        return assignment;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new PartnerException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new PartnerException("File too large. Maximum size is 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new PartnerException("Invalid file type. Allowed: JPG, PNG");
        }
    }

    private ProofImageDto mapToDto(ProofImage image) {
        return ProofImageDto.builder()
                .id(image.getId().toString())
                .imageUrl(image.getImageUrl())
                .caption(image.getCaption())
                .uploadedAt(image.getCreatedAt().format(DATETIME_FORMATTER))
                .build();
    }
}

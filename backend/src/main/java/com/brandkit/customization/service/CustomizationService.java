package com.brandkit.customization.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.customization.dto.SaveDraftRequest;
import com.brandkit.customization.entity.CustomizationDraft;
import com.brandkit.customization.repository.CustomizationDraftRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Customization Service
 * FRD-003: Customization business logic
 */
@Service
public class CustomizationService {
    private static final Logger log = LoggerFactory.getLogger(CustomizationService.class);

    @Autowired
    private CustomizationDraftRepository draftRepository;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Save customization draft
     * FRD-003 Sub-Prompt 7: Save Draft Customization
     */
    @Transactional
    public UUID saveDraft(UUID userId, SaveDraftRequest request) {
        try {
            // Convert crop data to JSON
            String cropDataJson = objectMapper.writeValueAsString(request.getCropData());

            CustomizationDraft draft = CustomizationDraft.builder()
                    .userId(userId)
                    .productId(request.getProductId())
                    .logoFileUrl(request.getLogoFileUrl())
                    .logoFileName(request.getLogoFileName())
                    .logoFileSize(request.getLogoFileSize())
                    .logoDimensions(request.getLogoDimensions())
                    .cropData(cropDataJson)
                    .croppedImageUrl(request.getCroppedImageUrl())
                    .previewImageUrl(request.getPreviewImageUrl())
                    .bundleId(request.getBundleId())
                    .bundleName(request.getBundleName())
                    .status("draft")
                    .expiresAt(LocalDateTime.now().plusDays(30))
                    .build();

            draft = draftRepository.save(draft);
            log.info("Draft saved: {} for user: {}", draft.getId(), userId);
            return draft.getId();
        } catch (Exception e) {
            log.error("Failed to save draft", e);
            throw new RuntimeException("Failed to save draft: " + e.getMessage(), e);
        }
    }

    /**
     * Get user's drafts
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserDrafts(UUID userId) {
        List<CustomizationDraft> drafts = draftRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return drafts.stream()
                .filter(draft -> draft.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(this::draftToMap)
                .collect(Collectors.toList());
    }

    /**
     * Get draft by ID
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDraft(UUID draftId, UUID userId) {
        CustomizationDraft draft = draftRepository.findByIdAndUserId(draftId, userId)
                .orElseThrow(() -> new RuntimeException("Draft not found or expired"));

        if (draft.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Draft expired");
        }

        return draftToMap(draft);
    }

    /**
     * Delete draft
     */
    @Transactional
    public void deleteDraft(UUID draftId, UUID userId) {
        CustomizationDraft draft = draftRepository.findByIdAndUserId(draftId, userId)
                .orElseThrow(() -> new RuntimeException("Draft not found"));
        
        draftRepository.delete(draft);
        log.info("Draft deleted: {} for user: {}", draftId, userId);
    }

    private Map<String, Object> draftToMap(CustomizationDraft draft) {
        return Map.of(
                "id", draft.getId().toString(),
                "productId", draft.getProductId().toString(),
                "logoFileUrl", draft.getLogoFileUrl(),
                "logoFileName", draft.getLogoFileName(),
                "croppedImageUrl", draft.getCroppedImageUrl(),
                "previewImageUrl", draft.getPreviewImageUrl() != null ? draft.getPreviewImageUrl() : "",
                "bundleName", draft.getBundleName() != null ? draft.getBundleName() : "",
                "createdAt", draft.getCreatedAt().toString(),
                "expiresAt", draft.getExpiresAt().toString()
        );
    }
}

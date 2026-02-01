package com.brandkit.partner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserType;
import com.brandkit.auth.repository.UserRepository;
import com.brandkit.catalog.entity.Partner;
import com.brandkit.catalog.repository.PartnerRepository;
import com.brandkit.order.entity.PartnerOrderStatus;
import com.brandkit.order.repository.OrderPartnerAssignmentRepository;
import com.brandkit.partner.dto.PartnerProfileRequest;
import com.brandkit.partner.dto.PartnerProfileResponse;
import com.brandkit.partner.dto.PartnerProfileResponse.*;
import com.brandkit.partner.exception.PartnerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

/**
 * Partner Profile Service - FRD-005 FR-64
 * Partner profile management
 */
@Service
public class PartnerProfileService {
    private static final Logger log = LoggerFactory.getLogger(PartnerProfileService.class);

    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderPartnerAssignmentRepository assignmentRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Get partner profile
     * FRD-005 FR-64: Partner Profile Management
     */
    @Transactional(readOnly = true)
    public PartnerProfileResponse getProfile(UUID partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        return mapToProfileResponse(partner);
    }

    /**
     * Get partner by user ID
     */
    @Transactional(readOnly = true)
    public Partner getPartnerByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PartnerException("User not found"));

        if (user.getUserType() != UserType.PARTNER) {
            throw new PartnerException("User is not a partner");
        }

        return partnerRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new PartnerException("Partner profile not found"));
    }

    /**
     * Update partner profile
     */
    @Transactional
    public PartnerProfileResponse updateProfile(UUID partnerId, PartnerProfileRequest request) {
        log.info("Updating profile for partner: {}", partnerId);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        // Update business info
        if (request.getBusinessName() != null) {
            partner.setBusinessName(request.getBusinessName());
        }
        if (request.getPhone() != null) {
            partner.setPhone(request.getPhone());
        }
        if (request.getCity() != null) {
            partner.setCity(request.getCity());
        }
        if (request.getState() != null) {
            partner.setState(request.getState());
        }
        if (request.getGstin() != null) {
            partner.setGstNumber(request.getGstin());
        }

        // Update bank details (mark as unverified when changed)
        boolean bankDetailsChanged = false;
        if (request.getBankAccountNumber() != null) {
            // Check if bank details changed
            String currentMasked = maskAccountNumber(partner.getBankAccountNumber());
            if (!request.getBankAccountNumber().equals(currentMasked)) {
                partner.setBankAccountNumber(request.getBankAccountNumber());
                bankDetailsChanged = true;
            }
        }
        if (request.getBankAccountHolder() != null) {
            partner.setBankAccountHolder(request.getBankAccountHolder());
        }
        if (request.getBankName() != null) {
            partner.setBankName(request.getBankName());
        }
        if (request.getBankIfscCode() != null) {
            partner.setBankIfscCode(request.getBankIfscCode());
        }

        if (bankDetailsChanged) {
            partner.setBankVerified(false);
        }

        // Update capacity settings
        if (request.getMaxConcurrentOrders() != null) {
            partner.setMaxConcurrentOrders(request.getMaxConcurrentOrders());
        }
        if (request.getIsAcceptingOrders() != null) {
            partner.setIsAcceptingOrders(request.getIsAcceptingOrders());
        }

        // Check if profile is complete
        checkProfileComplete(partner);

        partnerRepository.save(partner);
        log.info("Profile updated for partner: {}", partnerId);

        return mapToProfileResponse(partner);
    }

    /**
     * Complete first login profile setup
     */
    @Transactional
    public PartnerProfileResponse completeProfile(UUID partnerId, PartnerProfileRequest request) {
        log.info("Completing profile for partner: {}", partnerId);

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new PartnerException("Partner not found"));

        // Set all required fields
        partner.setBusinessName(request.getBusinessName());
        partner.setOwnerName(request.getOwnerName());
        partner.setPhone(request.getPhone());
        partner.setBusinessAddress(request.getBusinessAddress());
        partner.setCity(request.getCity());
        partner.setState(request.getState());
        partner.setGstNumber(request.getGstin());
        partner.setBankAccountHolder(request.getBankAccountHolder());
        partner.setBankName(request.getBankName());
        partner.setBankAccountNumber(request.getBankAccountNumber());
        partner.setBankIfscCode(request.getBankIfscCode());
        partner.setMaxConcurrentOrders(request.getMaxConcurrentOrders() != null ? request.getMaxConcurrentOrders() : 20);
        partner.setIsAcceptingOrders(true);
        partner.setProfileCompleted(true);
        partner.setFirstLoginAt(java.time.ZonedDateTime.now());

        if (request.getCategories() != null) {
            partner.setCategories(request.getCategories().toArray(new String[0]));
        }

        partnerRepository.save(partner);
        log.info("Profile completed for partner: {}", partnerId);

        return mapToProfileResponse(partner);
    }

    private void checkProfileComplete(Partner partner) {
        boolean complete = partner.getBusinessName() != null &&
                partner.getPhone() != null &&
                partner.getBankAccountHolder() != null &&
                partner.getBankName() != null &&
                partner.getBankAccountNumber() != null &&
                partner.getBankIfscCode() != null;

        partner.setProfileCompleted(complete);
    }

    private PartnerProfileResponse mapToProfileResponse(Partner partner) {
        // Count active orders
        long activeOrders = countActiveOrders(partner.getId());

        // Bank details
        BankDetails bankDetails = BankDetails.builder()
                .accountHolder(partner.getBankAccountHolder())
                .bankName(partner.getBankName())
                .accountNumber(maskAccountNumber(partner.getBankAccountNumber()))
                .ifscCode(partner.getBankIfscCode())
                .verified(partner.getBankVerified() != null && partner.getBankVerified())
                .build();

        // Capacity settings
        CapacitySettings capacitySettings = CapacitySettings.builder()
                .maxConcurrentOrders(partner.getMaxConcurrentOrders() != null ? partner.getMaxConcurrentOrders() : 20)
                .isAcceptingOrders(partner.getIsAcceptingOrders() != null && partner.getIsAcceptingOrders())
                .currentActiveOrders((int) activeOrders)
                .build();

        return PartnerProfileResponse.builder()
                .partnerId(partner.getId().toString())
                .email(partner.getEmail())
                .businessName(partner.getBusinessName())
                .ownerName(partner.getOwnerName())
                .phone(partner.getPhone())
                .businessAddress(partner.getBusinessAddress())
                .city(partner.getCity())
                .state(partner.getState())
                .gstin(partner.getGstNumber())
                .profilePictureUrl(partner.getProfilePictureUrl())
                .profileCompleted(partner.getProfileCompleted() != null && partner.getProfileCompleted())
                .bankDetails(bankDetails)
                .capacitySettings(capacitySettings)
                .categories(partner.getCategories() != null ? Arrays.asList(partner.getCategories()) : null)
                .commissionRate(partner.getCommissionRate())
                .status(partner.getStatus().name())
                .createdAt(partner.getCreatedAt().format(DATE_FORMATTER))
                .build();
    }

    private long countActiveOrders(UUID partnerId) {
        long inProduction = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.IN_PRODUCTION);
        long accepted = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.ACCEPTED);
        long pending = assignmentRepository.countByPartnerIdAndStatus(partnerId, PartnerOrderStatus.AWAITING_ACCEPTANCE);
        return inProduction + accepted + pending;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        int visibleChars = 4;
        String masked = "X".repeat(accountNumber.length() - visibleChars);
        return masked + accountNumber.substring(accountNumber.length() - visibleChars);
    }
}

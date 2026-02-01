package com.brandkit.order.service;

import com.brandkit.auth.entity.User;
import com.brandkit.order.dto.AddressRequest;
import com.brandkit.order.dto.AddressResponse;
import com.brandkit.order.entity.Address;
import com.brandkit.order.entity.PinCodeServiceability;
import com.brandkit.order.exception.OrderException;
import com.brandkit.order.repository.AddressRepository;
import com.brandkit.order.repository.PinCodeServiceabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for address management - FRD-004 FR-41
 */
@Service
@Transactional
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PinCodeServiceabilityRepository pinCodeRepository;

    /**
     * Get all addresses for a user
     */
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(User user) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get address by ID
     */
    @Transactional(readOnly = true)
    public AddressResponse getAddress(User user, UUID addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(OrderException::addressNotFound);
        return mapToResponse(address);
    }

    /**
     * Get default address for a user
     */
    @Transactional(readOnly = true)
    public AddressResponse getDefaultAddress(User user) {
        return addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .map(this::mapToResponse)
                .orElse(null);
    }

    /**
     * Create new address
     */
    public AddressResponse createAddress(User user, AddressRequest request) {
        // Validate PIN code serviceability
        PinCodeServiceability pinCode = validatePinCode(request.getPinCode());

        // If this is the first address, make it default
        long addressCount = addressRepository.countByUserId(user.getId());
        boolean isDefault = request.getIsDefault() || addressCount == 0;

        // Clear other defaults if this is default
        if (isDefault) {
            addressRepository.clearDefaultAddresses(user.getId());
        }

        Address address = new Address();
        address.setUser(user);
        address.setFullName(request.getFullName());
        address.setPhone(formatPhoneNumber(request.getPhone()));
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(pinCode.getCity()); // Use city from PIN code data
        address.setState(pinCode.getState()); // Use state from PIN code data
        address.setPinCode(request.getPinCode());
        address.setAddressType(request.getAddressType());
        address.setIsDefault(isDefault);
        address.setIsServiceable(pinCode.getIsServiceable());

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    /**
     * Update existing address
     */
    public AddressResponse updateAddress(User user, UUID addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(OrderException::addressNotFound);

        // Validate PIN code serviceability
        PinCodeServiceability pinCode = validatePinCode(request.getPinCode());

        // Handle default flag
        if (request.getIsDefault() && !address.getIsDefault()) {
            addressRepository.clearDefaultAddresses(user.getId());
        }

        address.setFullName(request.getFullName());
        address.setPhone(formatPhoneNumber(request.getPhone()));
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(pinCode.getCity());
        address.setState(pinCode.getState());
        address.setPinCode(request.getPinCode());
        address.setAddressType(request.getAddressType());
        address.setIsDefault(request.getIsDefault());
        address.setIsServiceable(pinCode.getIsServiceable());

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    /**
     * Delete address
     */
    public void deleteAddress(User user, UUID addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(OrderException::addressNotFound);

        // If deleting default address, make another one default
        if (address.getIsDefault()) {
            addressRepository.delete(address);
            // Find another address to make default
            addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId())
                    .stream()
                    .findFirst()
                    .ifPresent(addr -> {
                        addr.setIsDefault(true);
                        addressRepository.save(addr);
                    });
        } else {
            addressRepository.delete(address);
        }
    }

    /**
     * Set address as default
     */
    public AddressResponse setDefaultAddress(User user, UUID addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(OrderException::addressNotFound);

        addressRepository.clearDefaultAddresses(user.getId());
        address.setIsDefault(true);
        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    /**
     * Validate PIN code serviceability
     */
    public PinCodeServiceability validatePinCode(String pinCode) {
        return pinCodeRepository.findByPinCode(pinCode)
                .filter(PinCodeServiceability::getIsServiceable)
                .orElseThrow(OrderException::pinCodeNotServiceable);
    }

    /**
     * Check if PIN code is serviceable
     */
    @Transactional(readOnly = true)
    public boolean isPinCodeServiceable(String pinCode) {
        return pinCodeRepository.existsByPinCodeAndIsServiceableTrue(pinCode);
    }

    /**
     * Format phone number to +91XXXXXXXXXX
     */
    private String formatPhoneNumber(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() == 10) {
            return "+91" + digits;
        } else if (digits.length() == 12 && digits.startsWith("91")) {
            return "+" + digits;
        }
        return phone;
    }

    /**
     * Map address entity to response DTO
     */
    private AddressResponse mapToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setFullName(address.getFullName());
        response.setPhone(address.getPhone());
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPinCode(address.getPinCode());
        response.setAddressType(address.getAddressType());
        response.setIsDefault(address.getIsDefault());
        response.setIsServiceable(address.getIsServiceable());
        response.setFormattedAddress(address.getFormattedAddress());
        return response;
    }
}

package com.brandkit.partner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Partner Exception - FRD-005
 * Custom exception for partner-related errors
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PartnerException extends RuntimeException {

    public PartnerException(String message) {
        super(message);
    }

    public PartnerException(String message, Throwable cause) {
        super(message, cause);
    }
}

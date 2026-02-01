package com.brandkit.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.admin.entity.AdminAuditLog;
import com.brandkit.admin.repository.AdminAuditLogRepository;
import com.brandkit.auth.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for admin audit logging
 * 
 * FRD-006 FR-75: System Logs
 * FRD-006 NFR-129: All admin actions recorded in audit log (immutable)
 * FRD-006 NFR-130: Audit logs include admin user ID, action type, entity affected, old/new value, timestamp
 */
@Service
public class AdminAuditService {
    private static final Logger log = LoggerFactory.getLogger(AdminAuditService.class);

    @Autowired
    private AdminAuditLogRepository auditLogRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Log an admin action
     */
    @Transactional
    public AdminAuditLog logAction(
            User admin,
            String actionType,
            String entityType,
            UUID entityId,
            Object oldValue,
            Object newValue) {
        
        return logAction(admin, actionType, entityType, entityId, oldValue, newValue, null, null);
    }

    /**
     * Log an admin action with request context
     */
    @Transactional
    public AdminAuditLog logAction(
            User admin,
            String actionType,
            String entityType,
            UUID entityId,
            Object oldValue,
            Object newValue,
            String ipAddress,
            String userAgent) {
        
        Map<String, Object> oldValues = convertToMap(oldValue);
        Map<String, Object> newValues = convertToMap(newValue);

        AdminAuditLog auditLog = AdminAuditLog.builder()
                .admin(admin)
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .oldValues(oldValues)
                .newValues(newValues)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLog = auditLogRepository.save(auditLog);
        
        log.debug("Audit log created: {} {} {} by {}", 
                actionType, entityType, entityId, admin.getEmail());
        
        return auditLog;
    }

    /**
     * Async logging for non-critical actions
     */
    @Async
    @Transactional
    public void logActionAsync(
            User admin,
            String actionType,
            String entityType,
            UUID entityId,
            Object oldValue,
            Object newValue) {
        
        logAction(admin, actionType, entityType, entityId, oldValue, newValue);
    }

    /**
     * Get audit logs by admin ID
     */
    public Page<AdminAuditLog> getLogsByAdmin(UUID adminId, Pageable pageable) {
        return auditLogRepository.findByAdminIdOrderByCreatedAtDesc(adminId, pageable);
    }

    /**
     * Get audit logs by action type
     */
    public Page<AdminAuditLog> getLogsByAction(String actionType, Pageable pageable) {
        return auditLogRepository.findByActionTypeOrderByCreatedAtDesc(actionType, pageable);
    }

    /**
     * Get audit logs for a specific entity
     */
    public Page<AdminAuditLog> getLogsByEntity(String entityType, UUID entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId, pageable);
    }

    /**
     * Get audit logs within date range
     */
    public Page<AdminAuditLog> getLogsByDateRange(ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Search audit logs by keyword
     */
    public Page<AdminAuditLog> searchLogs(String keyword, Pageable pageable) {
        return auditLogRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * Convert object to Map for JSON storage
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        
        try {
            String json = objectMapper.writeValueAsString(value);
            return objectMapper.readValue(json, HashMap.class);
        } catch (Exception e) {
            log.warn("Failed to convert object to map: {}", e.getMessage());
            Map<String, Object> map = new HashMap<>();
            map.put("value", value.toString());
            return map;
        }
    }
}

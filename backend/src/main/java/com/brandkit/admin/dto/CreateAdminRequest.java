package com.brandkit.admin.dto;

import com.brandkit.admin.entity.AdminRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/**
 * Request DTO for creating a new admin account
 * 
 * FRD-006 FR-66: Admin Creation (Super Admin only)
 */
public class CreateAdminRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotNull(message = "Admin role is required")
    private AdminRole adminRole;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @Size(max = 50, message = "Employee ID must not exceed 50 characters")
    private String employeeId;

    /**
     * Password will be auto-generated if not provided
     */
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    public String getFullName() {
        return this.fullName;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPhone() {
        return this.phone;
    }
    public AdminRole getAdminRole() {
        return this.adminRole;
    }
    public String getDepartment() {
        return this.department;
    }
    public String getEmployeeId() {
        return this.employeeId;
    }
    public String getPassword() {
        return this.password;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAdminRole(AdminRole adminRole) {
        this.adminRole = adminRole;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public CreateAdminRequest() {
    }
    public CreateAdminRequest(String fullName, String email, String phone, AdminRole adminRole, String department, String employeeId, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.adminRole = adminRole;
        this.department = department;
        this.employeeId = employeeId;
        this.password = password;
    }
    public static CreateAdminRequestBuilder builder() {
        return new CreateAdminRequestBuilder();
    }

    public static class CreateAdminRequestBuilder {
        private String fullName;
        private String email;
        private String phone;
        private AdminRole adminRole;
        private String department;
        private String employeeId;
        private String password;

        CreateAdminRequestBuilder() {
        }

        public CreateAdminRequestBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public CreateAdminRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CreateAdminRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public CreateAdminRequestBuilder adminRole(AdminRole adminRole) {
            this.adminRole = adminRole;
            return this;
        }

        public CreateAdminRequestBuilder department(String department) {
            this.department = department;
            return this;
        }

        public CreateAdminRequestBuilder employeeId(String employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public CreateAdminRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public CreateAdminRequest build() {
            CreateAdminRequest instance = new CreateAdminRequest();
            instance.fullName = this.fullName;
            instance.email = this.email;
            instance.phone = this.phone;
            instance.adminRole = this.adminRole;
            instance.department = this.department;
            instance.employeeId = this.employeeId;
            instance.password = this.password;
            return instance;
        }
    }
}

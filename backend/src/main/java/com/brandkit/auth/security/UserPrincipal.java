package com.brandkit.auth.security;

import com.brandkit.auth.entity.User;
import com.brandkit.auth.entity.UserType;
import com.brandkit.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * User Principal - Spring Security UserDetails implementation
 * 
 * FRD-001 FR-8: Role-Based Access Control
 * Represents authenticated user with role information
 */
public class UserPrincipal implements UserDetails {

    private UUID id;
    private String email;
    private String fullName;
    private UserType userType;
    private boolean enabled;
    private boolean accountNonLocked;

    public static UserPrincipal fromUser(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType())
                .enabled(user.canLogin())
                .accountNonLocked(!user.isLocked())
                .build();
    }

    public static UserPrincipal fromClaims(UUID id, String email, String name, UserType role) {
        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .fullName(name)
                .userType(role)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userType.name()));
    }

    @Override
    public String getPassword() {
        return null; // Not used for JWT auth
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Get the full User entity from repository
     * This method requires a UserRepository to fetch the complete user object
     */
    public User getUser(UserRepository userRepository) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public UUID getId() {
        return this.id;
    }
    public String getEmail() {
        return this.email;
    }
    public String getFullName() {
        return this.fullName;
    }
    public UserType getUserType() {
        return this.userType;
    }
    public boolean getEnabled() {
        return this.enabled;
    }
    public boolean getAccountNonLocked() {
        return this.accountNonLocked;
    }
    public UserPrincipal() {
    }

    public UserPrincipal(UUID id, String email, String fullName, UserType userType, boolean enabled, boolean accountNonLocked) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.userType = userType;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
    }
    public static UserPrincipalBuilder builder() {
        return new UserPrincipalBuilder();
    }

    public static class UserPrincipalBuilder {
        private UUID id;
        private String email;
        private String fullName;
        private UserType userType;
        private boolean enabled;
        private boolean accountNonLocked;

        UserPrincipalBuilder() {
        }

        public UserPrincipalBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserPrincipalBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserPrincipalBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserPrincipalBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public UserPrincipalBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserPrincipalBuilder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public UserPrincipal build() {
            UserPrincipal instance = new UserPrincipal();
            instance.id = this.id;
            instance.email = this.email;
            instance.fullName = this.fullName;
            instance.userType = this.userType;
            instance.enabled = this.enabled;
            instance.accountNonLocked = this.accountNonLocked;
            return instance;
        }
    }
}

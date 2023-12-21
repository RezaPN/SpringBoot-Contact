package com.adira.contact.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AuthenticatedUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUserDetails(Long userId, String email, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        // Return the password if needed; it's not recommended to expose passwords.
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Return true if the user account is not expired.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Return true if the user account is not locked.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Return true if the user credentials are not expired.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Return true if the user is enabled.
        return true;
    }
}

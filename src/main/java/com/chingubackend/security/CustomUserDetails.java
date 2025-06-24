package com.chingubackend.security;

import com.chingubackend.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String userId;
    private final String password;
    private final String role;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.role = user.getRole().name();  // "ROLE_USER" or "ROLE_ADMIN"
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
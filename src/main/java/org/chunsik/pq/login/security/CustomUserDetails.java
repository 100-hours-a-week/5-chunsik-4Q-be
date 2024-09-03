package org.chunsik.pq.login.security;

import lombok.Getter;
import org.chunsik.pq.login.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final String email;
    @Getter
    private final String nickname;
    private final String password;
    @Getter
    private final Long id;

    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.password = user.getPassword();
        this.id = user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}

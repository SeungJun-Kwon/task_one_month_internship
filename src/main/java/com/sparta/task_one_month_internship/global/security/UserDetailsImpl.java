package com.sparta.task_one_month_internship.global.security;

import com.sparta.task_one_month_internship.domain.user.entity.User;
import com.sparta.task_one_month_internship.domain.user.entity.UserRole;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

    private User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        if (user != null) {
            return user.getPassword();
        }

        return null;
    }

    @Override
    public String getUsername() {
        if (user != null) {
            return user.getUsername();
        }

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role;
        if (user != null) {
            role = user.getAuthorityName();
        } else {
            return null;
        }

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(
            role.getAuthority());
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
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
}

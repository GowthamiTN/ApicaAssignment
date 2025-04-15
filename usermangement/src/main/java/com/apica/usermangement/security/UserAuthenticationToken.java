package com.apica.usermangement.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;

    public UserAuthenticationToken(String username, Object roles) {
        super(mapRolesToAuthorities(roles));
        this.username = username;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> mapRolesToAuthorities(Object roles) {
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .map(role -> new SimpleGrantedAuthority(role.toString()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return null; // Credentials are not needed after authentication
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}

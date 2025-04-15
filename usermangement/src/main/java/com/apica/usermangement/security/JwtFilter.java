package com.apica.usermangement.security;

import com.apica.usermangement.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String header = httpRequest.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // remove "Bearer "

            try {
                String username = jwtUtil.extractUsername(token);

                // Only proceed if no authentication is already set
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Load user details from DB
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // ✅ Validate token specifically for this user
                    if (jwtUtil.validateToken(token, userDetails)) {

                        List<String> roles = jwtUtil.extractAllClaims(token).get("roles", List.class);
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        System.out.println("Authenticated user: " + username + " with roles: " + roles);
                    } else {
                        System.out.println("Token validation failed for user: " + username);
                    }
                }

            } catch (Exception e) {
                System.out.println("JWT Filter Exception: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }

}

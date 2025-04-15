package com.apica.usermangement.service;

import com.apica.usermangement.dto.AuthRequest;
import com.apica.usermangement.dto.AuthResponse;
import com.apica.usermangement.dto.RegisterRequest;
import com.apica.usermangement.dto.Response;
import com.apica.usermangement.model.Role;
import com.apica.usermangement.model.User;
import com.apica.usermangement.repository.UserRepository;
import com.apica.usermangement.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private List<String> getRoleNames(User user) {
        return user.getRoles().stream().map(Role::getName).toList();
    }

    public ResponseEntity<Response> validateLogin(String token) {
        try {
            Claims claims = jwtUtil.validateToken(token);
            String username = claims.getSubject(); // if subject is username

            return ResponseEntity.ok(new Response(200, "Login successful for user: " + username));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body(new Response(401, "Token expired"));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(401).body(new Response(401, "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(500, "Internal server error: " + e.getMessage()));
        }
    }
}

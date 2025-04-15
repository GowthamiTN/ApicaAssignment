package com.apica.usermangement.controller;


import com.apica.usermangement.dto.*;
import com.apica.usermangement.events.UserEventPublisher;
import com.apica.usermangement.security.JwtUtil;
import com.apica.usermangement.service.AuthService;
import com.apica.usermangement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private final AuthService authService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserService userService, AuthService authService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authService = authService;
    }
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @PostMapping("/token")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("roles", userService.getRoles(request.getUsername()));

            String token = jwtUtil.generateToken(request.getUsername(), extraClaims);
            logger.info("JWT generated for user: {}", request.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            logger.error("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest request) {
        try {
            Response response = userService.registerUser(request);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception e) {
            logger.error("User registration failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(500, "User registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(401, "Missing or invalid Authorization header"));
            }

            String jwt = authHeader.substring(7); // Remove "Bearer "
            Response response = authService.validateLogin(jwt).getBody();

            return ResponseEntity.status(response.getCode()).body(response);

        } catch (Exception e) {
            logger.error("Login validation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(500, "Login validation failed: " + e.getMessage()));
        }
    }


}

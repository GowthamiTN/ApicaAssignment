package com.apica.usermangement.controller;

import com.apica.usermangement.dto.RegisterRequest;
import com.apica.usermangement.dto.Response;
import com.apica.usermangement.model.User;
import com.apica.usermangement.security.JwtUtil;
import com.apica.usermangement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetchUserData() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user data: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Response> updateUser(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        try {
            String token = jwtUtil.extractTokenFromRequest(httpRequest);
            String usernameFromToken = jwtUtil.extractUsername(token);

            if (!usernameFromToken.equals(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(401, "You are not authorized to update this user."));
            }

            Response serviceResponse = userService.updateUser(request);
            return ResponseEntity.status(serviceResponse.getCode()).body(serviceResponse);
        } catch (Exception e) {
            logger.error("Exception while updating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response(500, "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        Response response = userService.deleteUser(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}

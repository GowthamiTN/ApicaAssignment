package com.apica.usermangement.service;

import com.apica.usermangement.dto.RegisterRequest;
import com.apica.usermangement.dto.Response;
import com.apica.usermangement.events.UserEventPublisher;
import com.apica.usermangement.model.Role;
import com.apica.usermangement.model.User;
import com.apica.usermangement.repository.RoleRepository;
import com.apica.usermangement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher eventPublisher;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    public Response registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new Response(200, "User Already Exists");
        }
        Set<Role> roleEntities = new HashSet<>();

        for (Role roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName.getName()).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roleEntities.add(role);
        }
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(roleEntities);
            user.setEmail(request.getEmail());
            userRepository.save(user);
            eventPublisher.publishUserEvent("CREATE_USER", user);
            return new Response(200, "User Created updated successfully");
        } catch (Exception e) {
            return new Response(500, "Failed to save user: " + e.getMessage());
        }
    }

    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
    public Response updateUser(RegisterRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

            if (optionalUser.isEmpty()) {
                return new Response(404, "User with username '" + request.getUsername() + "' not found");
            }

            User user = optionalUser.get();

            if (request.getUsername() != null) {
                user.setUsername(request.getUsername());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            userRepository.save(user);
            eventPublisher.publishUserEvent("UPDATE_USER", user);

            return new Response(200, "User updated successfully");
        } catch (Exception e) {
            // Log the exception
            //logger.error("Error occurred while updating user: {}", e.getMessage(), e);
            return new Response(500, "An unexpected error occurred: " + e.getMessage());
        }
    }


    public List<String> getRoles(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    List<String> roles = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList());

                    logger.info("Fetched roles for user '{}': {}", username, roles);
                    return roles;
                })
                .orElseThrow(() -> {
                    logger.warn("User '{}' not found when fetching roles", username);
                    return new RuntimeException("User not found with username: " + username);
                });
    }
    public Response deleteUser(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                logger.warn("User with ID {} not found for deletion", id);
                return new Response(404, "User not found");
            }

            userRepository.deleteById(id);
            logger.info("User with ID {} deleted successfully", id);
            return new Response(200, "User deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            return new Response(500, "Failed to delete user: " + e.getMessage());
        }
    }
}


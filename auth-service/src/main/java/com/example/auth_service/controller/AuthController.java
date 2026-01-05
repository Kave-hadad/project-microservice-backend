package com.example.auth_service.controller;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_service.dto.AuthResponseDTO;
import com.example.auth_service.dto.LoginRequestDTO;
import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.dto.RegisterResponseDTO;
import com.example.auth_service.dto.ResponseDTO;
import com.example.auth_service.dto.UserProfileDTO;
import com.example.auth_service.entity.UserCredential;
import com.example.auth_service.security.JwtService;
import com.example.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtService jwtService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    
 
    
    @Operation(summary = "Register user with admin role",description = "Allows to create a new user account with admin role.")
    @PostMapping("/register/admin")
    public ResponseEntity<RegisterResponseDTO> createUserByAdmin(@Valid @RequestBody RegisterRequestDTO dto) {
        logger.info("Admin creating user with email: {}", dto.getEmail());
        UserCredential credential = authService.createCredentialByAdmin(dto);
        String token = jwtService.generateToken(credential.getEmail(), credential.getRole());
        UserProfileDTO profile = authService.createUserProfile(dto, credential.getId());
        RegisterResponseDTO registerResponseDto = new RegisterResponseDTO(token, profile);
        logger.info("Admin created user successfully with id: {}", credential.getId());
        return ResponseEntity.ok(registerResponseDto);
    }

    
    @Operation(summary = "Register new user",description = "Creates a new user account with email, password, and profile information.")   
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        logger.info("Register request received for email: {}", dto.getEmail());
        UserCredential credential = authService.createCredential(dto);
        String token = jwtService.generateToken(credential.getEmail(), credential.getRole());
        UserProfileDTO profile = authService.createUserProfile(dto, credential.getId());
        RegisterResponseDTO registerResponseDto = new RegisterResponseDTO(token, profile);
        logger.info("User registered successfully with id: {}", credential.getId());
        return ResponseEntity.ok(registerResponseDto);
    }
    
    
    @Operation(summary = "Get all users",description = "Fetches a list of all registered users. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<ResponseDTO>> getAllUsers() {
        logger.debug("Fetching all users...");
        List<ResponseDTO> users = authService.getAllUsers();
        logger.info("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    
    @Operation(summary = "Get users by family name",description = "Fetches users filtered by family name. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/family/{family}")
    public ResponseEntity<List<ResponseDTO>> getUserByFamily(@PathVariable String family) {
        logger.debug("Fetching users by family: {}", family);
        List<ResponseDTO> users = authService.getUserByFamily(family);
        logger.info("Found {} users with family {}", users.size(), family);
        return ResponseEntity.ok(users);
    }

    
    
    @Operation(summary = "Delete user by ID",description = "Deletes a user account by its unique ID. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@Parameter(description = "User ID", required = true, example = "123")
                                               @PathVariable("id") Long id) {
        logger.warn("Deleting user with id: {}", id);
        authService.deleteUserById(id);
        logger.info("User with id {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    
    
    @Operation(summary = "Patch user or Admin profile",description = "Updates partial information of a user profile and password. Accessible by ADMIN or the user themselves.")
    @PreAuthorize("hasRole('ADMIN') or #dto.email == authentication.name")
    @PatchMapping("/users/{id}")
    public ResponseEntity<UserProfileDTO> patchUser(@PathVariable("id") long authUserId,
                                                    @Valid @RequestBody RegisterRequestDTO dto) {
        logger.info("Patching user with id: {}", authUserId);
        UserProfileDTO updatedProfile = authService.patchUser(dto, authUserId);
        logger.info("User with id {} patched successfully", authUserId);
        return ResponseEntity.ok(updatedProfile);
    }

    
    
    
    @Operation(summary = "Get user by ID",description = "Fetches a single user by their unique ID. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseDTO> getUserByAuthUserId(@Parameter(description = "User ID", required = true, example = "123")
                                                           @PathVariable Long id) {
        logger.debug("Fetching user by id: {}", id);
        ResponseDTO user = authService.getUserByAuthUserId(id);
        logger.info("Fetched user with id {}", id);
        return ResponseEntity.ok(user);
    }


    @Operation(summary = "Login user",description = "Authenticates a user with email and password, returns JWT token.") 
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
    	 logger.info("Login attempt for email: {}", request.getEmail());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();
        String token = jwtService.generateToken(request.getEmail(), role);
        logger.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    
     
    @Operation(summary = "Refresh JWT token", description = "Generates a new JWT token if the provided one is valid but expired.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshExpiredToken(@RequestHeader("Authorization") String authHeader) {
        logger.debug("Refreshing token...");
        String token = authHeader.substring(7);
        if (jwtService.validateToken(token)) {
            String role = jwtService.extractRole(token);
            String email = jwtService.extractUsername(token);
            String newToken = jwtService.generateToken(email, role);
            logger.info("Token refreshed successfully for email: {}", email);
            return ResponseEntity.ok(new AuthResponseDTO(newToken));
        } else {
            logger.warn("Invalid token provided for refresh");
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
}

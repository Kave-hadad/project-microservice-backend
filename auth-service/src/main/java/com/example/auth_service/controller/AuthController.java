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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
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




	@PostMapping("/register")
	public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
		UserCredential credential = authService.createCredential(dto);
		//UserProfileDTO profile = authService.createUserProfile(dto,credential.getId());

		String token = jwtService.generateToken(credential.getEmail(), credential.getRole());
		UserProfileDTO profile = authService.createUserProfile(dto,credential.getId());
		RegisterResponseDTO registerResponseDto = new RegisterResponseDTO(token, profile);

		return ResponseEntity.ok(registerResponseDto);
//    UserProfileDTO profile = authService.createUserProfile(dto);

//    return ResponseEntity.ok(profile);
	}

	@PostMapping("/register/admin")
	public ResponseEntity<RegisterResponseDTO> createUserByAdmin(@Valid @RequestBody RegisterRequestDTO dto) {
		UserCredential credential = authService.createCredentialByAdmin(dto);
		//UserProfileDTO profile = authService.createUserProfile(dto,credential.getId());
		String token = jwtService.generateToken(credential.getEmail(), credential.getRole());
		UserProfileDTO profile = authService.createUserProfile(dto,credential.getId());
		RegisterResponseDTO registerResponseDto = new RegisterResponseDTO(token, profile);

		return ResponseEntity.ok(registerResponseDto);

//    UserProfileDTO profile = authService.createUserProfile(dto);
//    return ResponseEntity.ok(profile);
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users")
    public ResponseEntity<List<ResponseDTO>> getAllUsers() {
System.out.println("im in getAllUsers before");
        List<ResponseDTO> users = authService.getAllUsers();
        System.out.println("im in getAllUsers after");
        return ResponseEntity.ok(users);
    }
	
	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/family/{family}")
    public ResponseEntity<List<ResponseDTO>> getUserByFamily(@PathVariable String family) {
        List<ResponseDTO> users = authService.getUserByFamily(family);
        return ResponseEntity.ok(users);
    }
	
	@Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById( @Parameter(description = "User ID", required = true, example = "123")@PathVariable("id") Long id) {
        authService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @PreAuthorize("hasRole('ADMIN') or #authUserId == principal.id")
    @PatchMapping("/users/{id}")
    public ResponseEntity<UserProfileDTO> patchUser(
            @PathVariable("id") long authUserId,
            @Valid @RequestBody RegisterRequestDTO dto) {
        
        UserProfileDTO updatedProfile = authService.patchUser(dto, authUserId);
        return ResponseEntity.ok(updatedProfile);
    }
	
    @PreAuthorize("hasRole('ADMIN')")
	  @GetMapping("/users/{id}")
	    public ResponseEntity<ResponseDTO> getUserByAuthUserId(   
	            @Parameter(description = "User ID", required = true, example = "123")@PathVariable Long id) {
	        ResponseDTO user = authService.getUserByAuthUserId(id);
	        return ResponseEntity.ok(user);
	    }
	

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO  request) {
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
			////// new
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
			String role = authorities.iterator().next().getAuthority(); // مثلاً "ROLE_USER" یا "ROLE_ADMIN"
			System.out.println("roleeeee   " + role);
			////// new
			String token = jwtService.generateToken(request.getEmail(), role);
			return ResponseEntity.ok(new AuthResponseDTO(token));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(401).body("Invalid credentials");
		}

	}


	@PostMapping("/refresh")
	public ResponseEntity<?> refreshExpiredToken(@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.substring(7);
		if (jwtService.validateToken(token)) {
			String role = jwtService.extractRole(token);
			String email = jwtService.extractUsername(token);
			String newToken = jwtService.generateToken(email, role); // no role needed
			return ResponseEntity.ok(new AuthResponseDTO(newToken));
		} else {
			return ResponseEntity.status(401).body("Invalid token");
		}
	}

	
}

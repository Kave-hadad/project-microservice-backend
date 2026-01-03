package com.example.user_service.controller;



import java.util.List;
// برای ساختن پروژه اول کلاس انتیتی رو میسازیم سپس ریپازستوری و سپس کلاس سرویس و متودهایش را مینویسیم و در پایان کنترلر

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.UserPatchDTO;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);	

	

	private final UserService userService;


	
	@Operation( summary = "Get all user profiles", description = "Fetches a list of all user profiles available in the system." )
	@GetMapping
	public ResponseEntity<List<UserProfileDTO>> getAllUsersProfiles(){
		 logger.info("Fetching  AllUsersProfiles ");
		return ResponseEntity.ok(userService.getAllUsers());		
	}
	

	
	   @Operation( summary = "Get user by ID",description = "Fetches a single user profile by its unique AuthUserId." )
	 @GetMapping("/{id}")
	 public ResponseEntity<UserProfileDTO> getUserByAuthUserId(@PathVariable Long id) {
		    logger.info("Fetching user by AuthUserId: {}", id);
		return ResponseEntity.ok(userService.getUserByAuthUserId(id));			 
	 }
	 
	

	   
	   @Operation(summary = "Search users by family name",description = "Fetches user profiles filtered by family name.")
	 @GetMapping("/search")
	 public ResponseEntity<List<UserProfileDTO>> getUserByFamily(@RequestParam String family) {
	
		    logger.info("Fetching userProfile by family: {}", family);
		return ResponseEntity.ok(userService.getUserByFamily(family));			 
	 }
	 
	 
	   @Operation( summary = "Create new user profile",description = "Creates a new user profile with provided information.")

	 @PostMapping
	 public ResponseEntity<UserProfileDTO> createUserProfile(@Valid @RequestBody UserProfileDTO userProfileDto)

	 {
		 System.out.println("im in createUserProfile--- ");
		 UserProfileDTO dtoReturn =  userService.createUser(userProfileDto);
		logger.info("Admin creating user by this path /api/users with AuthUserId: {}", userProfileDto.getAuthUserId());
		return ResponseEntity.ok(dtoReturn);
	
   	}

	   @Operation(summary = "Delete user profile by ID",description = "Deletes a user profile by its unique AuthUserId.")
	   
	 @DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id)
	 {
		 logger.warn("Deleting user by AuthUserId: {}", id); 
		userService.deleteUserProfile(id);
	 }

	   
	   @Operation(summary = "Patch user profile",description = "Updates partial information of a user profile. Accepts UserPatchDTO with fields to update.")

	 @PatchMapping
	 public ResponseEntity<UserProfileDTO> patchUser(@Valid @RequestBody UserPatchDTO updates) {

	     logger.info(" updating  a field of user by AuthUserId: {}", updates.getAuthUserId());
	     UserProfileDTO updatedUser = userService.patchUser( updates);
	     return ResponseEntity.ok(updatedUser);
	 }
 
	 
	 
}

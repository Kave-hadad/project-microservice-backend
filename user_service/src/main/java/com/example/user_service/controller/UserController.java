package com.example.user_service.controller;



import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.UserPatchDTO;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;



@RestController

@RequestMapping("/api/users")

@AllArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);	
//	@GetMapping
//   public List<User> getAllUsers()
//	{
//		System.out.println("UserContoroller");
//	return Arrays.asList( 
//			new User(1L, "Alice", "alice@example.com"),
//            new User(2L, "Bob", "bob@example.com"),
//            new User(3L, "Charlie", "charlie@example.com")
//            );			
//	}
	
	

	

	private final UserService userService;


	@GetMapping

	public ResponseEntity<List<UserProfileDTO>> getAllUsersProfiles(){
		return ResponseEntity.ok(userService.getAllUsers());		
	}
	

	 @GetMapping("/{id}")
	 public ResponseEntity<UserProfileDTO> getUserByAuthUserId(@PathVariable Long id) {

		    logger.info("Fetching user by AuthUserId: {}", id);
		return ResponseEntity.ok(userService.getUserByAuthUserId(id));			 
	 }
	 
	

	 @GetMapping("/search")
	 public ResponseEntity<List<UserProfileDTO>> getUserByFamily(@RequestParam String family) {
	
		    logger.info("Fetching userProfile by family: {}", family);
		return ResponseEntity.ok(userService.getUserByFamily(family));			 
	 }
	 
	 


	 @PostMapping
	 public ResponseEntity<UserProfileDTO> createUserProfile(@Valid @RequestBody UserProfileDTO userProfileDto)

	 {
		 System.out.println("im in createUserProfile--- ");
		 UserProfileDTO dtoReturn =  userService.createUser(userProfileDto);
		logger.info("Admin creating user by this path /api/users with AuthUserId: {}", userProfileDto.getAuthUserId());
		return ResponseEntity.ok(dtoReturn);
	
   	}
//	 @PreAuthorize("hasAnyRole('USER','ADMIN')")
//	 @PutMapping("/{id}")
//	 public ResponseEntity<String> updateUserProfile(@RequestBody UserProfileDTO user)
//	// old  public ResponseEntity<String> updateUserProfile(@PathVariable Long authUserId ,@RequestBody UserProfileDTO user)
//	 {
//	  userService.updateUserProfile(user);	
//	  logger.info("Updating user by AuthUserId: {}",user.getAuthUserId());
//      return ResponseEntity.ok("usersProfile updated  successfully "); 
//	 }
//	 
	 

	 @DeleteMapping("/{id}")
	public void deleteUser(@PathVariable Long id)
	 {
		 logger.warn("Deleting user by AuthUserId: {}", id); 
		userService.deleteUserProfile(id);
	 }

	 //////////// have to improve 

//	 @GetMapping("/search")
//	  public ResponseEntity<UserDTO> searchUserByName(@RequestParam String name) 
//	 {	 
//		   logger.debug("Searching user by name: {}", name);
//	        return  ResponseEntity.ok( userService.getUserByName(name));
//	    }
	 /////////////////////////////////////////////////////////
	 ///

	 @PatchMapping
	 public ResponseEntity<UserProfileDTO> patchUser(@Valid @RequestBody UserPatchDTO updates) {

	     logger.info(" updating  a field of user by AuthUserId: {}", updates.getAuthUserId());
	     UserProfileDTO updatedUser = userService.patchUser( updates);
	     return ResponseEntity.ok(updatedUser);
	 }
 
	 
	 
}

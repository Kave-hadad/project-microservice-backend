package com.example.auth_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.auth_service.dto.UserProfileDTO;

@FeignClient(name = "userservice", url = "${user-service.url}")
public interface AuthServiceClient {

	@GetMapping("/api/users/search")
	ResponseEntity<List<UserProfileDTO>> getUserByFamily(@RequestParam String family);

	@GetMapping("/api/users")
	ResponseEntity<List<UserProfileDTO>> getAllUsersProfiles();

	@PostMapping("/api/users")
	ResponseEntity<UserProfileDTO> createUserProfile(@RequestBody UserProfileDTO userProfileDto);

	@GetMapping("/api/users/{id}")
	ResponseEntity<UserProfileDTO> getUserByAuthUserId(@PathVariable("id") Long authUserId);

	@DeleteMapping("/api/users/{id}")
	ResponseEntity<Void> deleteUser(@PathVariable("id") Long authUserId);

	@PatchMapping("/api/users")
	ResponseEntity<UserProfileDTO> patchUser(@RequestBody UserProfileDTO updates);

}

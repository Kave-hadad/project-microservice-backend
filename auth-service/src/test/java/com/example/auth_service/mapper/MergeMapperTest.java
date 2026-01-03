package com.example.auth_service.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.auth_service.dto.ResponseDTO;
import com.example.auth_service.dto.UserProfileDTO;
import com.example.auth_service.entity.UserCredential;

public class MergeMapperTest {

	private final MergeMapper mergeMapper = new MergeMapper();

	@Test
	void shouldUserProfileDTOAndUserCredentialMerge() {
		// arrange
		UserProfileDTO profile = new UserProfileDTO();
		profile.setName("Jack");
		profile.setFamily("Simon");
		profile.setAddress("Tehran");
		profile.setPhone("09121111111");
		profile.setBirthDate(LocalDate.parse("2010-01-01"));
		UserCredential credential = new UserCredential();
		credential.setCreatedAt(LocalDateTime.parse("2020-01-01T00:00:00"));
		credential.setEmail("test@gmail.com");
		credential.setRole("ROLE_ADMIN");

		// act
		ResponseDTO response = mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential);
		
		//ASSERT
		Assertions.assertNotNull(response);
		Assertions.assertEquals("Jack", response.getName());
		Assertions.assertEquals("Simon", response.getFamily());
		Assertions.assertEquals("09121111111", response.getPhone());	
		Assertions.assertEquals("ROLE_ADMIN", response.getRole());
		Assertions.assertEquals(LocalDateTime.parse("2020-01-01T00:00:00"), response.getCreatedAt());
		Assertions.assertEquals("Tehran", response.getAddress());
		Assertions.assertEquals(LocalDate.parse("2010-01-01"), response.getBirthDate());
		Assertions.assertEquals("test@gmail.com", response.getEmail());

	}

}

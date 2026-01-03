package com.example.auth_service.mapper;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.dto.UserProfileDTO;
import com.example.auth_service.entity.UserCredential;

public class DtoUserMapperTest {
	
	private final DtoUserMapper mapper = new DtoUserMapper();
	
	
	
	@Test
	void shouldmapRegisterDtoToUserCredential() {
	//arrange
	RegisterRequestDTO dto = new RegisterRequestDTO();
	dto.setEmail("test@gmail.com");
	//act
	UserCredential userCredential = mapper.dtoToCredential(dto);
	//assert
	Assertions.assertNotNull(userCredential);
	Assertions.assertEquals("test@gmail.com", userCredential.getEmail());


	
	}

	@Test
	void shouldMapRegisterRequestDTOToUserProfileDTO ()
{
		
		//arrange
		RegisterRequestDTO dto = new RegisterRequestDTO();
		long auth_user_id = 5L;
		dto.setName("Jack");
		dto.setFamily("Simon");
		dto.setAddress("Tehran");
		dto.setPhone("09121111111");
		dto.setBirthDate(LocalDate.parse("2010-01-01"));
		//act
		UserProfileDTO profileDto = mapper.dtoToUserProfileDto(dto, auth_user_id);
		//Assert
		
		Assertions.assertNotNull(profileDto);
		Assertions.assertEquals(5L, profileDto.getAuthUserId());
		Assertions.assertEquals("Jack", profileDto.getName());
		Assertions.assertEquals("Simon", profileDto.getFamily());
	    Assertions.assertEquals("09121111111", profileDto.getPhone());
	    Assertions.assertEquals("Tehran", profileDto.getAddress());
	Assertions.assertEquals(LocalDate.parse("2010-01-01"), profileDto.getBirthDate());
		
	}
	
	@Test
	void shouldThrowExceptionWhenDtoIsNull() {
		Assertions.assertThrows(NullPointerException.class, () -> {
			mapper.dtoToCredential(null);
			mapper.dtoToUserProfileDto(null, 0);
		});
	}
	
}

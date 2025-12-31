package com.example.user_service.mapper;

import org.springframework.stereotype.Component;

import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.entity.UserProfile;

@Component
public class UserDtoMapper {

	public UserProfileDTO userToDto(UserProfile user) {
		UserProfileDTO dto = new UserProfileDTO();
		dto.setAuthUserId(user.getAuthUserId());
		dto.setName(user.getName());
		dto.setFamily(user.getFamily());
		dto.setPhone(user.getPhone());
		dto.setAddress(user.getAddress());
		dto.setBirthDate(user.getBirthDate());


		return dto;
	}
}

//public class UserConverter {
//    public static User toEntity(UserDTO dto) {
//        User user = new User();
//        user.setName(dto.getName());
//        user.setEmail(dto.getEmail());
//        return user;
//    }
//

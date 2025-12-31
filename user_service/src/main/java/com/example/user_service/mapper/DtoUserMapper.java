package com.example.user_service.mapper;

import org.springframework.stereotype.Component;

import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.entity.UserProfile;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DtoUserMapper {

	public UserProfile dtoToEntity(UserProfileDTO userDto)

	{
		UserProfile entity = new UserProfile();

		entity.setName(userDto.getName());
		entity.setAuthUserId(userDto.getAuthUserId());
		entity.setBirthDate(userDto.getBirthDate());
		entity.setFamily(userDto.getFamily());
		entity.setPhone(userDto.getPhone());
		entity.setAddress(userDto.getAddress());
		return entity;

	}

	public UserProfile updateEntityFromDto(UserProfile entity, UserProfileDTO userDto) {

		entity.setName(userDto.getName());
		entity.setAddress(userDto.getAddress());
		entity.setBirthDate(userDto.getBirthDate());
		entity.setFamily(userDto.getFamily());
		entity.setPhone(userDto.getPhone());
		return entity;
	}

}

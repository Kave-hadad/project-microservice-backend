package com.example.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.dto.UserProfileDTO;
import com.example.auth_service.entity.UserCredential;

@Component
public class DtoUserMapper {
	
    public UserCredential dtoToCredential(RegisterRequestDTO dto) {
        UserCredential credential = new UserCredential();
        credential.setEmail(dto.getEmail());

        return credential;
    }

    public UserProfileDTO dtoToUserProfileDto(RegisterRequestDTO dto,long auth_user_id ) {
        UserProfileDTO profile = new UserProfileDTO();
        profile.setAuthUserId(auth_user_id);
        profile.setName(dto.getName());
        profile.setFamily(dto.getFamily());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setBirthDate(dto.getBirthDate());
        return profile;
    }

	}



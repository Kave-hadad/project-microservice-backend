package com.example.auth_service.mapper;

import org.springframework.stereotype.Component;

import com.example.auth_service.dto.ResponseDTO;
import com.example.auth_service.dto.UserProfileDTO;
import com.example.auth_service.entity.UserCredential;

@Component
public class MergeMapper {

	
	public ResponseDTO dtoProfileAndCredentialToResponseDto(UserProfileDTO profile , UserCredential credential) {
		ResponseDTO response = new ResponseDTO();
		response.setName(profile.getName());  
		response.setEmail(credential.getEmail());   
		response.setRole(credential.getRole());    
		response.setEnabled(credential.isEnabled());  
		response.setCreatedAt(credential.getCreatedAt());
		response.setFamily( profile.getFamily());   
		response.setPhone( profile.getPhone());  
		response.setAddress(profile.getAddress());   
		response.setBirthDate( profile.getBirthDate()); 
		response.setUserId(credential.getId());
		
		return response;
	}
	
}

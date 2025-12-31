package com.example.auth_service.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.auth_service.entity.UserCredential;
import com.example.auth_service.repository.CredentialRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {


	private final CredentialRepository credentialRepository ;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserCredential userCredential = credentialRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
			
		return new CustomUserDetails(userCredential);
	}

	

	
	
	
}

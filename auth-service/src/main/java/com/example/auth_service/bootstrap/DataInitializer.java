package com.example.auth_service.bootstrap;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.entity.UserCredential;
import com.example.auth_service.repository.CredentialRepository;
import com.example.auth_service.service.AuthService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

	
     private final CredentialRepository userRepository;

	private final AuthService authService;
    @Override
    public void run(String... args) throws Exception {
    	
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
        	RegisterRequestDTO dto = new RegisterRequestDTO();
        	dto.setName("Kave");
        	dto.setFamily("Hadad");
        	dto.setPassword("123456"); 
        	dto.setEmail("admin@gmail.com");
        	dto.setAddress("Ahwaz");
        	dto.setBirthDate(LocalDate.parse("2020-01-01"));
        	dto.setPhone("0991111111");
        	 UserCredential admin = 	authService.createCredentialByAdmin(dto);
        	authService.createUserProfile(dto, admin.getId());
        ////////////////////
       
        	RegisterRequestDTO dtouser = new RegisterRequestDTO();
        	dtouser.setName("user");
        	dtouser.setFamily("Hadad");
        	dtouser.setPassword("123456"); 
        	dtouser.setEmail("user@gmail.com");
        	dtouser.setAddress("Ahwaz");
        	dtouser.setBirthDate(LocalDate.parse("2020-01-01"));
        	dtouser.setPhone("0991111111");
        	UserCredential user = authService.createCredential(dtouser);
        	authService.createUserProfile(dtouser, user.getId());
            System.out.println("✅ Admin and user created");
        } else {
            System.out.println("ℹ️ Admin user already exists");
        }
    }
}

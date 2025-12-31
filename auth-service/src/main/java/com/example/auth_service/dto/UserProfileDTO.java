package com.example.auth_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

 	private Long authUserId;

	private String name;

	private String family;

	private String email;

	private String phone;
	private String address;
	private LocalDate birthDate;
	private LocalDateTime createdAt;
}

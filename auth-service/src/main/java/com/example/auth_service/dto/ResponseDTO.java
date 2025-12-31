package com.example.auth_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

	private String name;
	private String password;
	private String email;
	private String role;
	private boolean enabled;
	private LocalDateTime createdAt;
	private String family;
	private String phone;
	private String address;
	private LocalDate birthDate;
	private long userId;

}

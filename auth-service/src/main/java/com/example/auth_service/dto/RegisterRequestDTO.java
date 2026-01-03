package com.example.auth_service.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

	@NotBlank(message = "name is required")
	@Size(min = 2, max = 10, message = "name should be between 2 and 10 characters")
	private String name;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "family is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Family can only contain letters")
	@Size(min = 2, max = 10, message = "Family should be between 2 and 10 characters")
	private String family;
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10–15 digits")
	private String phone;
	@Size(max = 200, message = "Address must be less than 200 characters")
	private String address;
	@NotNull(message = "Birth date is required")
	@Past(message = "Birth date must be in the past")
	private LocalDate birthDate;

}

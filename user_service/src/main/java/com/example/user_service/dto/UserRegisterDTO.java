package com.example.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
	

    @NotBlank(message = "Name is required")

	@Pattern(regexp = "^[a-zA-Z]+$", message = "Name can only contain letters")
	@Size(min = 2, max =10, message = "Name should be between 2 and 10 characters")
    private String name;
    @NotBlank(message = "Name is required")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Family can only contain letters")
	@Size(min = 2, max =10, message = "Family should be between 2 and 10 characters")
    private String family;
    
     @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
     private String email;
     @NotBlank(message = "Password is required")
     @Size(min = 6, message = "Password must be at least 6 characters")
     private String password;
     @NotBlank(message = "Username is required")
     @Size(min = 2, max =10, message = "username should be between 2 and 10 characters")
     private String username;
}

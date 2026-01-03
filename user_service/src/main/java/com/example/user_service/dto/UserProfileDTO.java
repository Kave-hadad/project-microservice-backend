package com.example.user_service.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
	
	@NotNull(message = "auth_user_id is required")
	private long authUserId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Family is required")
    private String family;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

}

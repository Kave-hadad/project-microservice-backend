package com.example.user_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDTO {

    @NotBlank(message = "Name is required")

    private String name;
    @NotBlank(message = "family is required")
//	@Pattern(regexp = "^[a-zA-Z]+$", message = "Family can only contain letters")
    private String family;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;
///@NotNull
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;
	@NotNull(message = "auth_user_id is required")
	private long authUserId;

}

package com.example.auth_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.dto.ResponseDTO;
import com.example.auth_service.dto.UserProfileDTO;
import com.example.auth_service.entity.UserCredential;
import com.example.auth_service.exception.AuthServiceUnavailableException;
import com.example.auth_service.exception.DuplicateUserException;
import com.example.auth_service.exception.UserNotFoundException;
import com.example.auth_service.feign.AuthServiceClient;
import com.example.auth_service.mapper.DtoUserMapper;
import com.example.auth_service.mapper.MergeMapper;
import com.example.auth_service.repository.CredentialRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private DtoUserMapper dtoUserMapper;
	@Mock
	private MergeMapper mergeMapper;
	@Mock
	private CredentialRepository credentialRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private AuthServiceClient authServiceClient;
	@InjectMocks
	private AuthService authService;

	// createCredential
	@Test
	void shouldCreateCredentialSuccessfully_whenCreateCredentialCalled() {

		// arrange
		RegisterRequestDTO dto = new RegisterRequestDTO();
		dto.setEmail("test@gmail.com");

		dto.setPassword("123456");
		UserCredential credential = new UserCredential();
		credential.setEmail(dto.getEmail());
		when(credentialRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(dtoUserMapper.dtoToCredential(dto)).thenReturn(credential);
		when(passwordEncoder.encode(dto.getPassword())).thenReturn("encode123456");

		when(credentialRepository.save(any(UserCredential.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// act
		UserCredential saved = authService.createCredential(dto);

		// assert

		assertThat(saved).isNotNull();
		assertThat(saved.getEmail()).isEqualTo("test@gmail.com");
		assertThat(saved.getPassword()).isEqualTo("encode123456");
		assertThat(saved.getRole()).isEqualTo("ROLE_USER");
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.isEnabled()).isTrue();

		verify(credentialRepository).save(saved);
	}

	// createCredential
	@Test
	void shouldThrowExceptionWhenDuplicateUserException_whenCreateCredentialFails() {
		RegisterRequestDTO dto = new RegisterRequestDTO();
		UserCredential userCredential = new UserCredential();
		dto.setEmail("test@gmail.com");
		when(credentialRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(userCredential));
		// assert and act
		assertThatThrownBy(() -> authService.createCredential(dto)).isInstanceOf(DuplicateUserException.class).hasMessageContaining("A user with this email is already registered");
	}

	// createCredentialByAdmin
	@Test
	void shouldCreateCreadentialByAdminSuccessfully_whenCreateCredentialByAdminCalled() {
		// arrange
		RegisterRequestDTO dto = new RegisterRequestDTO();
		dto.setEmail("admin2@gmail.com");
		dto.setPassword("123456");
		UserCredential credential = new UserCredential();
		credential.setEmail(dto.getEmail());
		when(credentialRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(dtoUserMapper.dtoToCredential(dto)).thenReturn(credential);
		when(passwordEncoder.encode(dto.getPassword())).thenReturn("encode123456");
		when(credentialRepository.save(any(UserCredential.class))).thenAnswer(invocation -> invocation.getArgument(0));
		// act
		UserCredential saved = authService.createCredentialByAdmin(dto);
		// assert
		assertThat(saved).isNotNull();
		assertThat(saved.getEmail()).isEqualTo("admin2@gmail.com");
		assertThat(saved.getPassword()).isEqualTo("encode123456");
		assertThat(saved.getRole()).isEqualTo("ROLE_ADMIN");
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.isEnabled()).isTrue();
	}

	// createCredentialByAdmin
	@Test
	void shouldThrowExceptionWhenDuplicateUserException_whencreateCredentialByAdminFails() {
		RegisterRequestDTO dto = new RegisterRequestDTO();
		UserCredential userCredential = new UserCredential();
		dto.setEmail("test@gmail.com");
		when(credentialRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(userCredential));
		// assert and act
		assertThatThrownBy(() -> authService.createCredentialByAdmin(dto)).isInstanceOf(DuplicateUserException.class).hasMessageContaining("A user with this email is already registered");
	}

	// createUserProfile
	@Test
	void shouldCreateUserUserProfileSuccessfully_whenCreateUserProfileCalled() {
		// arrange
		RegisterRequestDTO dto = new RegisterRequestDTO();
		dto.setName("Nick");
		long authUserId = 1L;

		UserProfileDTO userProfileDto = new UserProfileDTO();
		userProfileDto.setName("Nick");
		userProfileDto.setAuthUserId(authUserId);
		when(dtoUserMapper.dtoToUserProfileDto(dto, authUserId)).thenReturn(userProfileDto);
		when(authServiceClient.createUserProfile(userProfileDto)).thenReturn(ResponseEntity.ok(userProfileDto));
		// act
		UserProfileDTO result = authService.createUserProfile(dto, authUserId);

		// assert
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("Nick");
		assertThat(result.getAuthUserId()).isEqualTo(authUserId);
	}

	// createUserProfile
	@Test
	void shouldThrowExceptionWhenAuthServiceUnavailableException_whenCreateUserProfileFails() {
		// Arrange
		RegisterRequestDTO dto = new RegisterRequestDTO();
		long authUserId = 10L;
		UserProfileDTO profileDto = new UserProfileDTO();
		when(dtoUserMapper.dtoToUserProfileDto(dto, authUserId)).thenReturn(profileDto);
		when(authServiceClient.createUserProfile(profileDto)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
		// Act & Assert
		assertThatThrownBy(() -> authService.createUserProfile(dto, authUserId)).isInstanceOf(AuthServiceUnavailableException.class).hasMessageContaining("Error creating profile");
	}

	// createUserProfile
	@Test
	void shouldThrowExceptionWhenResponseBodyIsNull_whenCreateUserProfileFails() {
		//
		RegisterRequestDTO dto = new RegisterRequestDTO();
		long authUserId = 10L;
		UserProfileDTO profileDto = new UserProfileDTO();
		when(dtoUserMapper.dtoToUserProfileDto(dto, authUserId)).thenReturn(profileDto);
		when(authServiceClient.createUserProfile(profileDto)).thenReturn(ResponseEntity.ok(null));
		// Act & Assert
		assertThatThrownBy(() -> authService.createUserProfile(dto, authUserId)).isInstanceOf(AuthServiceUnavailableException.class).hasMessageContaining("Error creating profile");

	}


	// getAllUsers
	@Test
	void shouldReturnMergedUsersSuccessfully_whenGetAllUsersCalled() {
		// Arrange
		UserCredential credential = new UserCredential();
		credential.setId(1L);
		credential.setEmail("test@gmail.com");
		credential.setRole("ROLE_USER");
		credential.setEnabled(true);
		credential.setCreatedAt(LocalDateTime.now());

		UserProfileDTO profile = new UserProfileDTO();
		profile.setAuthUserId(1L);
		profile.setName("Nick");
		profile.setFamily("Smith");

		ResponseDTO merged = new ResponseDTO();
		merged.setEmail("test@gmail.com");
		merged.setName("Nick");
		merged.setFamily("Smith");

		when(credentialRepository.findAll()).thenReturn(Arrays.asList(credential));
		when(authServiceClient.getAllUsersProfiles()).thenReturn(ResponseEntity.ok(Arrays.asList(profile)));
		when(mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential)).thenReturn(merged);

		// Act
		List<ResponseDTO> result = authService.getAllUsers();


		// Assert
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getEmail()).isEqualTo("test@gmail.com");
		assertThat(result.get(0).getName()).isEqualTo("Nick");
	}

	// getAllUsers

	@Test
	void shouldThrowExceptionWhenauthServiceUnavailable_whenGetAllUsersFails() {
		// Arrange
		when(credentialRepository.findAll()).thenReturn(Arrays.asList());
		when(authServiceClient.getAllUsersProfiles()).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

		// Act & Assert
		assertThatThrownBy(() -> authService.getAllUsers()).isInstanceOf(AuthServiceUnavailableException.class).hasMessageContaining("Error receiving user profiles list");
	}

	// getAllUsers

	@Test
	void shouldThrowExceptionWhenResponseBodyIsNull_whenGetAllUsersFails() {
		// Arrange
		when(credentialRepository.findAll()).thenReturn(Arrays.asList());
		when(authServiceClient.getAllUsersProfiles()).thenReturn(ResponseEntity.ok(null));
		// Act & Assert
		assertThatThrownBy(() -> authService.getAllUsers()).isInstanceOf(AuthServiceUnavailableException.class);
	}

	// getUserByFamily
	@Test
	void shouldReturnMergedUsersSuccessfully_whenGetUserByFamilyCalled() {
		// arrange
		String family = "Smith";
		UserProfileDTO profile = new UserProfileDTO();
		profile.setAuthUserId(1L);
		profile.setName("Nick");
		profile.setFamily("Smith");
		UserCredential credential = new UserCredential();
		credential.setId(1L);
		credential.setEmail("test@gmail.com");
		credential.setRole("ROLE_USER");
		credential.setEnabled(true);
		credential.setCreatedAt(LocalDateTime.now());
		ResponseDTO merged = new ResponseDTO();
		merged.setEmail("test@gmail.com");
		merged.setName("Nick");
		merged.setFamily("Smith");

		when(authServiceClient.getUserByFamily(family)).thenReturn(ResponseEntity.ok(Arrays.asList(profile)));
		when(credentialRepository.findByIdIn(Arrays.asList(1L))).thenReturn(Arrays.asList(credential));
		when(mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential)).thenReturn(merged);
		// act
		List<ResponseDTO> result = authService.getUserByFamily(family);
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getEmail()).isEqualTo("test@gmail.com");
		assertThat(result.get(0).getName()).isEqualTo("Nick");
		assertThat(result.get(0).getFamily()).isEqualTo("Smith");

	}

	// getUserByFamily
	@Test
	void shouldThrowExceptionResponseBodyIsNull_whenGetUserByFamilyFails() {
		String family = "Smith";
		when(authServiceClient.getUserByFamily(family)).thenReturn(ResponseEntity.ok(null));

		// Act & Assert
		assertThatThrownBy(() -> authService.getUserByFamily(family)).isInstanceOf(AuthServiceUnavailableException.class);
	}

	// getUserByFamily
	@Test
	void shouldThrowExceptionWhenauthServiceUnavailable_whenGetUserByFamilyFails() {
		// Arrange
		String family = "Smith";
		when(authServiceClient.getUserByFamily(family)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

		// Act & Assert
		assertThatThrownBy(() -> authService.getUserByFamily(family)).isInstanceOf(AuthServiceUnavailableException.class).hasMessageContaining("Error receiving profiles");
	}

	// getUserByAuthUserId
	@Test
	void shouldReturnUserByAuthUserIdSuccessfully_whengetUserByAuthUserIdCalled() {
		// Arrange
		long id = 1L;

		UserCredential credential = new UserCredential();
		credential.setId(id);
		credential.setEmail("test@gmail.com");
		credential.setRole("ROLE_USER");
		credential.setEnabled(true);
		credential.setCreatedAt(LocalDateTime.now());

		UserProfileDTO profile = new UserProfileDTO();
		profile.setAuthUserId(id);
		profile.setName("Nick");
		profile.setFamily("Smith");

		ResponseDTO merged = new ResponseDTO();
		merged.setEmail("test@gmail.com");
		merged.setName("Nick");
		merged.setFamily("Smith");

		when(credentialRepository.findById(id)).thenReturn(Optional.of(credential));
		when(authServiceClient.getUserByAuthUserId(id)).thenReturn(ResponseEntity.ok(profile));
		when(mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential)).thenReturn(merged);

		// Act
		ResponseDTO result = authService.getUserByAuthUserId(id);

		// Assert
		assertThat(result.getEmail()).isEqualTo("test@gmail.com");
		assertThat(result.getName()).isEqualTo("Nick");
		assertThat(result.getFamily()).isEqualTo("Smith");
	}

	// getUserByAuthUserId
	@Test
	void shouldThrowExceptionWhenauthServiceUnavailable_whengetUserByAuthUserIdّFails() {
		// Arrange
		long id = 1L;
		UserCredential credential = new UserCredential();
		credential.setId(id);
		when(credentialRepository.findById(id)).thenReturn(Optional.of(credential));
		when(authServiceClient.getUserByAuthUserId(id)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

		// Act & Assert
		assertThatThrownBy(() -> authService.getUserByAuthUserId(id)).isInstanceOf(AuthServiceUnavailableException.class).hasMessageContaining("Error receiving user profile");
	}

	// getUserByAuthUserId
	@Test
	void shouldThrowExceptionWhenResponseBodyIsNull_whengetUserByAuthUserIdّFails() {
		// Arrange
		long id = 1L;
		UserCredential credential = new UserCredential();
		credential.setId(id);
		when(credentialRepository.findById(id)).thenReturn(Optional.of(credential));
		when(authServiceClient.getUserByAuthUserId(id)).thenReturn(ResponseEntity.ok(null));

		// Act & Assert
		assertThatThrownBy(() -> authService.getUserByAuthUserId(id)).isInstanceOf(AuthServiceUnavailableException.class);
	}
	
	// getUserByAuthUserId
	@Test
	void shouldThrowExceptionUserNotFoundException_whengetUserByAuthUserIdّFails() {
		// Arrange
		long id = 1L;
		UserCredential credential = new UserCredential();
		credential.setId(id);
		when(credentialRepository.findById(id)).thenReturn(Optional.empty());
		//when(authServiceClient.getUserByAuthUserId(id)).thenReturn(ResponseEntity.ok(null));

		// Act & Assert
		assertThatThrownBy(() -> authService.getUserByAuthUserId(id)).isInstanceOf(UserNotFoundException.class).hasMessageContaining("was not found in credentialRepository.");
	}
	
	

	// deleteUserById
	@Test
	void shouldDeleteUserByIdSuccessfully_whenDeleteUserByIdCalled() {

		// arrange

		long id = 1L;
		when(credentialRepository.existsById(id)).thenReturn(true);
		when(authServiceClient.deleteUser(id)).thenReturn(ResponseEntity.ok().build());
		// act
		authService.deleteUserById(id);
		// assert
		verify(credentialRepository).deleteById(id);

	}

	// deleteUserById
	@Test
	void shouldThrowUserNotFoundException_whenDeleteUserByIdFails() {
		// arrange
		long id = 1L;
		when(credentialRepository.existsById(id)).thenReturn(false);
		// act and assert
		assertThatThrownBy(() -> authService.deleteUserById(id)).isInstanceOf(UserNotFoundException.class).hasMessageContaining("was not found in credentialRepository.");
	}

	// deleteUserById
	@Test
	void shouldThrowAuthServiceUnavailableException_whenDeleteUserByIdFails() {
		// arrange
		long id = 1L;
		when(credentialRepository.existsById(id)).thenReturn(true);
		when(authServiceClient.deleteUser(id))
				.thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
		// assert and act
		assertThatThrownBy(() -> authService.deleteUserById(id)).isInstanceOf(AuthServiceUnavailableException.class).hasMessageContaining("Error deleting user profile");
	}

	// patchUser
	@Test
	void shouldPatchUserSuccessfully_whenDeleteUserByIdCalled() {
		// Arrange
		long authUserId = 1L;

		RegisterRequestDTO dto = new RegisterRequestDTO();
		dto.setPassword("newPassword");

		UserProfileDTO profile = new UserProfileDTO();
		profile.setAuthUserId(authUserId);
		profile.setName("Nick");
		profile.setFamily("Smith");

		UserCredential credential = new UserCredential();
		credential.setId(authUserId);
		credential.setEmail("test@gmail.com");
		credential.setPassword("oldPassword");

		when(dtoUserMapper.dtoToUserProfileDto(dto, authUserId)).thenReturn(profile);
		when(authServiceClient.patchUser(profile)).thenReturn(ResponseEntity.ok(profile));
		when(credentialRepository.findById(authUserId)).thenReturn(Optional.of(credential));
		when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

		// Act
		UserProfileDTO result = authService.patchUser(dto, authUserId);

		// Assert
		assertThat(result.getName()).isEqualTo("Nick");
		assertThat(result.getFamily()).isEqualTo("Smith");
		verify(credentialRepository).save(any(UserCredential.class));
		assertThat(credential.getPassword()).isEqualTo("encodedPassword");
	}

	// patchUser
	@Test
	void shouldThrowExceptionWhenauthServiceUnavailable_whenDeleteUserByIdFails() {
		// Arrange
		long authUserId = 1L;
		RegisterRequestDTO dto = new RegisterRequestDTO();

		UserProfileDTO profile = new UserProfileDTO();
		profile.setAuthUserId(authUserId);

		when(dtoUserMapper.dtoToUserProfileDto(dto, authUserId)).thenReturn(profile);
		when(authServiceClient.patchUser(profile)).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

		// Act & Assert
		assertThatThrownBy(() -> authService.patchUser(dto, authUserId)).isInstanceOf(AuthServiceUnavailableException.class).hasMessageContaining("Error updating user profile");
	}

	// patchUser
	@Test
	void shouldThrowExceptionWhenResponseBodyIsNull_whenDeleteUserByIdFails() {
		// Arrange
		long authUserId = 1L;
		RegisterRequestDTO dto = new RegisterRequestDTO();

		UserProfileDTO profile = new UserProfileDTO();
		profile.setAuthUserId(authUserId);

		when(dtoUserMapper.dtoToUserProfileDto(dto, authUserId)).thenReturn(profile);
		when(authServiceClient.patchUser(profile)).thenReturn(ResponseEntity.ok(null));

		// Act & Assert
		assertThatThrownBy(() -> authService.patchUser(dto, authUserId)).isInstanceOf(AuthServiceUnavailableException.class);
	}

}

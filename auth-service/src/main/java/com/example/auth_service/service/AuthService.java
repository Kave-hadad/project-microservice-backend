package com.example.auth_service.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final DtoUserMapper dtoUserMapper;
    private final MergeMapper mergeMapper;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthServiceClient authServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public UserCredential createCredential(RegisterRequestDTO dto) {
        logger.debug("Attempting to create credential for email {}", dto.getEmail());
        if (credentialRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("Duplicate user detected with email {}", dto.getEmail());
            throw new DuplicateUserException("A user with this email is already registered");
        }
        UserCredential credential = dtoUserMapper.dtoToCredential(dto);
        credential.setPassword(passwordEncoder.encode(dto.getPassword()));
        credential.setRole("ROLE_USER");
        credential.setCreatedAt(LocalDateTime.now());
        credential.setEnabled(true);
        logger.info("User credential created for email {}", dto.getEmail());
        return credentialRepository.save(credential);
    }

    public UserCredential createCredentialByAdmin(RegisterRequestDTO dto) {
        logger.debug("Admin creating credential for email {}", dto.getEmail());
        if (credentialRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("Duplicate user detected with email {}", dto.getEmail());
            throw new DuplicateUserException("A user with this email is already registered");
        }
        UserCredential credential = dtoUserMapper.dtoToCredential(dto);
        credential.setPassword(passwordEncoder.encode(dto.getPassword()));
        credential.setRole("ROLE_ADMIN");
        credential.setCreatedAt(LocalDateTime.now());
        credential.setEnabled(true);
        logger.info("Admin credential created for email {}", dto.getEmail());
        return credentialRepository.save(credential);
    }

    public UserProfileDTO createUserProfile(RegisterRequestDTO dto, long authUserId) {
        logger.debug("Creating user profile for authUserId {}", authUserId);
        UserProfileDTO userProfileDto = dtoUserMapper.dtoToUserProfileDto(dto, authUserId);
        ResponseEntity<UserProfileDTO> response = authServiceClient.createUserProfile(userProfileDto);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            logger.info("Profile created successfully for authUserId {}", authUserId);
            return response.getBody();
        } else {
            logger.error("Failed to create profile for authUserId {}. Status: {}", authUserId, response.getStatusCode());
            throw new AuthServiceUnavailableException("Error creating profile: response status = " + response.getStatusCode());
        }
    }

    public List<ResponseDTO> getAllUsers() {
        logger.debug("Fetching all users...");
        List<UserCredential> usersCredentialsList = credentialRepository.findAll();
        ResponseEntity<List<UserProfileDTO>> response = authServiceClient.getAllUsersProfiles();
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            logger.info("Fetched {} user profiles", response.getBody().size());
            Map<Long, UserProfileDTO> profileMap = response.getBody().stream()
                    .collect(Collectors.toMap(UserProfileDTO::getAuthUserId, Function.identity()));
            List<ResponseDTO> responseList = new ArrayList<>();
            for (UserCredential credential : usersCredentialsList) {
                UserProfileDTO profile = profileMap.get(credential.getId());
                if (profile != null) {
                    responseList.add(mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential));
                }
            }
            return responseList;
        } else {
            logger.error("Failed to fetch user profiles. Status: {}", response.getStatusCode());
            throw new AuthServiceUnavailableException("Error receiving user profiles list: response status = " + response.getStatusCode());
        }
    }

    public List<ResponseDTO> getUserByFamily(String family) {
        logger.debug("Fetching users by family {}", family);
        ResponseEntity<List<UserProfileDTO>> response = authServiceClient.getUserByFamily(family);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            logger.info("Fetched {} profiles for family {}", response.getBody().size(), family);
            Map<Long, UserProfileDTO> profileMap = response.getBody().stream()
                    .collect(Collectors.toMap(UserProfileDTO::getAuthUserId, Function.identity()));
            List<Long> authUserIdList = response.getBody().stream().map(UserProfileDTO::getAuthUserId).toList();
            List<UserCredential> usersCredentialsList = credentialRepository.findByIdIn(authUserIdList);
            List<ResponseDTO> responseList = new ArrayList<>();
            for (UserCredential credential : usersCredentialsList) {
                UserProfileDTO profile = profileMap.get(credential.getId());
                if (profile != null) {
                    responseList.add(mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential));
                }
            }
            return responseList;
        } else {
            logger.error("Failed to fetch profiles for family {}. Status: {}", family, response.getStatusCode());
            throw new AuthServiceUnavailableException("Error receiving profiles: response status = " + response.getStatusCode());
        }
    }

    public ResponseDTO getUserByAuthUserId(long id) {
        logger.debug("Fetching user by id {}", id);
        Optional<UserCredential> userCredential = credentialRepository.findById(id);
        if (userCredential.isEmpty()) {
            logger.error("User with id {} not found in credentialRepository", id);
            throw new UserNotFoundException("User with id " + id + " was not found in credentialRepository.");
        }
        ResponseEntity<UserProfileDTO> userProfileDto = authServiceClient.getUserByAuthUserId(id);
        if (userProfileDto.getStatusCode().is2xxSuccessful() && userProfileDto.getBody() != null) {
            logger.info("Fetched profile for user id {}", id);
            return mergeMapper.dtoProfileAndCredentialToResponseDto(userProfileDto.getBody(), userCredential.get());
        } else {
            logger.error("Failed to fetch profile for user id {}. Status: {}", id, userProfileDto.getStatusCode());
            throw new AuthServiceUnavailableException("Error receiving user profile: response status =" + userProfileDto.getStatusCode());
        }
    }

    @Transactional
    public void deleteUserById(Long id) {
        logger.warn("Attempting to delete user with id {}", id);
        if (!credentialRepository.existsById(id)) {
            logger.error("User with id {} not found in credentialRepository", id);
            throw new UserNotFoundException("User with id " + id + " was not found in credentialRepository.");
        }
        ResponseEntity<Void> response = authServiceClient.deleteUser(id);
        if (response.getStatusCode().is2xxSuccessful()) {
            credentialRepository.deleteById(id);
            logger.info("User with id {} deleted successfully", id);
        } else {
            logger.error("Failed to delete user with id {}. Status: {}", id, response.getStatusCode());
            throw new AuthServiceUnavailableException("Error deleting user profile: response status = " + response.getStatusCode());
        }
    }

    @Transactional
    public UserProfileDTO patchUser(RegisterRequestDTO dto, long authUserId) {
        logger.debug("Patching user with id {}", authUserId);
        UserProfileDTO userProfileDto = dtoUserMapper.dtoToUserProfileDto(dto, authUserId);
        ResponseEntity<UserProfileDTO> response = authServiceClient.patchUser(userProfileDto);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            UserCredential credential = credentialRepository.findById(authUserId)
                    .orElseThrow(() -> {
                        logger.error("User with id {} not found during patch", authUserId);
                        return new UserNotFoundException("User with id " + authUserId + " was not found.");
                    });
            credential.setPassword(passwordEncoder.encode(dto.getPassword()));
            credentialRepository.save(credential);
            logger.info("User with id {} patched successfully", authUserId);
            return response.getBody();
        } else {
            logger.error("Failed to patch user with id {}. Status: {}", authUserId, response.getStatusCode());
            throw new AuthServiceUnavailableException("Error updating user profile: response status = " + response.getStatusCode());
        }
    }
}

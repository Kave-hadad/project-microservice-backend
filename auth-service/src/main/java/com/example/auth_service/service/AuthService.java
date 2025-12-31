package com.example.auth_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final AuthServiceClient AuthServiceClient;

    @Value("${user-service.url}")
    private String userServiceUrl;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    public UserCredential createCredential(RegisterRequestDTO dto) {

        if (credentialRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("User with email {} already exists", dto.getEmail());
            throw new DuplicateUserException("A user with this email is already registered");
        }

        UserCredential credential = dtoUserMapper.dtoToCredential(dto);
        credential.setPassword(passwordEncoder.encode(dto.getPassword()));
        credential.setRole("ROLE_USER");
        credential.setCreatedAt(LocalDateTime.now());
        credential.setEnabled(true);

        logger.info("User with Email {} just registered", dto.getEmail());
        return credentialRepository.save(credential);

    }

    public UserCredential createCredentialByAdmin(RegisterRequestDTO dto) {

        if (credentialRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("User with email {} already exists", dto.getEmail());
            throw new DuplicateUserException("A user with this email is already registered");
        }
        UserCredential credential = dtoUserMapper.dtoToCredential(dto);
        credential.setPassword(passwordEncoder.encode(dto.getPassword()));
        credential.setRole("ROLE_ADMIN");
        credential.setCreatedAt(LocalDateTime.now());
        credential.setEnabled(true);
        logger.info("ADMIN with Email {} just registered", dto.getEmail());
        return credentialRepository.save(credential);

    }
/// before

//    public UserProfileDTO createUserProfile(RegisterRequestDTO dto, long auth_user_id) {
//        UserProfileDTO userProfileDto = dtoUserMapper.dtoToUserProfile(dto, auth_user_id);
//        ResponseEntity<UserProfileDTO> response = AuthServiceClient.createUserProfile(userProfileDto);
//
//        return response.getBody();
//    }

    /// after
//    public UserProfileDTO createUserProfile(RegisterRequestDTO dto, long authUserId) {
//        UserProfileDTO userProfileDto = dtoUserMapper.dtoToUserProfileDto(dto, authUserId);
//
//        try {
//            ResponseEntity<UserProfileDTO> response = AuthServiceClient.createUserProfile(userProfileDto);
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                return response.getBody();
//            } else {
//                throw new UserServiceUnavailableException("Error creating profile: response status = " + response.getStatusCode());
//            }
//
//        } catch (feign.FeignException.BadRequest ex) {
//
//            throw new UserServiceUnavailableException("Invalid input or validation failed: " + ex.contentUTF8());
//
//        } catch (feign.FeignException.NotFound ex) {
//
//            throw new UserServiceUnavailableException("User not found: " + ex.contentUTF8());
//
//        } catch (feign.FeignException.MethodNotAllowed ex) {
//
//            throw new UserServiceUnavailableException("Incorrect HTTP method used.");
//
//        } catch (feign.FeignException.InternalServerError ex) {

//            throw new UserServiceUnavailableException("Internal error occurred in user service.");
//        } catch (Exception ex) {
//            // Any other unexpected error
//            throw new UserServiceUnavailableException("Unexpected error communicating with user service: " + ex.getMessage());
//        }
//    }


    public UserProfileDTO createUserProfile(RegisterRequestDTO dto, long authUserId) {

        UserProfileDTO userProfileDto = dtoUserMapper.dtoToUserProfileDto(dto, authUserId);

        ResponseEntity<UserProfileDTO> response = AuthServiceClient.createUserProfile(userProfileDto);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {


            throw new AuthServiceUnavailableException("Error creating profile: response status = " + response.getStatusCode());
        }
    }



/// before
//    public List<ResponseDTO> getAllUsers() {
//
//     List<UserCredential> usersCredentialsList = credentialRepository.findAll();
////     RestTemplate restTemplate = builder.build();
////     ResponseEntity<UserProfileDTO[]> response =restTemplate.getForEntity(userServiceUrl, UserProfileDTO[].class);
////     UserProfileDTO[] usersArray = response.getBody();
//     ResponseEntity<List<UserProfileDTO>> usersProfileList = AuthServiceClient.getAllUsersProfiles();
//           //Arrays.asList(usersArray);
//
// //  // Create Map from profiles
//     Map<Long, UserProfileDTO> profileMap = usersProfileList.getBody().stream()
//              .collect(Collectors.toMap(
//                  UserProfileDTO::getAuthUserId,
//                  Function.identity()
//              ));
//     List<ResponseDTO> responseList = new ArrayList<>();
//
//     // Merge using for-each
//     for (UserCredential credential : usersCredentialsList) {
//       // profileMap.get(credential.getId()) is the key in the map and returns the profile for each key
//         UserProfileDTO profile = profileMap.get(credential.getId());
//         if (profile != null) {
//             ResponseDTO dto = new ResponseDTO(
//                     profile.getName(),
//                     credential.getPassword(),
//                     credential.getEmail(),
//                     credential.getRole(),
//                     credential.isEnabled(),
//                     credential.getCreatedAt(),
//                     profile.getFamily(),
//                     profile.getPhone(),
//                     profile.getAddress(),
//                     profile.getBirthDate()
//             );
//             responseList.add(dto);
//         }
//     }
//return responseList;
//
//    }

    /// after
//    public List<ResponseDTO> getAllUsers() {
//        List<UserCredential> usersCredentialsList = credentialRepository.findAll();
//
//        try {
//            ResponseEntity<List<UserProfileDTO>> response = AuthServiceClient.getAllUsersProfiles();
//
//            // if successful (2xx)
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//
//                Map<Long, UserProfileDTO> profileMap = response.getBody().stream()
//                        .collect(Collectors.toMap(
//                                UserProfileDTO::getAuthUserId,
//                                Function.identity()
//                        ));
//
//                List<ResponseDTO> responseList = new ArrayList<>();
//
//                // Merge using for-each
//                for (UserCredential credential : usersCredentialsList) {
//                    UserProfileDTO profile = profileMap.get(credential.getId());
//                    if (profile != null) {
//                        ResponseDTO dto = mergeMapper.DtoProfileAndCredentialToResponseDto(profile, credential);
//                        responseList.add(dto);
//                    }
//                }
//                return responseList;
//            } else {
//                throw new UserServiceUnavailableException("Error retrieving profile list: response status = " + response.getStatusCode());
//            }
//
//        } catch (feign.FeignException.BadRequest ex) {
//
//            throw new UserServiceUnavailableException("Invalid input or validation failed: " + ex.contentUTF8());
//
//        } catch (feign.FeignException.NotFound ex) {
//
//            throw new UserServiceUnavailableException("Profiles not found: " + ex.contentUTF8());
//
//        } catch (feign.FeignException.MethodNotAllowed ex) {
//
//            throw new UserServiceUnavailableException("Incorrect HTTP method used.");
//
//        } catch (feign.FeignException.InternalServerError ex) {
//
//            throw new UserServiceUnavailableException("Internal error occurred in user service.");
//        } catch (Exception ex) {
//
//            throw new UserServiceUnavailableException("Unexpected error communicating with user service: " + ex.getMessage());
//        }
//    }


    public List<ResponseDTO> getAllUsers() {
        System.out.println("getAllUsers auth service before credentialRepository");

        List<UserCredential> usersCredentialsList = credentialRepository.findAll();
        System.out.println("getAllUsers auth service after credentialRepository");

        ResponseEntity<List<UserProfileDTO>> response = AuthServiceClient.getAllUsersProfiles();


        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

            Map<Long, UserProfileDTO> profileMap = response.getBody().stream()
                    .collect(Collectors.toMap(UserProfileDTO::getAuthUserId, Function.identity()));

            List<ResponseDTO> responseList = new ArrayList<>();


            for (UserCredential credential : usersCredentialsList) {
                UserProfileDTO profile = profileMap.get(credential.getId());
                if (profile != null) {
                    ResponseDTO dto = mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential);
                    responseList.add(dto);
                }
            }
            return responseList;
        } else {


            throw new AuthServiceUnavailableException(
                    "Error receiving user profiles list: response status = " + response.getStatusCode());

        }
    }

///////
///before
//    public List<ResponseDTO> getUserByFamily(String family) {
//    	ResponseEntity<List<UserProfileDTO>> usersProfileList = AuthServiceClient.getUserByFamily(family);
//
//        Map<Long, UserProfileDTO> profileMap = usersProfileList.getBody().stream()
//                .collect(Collectors.toMap(UserProfileDTO::getAuthUserId, Function.identity()));
//
//        List<Long> AuthUserIdList = new ArrayList<Long>();
//        for(UserProfileDTO userAuthUserId :usersProfileList.getBody()) {
//        	AuthUserIdList.add(userAuthUserId.getAuthUserId());
//        }
//        List<UserCredential> usersCredentialsList = credentialRepository.findByIdIn(AuthUserIdList);

//      // List<UserCredential> usersCredentialsList = credentialRepository.findAll();
//

//        List<ResponseDTO> responseList = new ArrayList<>();
//        for (UserCredential credential : usersCredentialsList) {
//            UserProfileDTO profile = profileMap.get(credential.getId());
//            if (profile != null) {
//                ResponseDTO dto = new ResponseDTO(
//                        profile.getName(),
//                        credential.getPassword(),
//                        credential.getEmail(),
//                        credential.getRole(),
//                        credential.isEnabled(),
//                        credential.getCreatedAt(),
//                        profile.getFamily(),
//                        profile.getPhone(),
//                        profile.getAddress(),
//                        profile.getBirthDate()
//                );
//                responseList.add(dto);
//            }
//        }
//
//        return responseList;
//    }

    /// after
    public List<ResponseDTO> getUserByFamily(String family) {

        ResponseEntity<List<UserProfileDTO>> response = AuthServiceClient.getUserByFamily(family);


        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

            Map<Long, UserProfileDTO> profileMap = response.getBody().stream()
                    .collect(Collectors.toMap(UserProfileDTO::getAuthUserId, Function.identity()));


            List<Long> authUserIdList = response.getBody().stream().map(UserProfileDTO::getAuthUserId)
                    .collect(Collectors.toList());


            List<UserCredential> usersCredentialsList = credentialRepository.findByIdIn(authUserIdList);


            List<ResponseDTO> responseList = new ArrayList<>();
            for (UserCredential credential : usersCredentialsList) {
                UserProfileDTO profile = profileMap.get(credential.getId());
                if (profile != null) {
                    ResponseDTO dto = mergeMapper.dtoProfileAndCredentialToResponseDto(profile, credential);
                    responseList.add(dto);
                }
            }
            return responseList;
        } else {

            throw new AuthServiceUnavailableException(
                    "Error receiving profiles: response status = " + response.getStatusCode());
        }
    }

    public ResponseDTO getUserByAuthUserId(long id){
    	Optional<UserCredential> userCredential = credentialRepository.findById(id);
    	ResponseEntity<UserProfileDTO>  userProfileDto = AuthServiceClient.getUserByAuthUserId(id);
    	ResponseDTO response = mergeMapper.DtoProfileAndCredentialToResponseDto(userProfileDto.getBody(), userCredential.get());
   return response;

    }




    @Transactional
    public void deleteUserById(Long id) {

        if (!credentialRepository.existsById(id)) {

            throw new UserNotFoundException("User with id " + id + " was not found in credentialRepository.");

        }




        ResponseEntity<Void> response = AuthServiceClient.deleteUser(id);



        if (response.getStatusCode().is2xxSuccessful()) {

            credentialRepository.deleteById(id);
            return; //
        } else {


            throw new AuthServiceUnavailableException(
                    "Error deleting user profile: response status = " + response.getStatusCode());
        }
    }

        public ResponseEntity<UserProfileDTO> patchUser( RegisterRequestDTO dto,long authUserId){
    	 UserCredential credential = dtoUserMapper.dtoToCredential(dto);
    	credentialRepository.save(credential);
    	  UserProfileDTO userProfileDto = dtoUserMapper.dtoToUserProfileDto(dto, authUserId);
    	  ResponseEntity<UserProfileDTO> response = AuthServiceClient.patchUser(userProfileDto);
    	  return response;
    }


}
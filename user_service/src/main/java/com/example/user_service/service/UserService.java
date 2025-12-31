package com.example.user_service.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.UserPatchDTO;
import com.example.user_service.dto.UserProfileDTO;
import com.example.user_service.entity.UserProfile;
import com.example.user_service.exception.UserNotFoundException;
import com.example.user_service.mapper.DtoUserMapper;
import com.example.user_service.mapper.UserDtoMapper;
import com.example.user_service.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final DtoUserMapper dtoUserMapper;

    private  final UserDtoMapper userDtoMappper;

    public List<UserProfileDTO> getAllUsers() {
        List<UserProfile> usersProfiles= userRepository.findAll();
        List<UserProfileDTO> usersDtoProfile = new ArrayList<>();;
        for(UserProfile userProfile: usersProfiles)
        {
            usersDtoProfile.add(userDtoMappper.userToDto(userProfile));
        }

        return usersDtoProfile ;
    }


    public UserProfileDTO getUserByAuthUserId(Long authUserId) {
        logger.info("Fetching user by AuthUserId: {}", authUserId);
        return userDtoMappper.userToDto(userRepository.findByAuthUserId(authUserId).orElseThrow(()-> new UserNotFoundException("User with ID " + authUserId + " not found"))  );
    }




    public UserProfileDTO createUser(UserProfileDTO  dto) {
        UserProfile userProfile = new UserProfile();
        userProfile = dtoUserMapper.dtoToEntity(dto);
        UserProfile savedUser = userRepository.save(userProfile);
        logger.info("Creating user by createUser with AuthUserId: {}", dto.getAuthUserId());
        return userDtoMappper.userToDto(savedUser);
    }


//  @PreAuthorize("hasRole('ADMIN')")

//  public UserProfileDTO createUserByAdmin(UserRegisterDTO dto) {
//      UserProfileDTO user = new UserProfileDTO();
//      user = dtoUserMapper.dtoToEntity(dto);
//      role =  "ROLE_ADMIN";
//      user.setRole(role);
//      User savedUser = userRepository.save(user);
//      logger.info("Creating user by createUserWithRole with email: {}", dto.getEmail());
//      return userDtoMappper.userToDto(savedUser);
//  }

    @Transactional
    public void deleteUserProfile(long authUserId) {
        logger.warn("Deleting userProfile by AuthUserId: {}", authUserId);
        userRepository.deleteByAuthUserId(authUserId);
    }


//  public User updateUser(Long id, User updatedUser) {
//  return userRepository.findById(id).map(user -> {
//      user.setName(updatedUser.getName());
//      user.setEmail(updatedUser.getEmail());
//      return userRepository.save(user);
//  }).orElse(null);
//}

//    public UserProfile updateUserProfile( UserProfileDTO updatedUser) {
//        logger.info("Attempting to update user with AuthUserId: {}", updatedUser.getAuthUserId());
//
//        UserProfile userProfile = userRepository.findByAuthUserId(updatedUser.getAuthUserId())
//            .orElseThrow(() -> new UserNotFoundException("User with ID " + updatedUser.getAuthUserId() + " not found"));
//
//        logger.debug("UserProfile found: {}", userProfile.getAuthUserId());
//        dtoUserMapper.updateEntityFromDto(userProfile, updatedUser);
//
//        UserProfile savedUser = userRepository.save(userProfile);
//        logger.info("User with AuthUserId {} updated successfully", updatedUser.getAuthUserId());
//        return savedUser;
//    }



    public List<UserProfileDTO> getUserByFamily(String Family)
    {
        logger.debug("Searching user by Family: {}", Family);
        List<UserProfile> users = userRepository.findByFamily(Family);
        List<UserProfileDTO> result =  new ArrayList<UserProfileDTO>();
        for(UserProfile userProfile :  users  )
        {

            result.add(userDtoMappper.userToDto(userProfile));
        }
        return  result;

    }

    ///////////////////////////////////////////////////////
    ///
    @Transactional
    public UserProfileDTO patchUser(UserPatchDTO updates) {
        logger.info("Starting patchUser update for user AuthUserId: {}",updates.getAuthUserId());
        UserProfile user = userRepository.findByAuthUserId(updates.getAuthUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with AuthUserId " + updates.getAuthUserId()));

        if (updates.getName() != null) {
            logger.debug("Updating name for user AuthUserId {}: {}", updates.getAuthUserId(), updates.getName());
            user.setName(updates.getName());
        }
        if (updates.getFamily() != null) {
            logger.debug("Updating family for user AuthUserId {}: {}", updates.getAuthUserId(), updates.getFamily());
            user.setFamily(updates.getFamily());
        }
        if (updates.getAddress() != null) {
            logger.debug("Updating Address for user AuthUserId {}: {}", updates.getAuthUserId(), updates.getAddress());
            user.setAddress(updates.getAddress());
        }
        if (updates.getBirthDate() != null) {
            logger.debug("Updating BirthDate for user AuthUserId {}: {}", updates.getAuthUserId(), updates.getBirthDate());
            user.setBirthDate(updates.getBirthDate());
        }
        if (updates.getPhone() != null) {
            logger.debug("Updating Phone for user AuthUserId {}: {}", updates.getAuthUserId(), updates.getPhone());
            user.setPhone(updates.getPhone());
        }

        UserProfile savedUser = userRepository.save(user);
        logger.info(" patchUser: Successfully  updated user AuthUserId: {}", updates.getAuthUserId());

        return userDtoMappper.userToDto(savedUser);

    }
}

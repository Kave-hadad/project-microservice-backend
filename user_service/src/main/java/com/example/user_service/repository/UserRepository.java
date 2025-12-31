package com.example.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.entity.UserProfile;


@Repository
public interface UserRepository  extends JpaRepository<UserProfile, Long>{

	

	  Optional<UserProfile> findByAuthUserId(long authUserId);
	  List<UserProfile> findByFamily(String family);
	  void deleteByAuthUserId(long authUserId);

}

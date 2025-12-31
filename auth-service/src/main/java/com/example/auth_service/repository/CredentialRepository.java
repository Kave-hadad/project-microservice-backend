package com.example.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.auth_service.entity.UserCredential;


@Repository
public interface  CredentialRepository extends JpaRepository<UserCredential, Long>{

	  Optional<UserCredential> findByEmail(String email);
	  List<UserCredential> findByIdIn(List<Long> ids);
}

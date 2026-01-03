package com.example.auth_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_credentials", uniqueConstraints = {
	    @UniqueConstraint(columnNames = "email")
	})
@Data
public class UserCredential {
	   @Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;               
	   @Column(nullable = false, unique = true)
	    private String email;      
	    private String password;      
	    private String role;         
	    private boolean enabled;      
	    private LocalDateTime createdAt; 



}

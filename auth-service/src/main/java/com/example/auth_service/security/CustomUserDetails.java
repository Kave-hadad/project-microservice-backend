package com.example.auth_service.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.auth_service.entity.UserCredential;


public class CustomUserDetails implements UserDetails{

	
	 private final UserCredential userCredential ;
	 public CustomUserDetails(UserCredential userCredential) {
		this.userCredential = userCredential;
	 }
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		  return List.of(new SimpleGrantedAuthority(userCredential.getRole()));
	}

	@Override
	public String getPassword() {
		
		return userCredential.getPassword();
	}

	@Override
	public String getUsername() {
		
		return userCredential.getEmail();
	}

	@Override
	public boolean isEnabled() {
	    return userCredential.isEnabled();
	}
	@Override
	public boolean isAccountNonExpired() {
	    return true;
	}

	@Override
	public boolean isAccountNonLocked() {
	    return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
	    return true;
	}
	
   // @PreAuthorize("hasRole('ADMIN') or #authUserId == principal.id")

    public Long getId() {
        return userCredential.getId();
    }
	
	
}

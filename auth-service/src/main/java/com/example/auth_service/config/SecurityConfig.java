package com.example.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.auth_service.security.CustomUserDetailsService;
import com.example.auth_service.security.JwtAuthFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity

@EnableMethodSecurity(prePostEnabled = true)

@AllArgsConstructor
public class SecurityConfig {
	private final CustomUserDetailsService userDetailsService;

	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf().disable().authorizeHttpRequests(auth -> auth

	
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll().requestMatchers("/api/auth/**")
				.permitAll()

				.anyRequest().authenticated()).sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

				.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder encoder) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
				.passwordEncoder(encoder).and().build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

package com.example.auth_service.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	public String generateToken(String username, String role) {
		System.out.println("im in genarate token  " + role);
		return Jwts.builder().setSubject(username).claim("role", role).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)

				.compact();
	}

	public boolean validateToken(String token) {
		try {
			getClaims(token);
			return true;
		} catch (ExpiredJwtException | SignatureException | IllegalArgumentException e) {
			return false;
		}
	}

	public String extractUsername(String token) {
		return getClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return getClaims(token).get("role", String.class);
	}

	private Claims getClaims(String token) {
		System.out.println("im in genarate token -- getClaims token method :  " + token);
		return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
	}

	private java.security.Key getSigningKey() {

		return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}

}

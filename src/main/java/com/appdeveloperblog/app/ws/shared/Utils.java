package com.appdeveloperblog.app.ws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.appdeveloperblog.app.ws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class Utils {

	private final Random random = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private final int ITERATIONS = 10000;
	private final int KEY_LENGTH = 256;
	
	public String generateUserId(int length) {
		return generateRandomString(length);
	}
	
	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder();
		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}
		return returnValue.toString();
	}
	
	public boolean hasTokenExpired(String token) {
		boolean status = true;
		try {
			Claims claims = Jwts.parser()
					.setSigningKey(SecurityConstants.getTokenSecret())
					.parseClaimsJws(token)
					.getBody();
			status = claims.getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
		    System.out.println(" Token expired ");
		} catch (SignatureException e) {
			System.out.println(" Token expired ");
		} catch(Exception e){
		    System.out.println(" Some other exception in JWT parsing ");
		}
		return status;
	}

	public String generateEmailVerificationToken(String userId) {
		String token = Jwts.builder()
			.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
			.setSubject(userId)
			.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
			.compact();
		return token;
	}

	public String generatePasswordResetToken(String userId) {
		String token = Jwts.builder()
			.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.RESET_PASSWORD_EXPIRATION_TIME))
			.setSubject(userId)
			.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
			.compact();
		return token;
	}
	
}

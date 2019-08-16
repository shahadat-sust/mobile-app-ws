package com.appdeveloperblog.app.ws.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appdeveloperblog.app.ws.exception.UserServiceException;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repository.PasswordResetTokenRepository;
import com.appdeveloperblog.app.ws.io.repository.UserRepository;
import com.appdeveloperblog.app.ws.shared.Utils;
import com.appdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appdeveloperblog.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userServiceImpl;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Mock
	private Utils utils;
	
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	UserEntity userEntity = null;
	String userId = "elri8sdfs3y";
	String encryptedPassword = "elri8sdfs3y";
	String email = "test@test.com";
	String emailVerificationToken = "ruiecriyoweoxe,xew";
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Shahadat");
		userEntity.setLastName("Hossain");
		userEntity.setUserId(userId);
		userEntity.setEmail(email);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmailVerificationToken(emailVerificationToken);
	}

	@Test
	void testFindByEmail() {
		when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(userEntity);
		
		UserDto userDto = userServiceImpl.findByEmail(email);
		
		assertNotNull(userDto);
		assertEquals("Shahadat", userDto.getFirstName());
	}
	
	@Test
	void testFindByEmail_UserServiceException() {
		when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
		assertThrows(UserServiceException.class, 
				() -> {
					UserDto userDto = userServiceImpl.findByEmail(email);
				}
		);
	}

	
	@Test
	void testCreateUser() {
		when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
		when(utils.generateUserId(ArgumentMatchers.anyInt())).thenReturn(userId);
		when(utils.generateAddressId(ArgumentMatchers.anyInt())).thenReturn("fsekmvjvdsksdf");
		when(utils.generateEmailVerificationToken(ArgumentMatchers.anyString())).thenReturn(emailVerificationToken);
		when(bCryptPasswordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(ArgumentMatchers.any(UserEntity.class))).thenReturn(userEntity);
		
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		List<AddressDto> addressDtos = new ArrayList<AddressDto>();
		addressDtos.add(addressDto);
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(addressDtos);
		
		UserDto returnValue = userServiceImpl.createUser(userDto);
		assertNotNull(returnValue);
		assertEquals(userEntity.getFirstName(), returnValue.getFirstName());
		
	}
	
	
}

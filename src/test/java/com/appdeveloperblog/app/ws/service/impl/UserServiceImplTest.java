package com.appdeveloperblog.app.ws.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appdeveloperblog.app.ws.exception.UserServiceException;
import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repository.PasswordResetTokenRepository;
import com.appdeveloperblog.app.ws.io.repository.UserRepository;
import com.appdeveloperblog.app.ws.shared.AmazonSES;
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
	
	@Mock
	private AmazonSES amazonSES;
	
	UserEntity userEntity = null;
	String userId = "elri8sdfs3y";
	String addressId = "fsekmvjvdsksdf";
	String password = "123";
	String encryptedPassword = "elri8sdfs3y";
	String email = "test@test.com";
	String emailVerificationToken = "ruiecriyoweoxe,xew";
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		//Stub
		userEntity = new UserEntity();
		userEntity.setFirstName("Shahadat");
		userEntity.setLastName("Hossain");
		userEntity.setEmail(email);
		userEntity.setAddresses(getAddressEntities());
		
		userEntity.setId(1L);
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmailVerificationToken(emailVerificationToken);
		
		long i = 0;
		for (AddressEntity addressEntity : userEntity.getAddresses()) {
			addressEntity.setId(i);
			addressEntity.setAddressId(addressId);
			i++;
		}
	}

	@Test
	void testFindByEmail() {
		when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(userEntity);
		
		UserDto userDto = userServiceImpl.findByEmail(email);
		assertNotNull(userDto);
		assertEquals(userEntity.getFirstName(), userDto.getFirstName());
	}
	
	@Test
	void testFindByEmail_UserServiceException() {
		when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
		
		assertThrows(UserServiceException.class, 
				() -> {
					userServiceImpl.findByEmail(email);
				}
		);
	}

	
	@Test
	void testCreateUser() {
		when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
		when(utils.generateUserId(ArgumentMatchers.anyInt())).thenReturn(userId);
		when(utils.generateAddressId(ArgumentMatchers.anyInt())).thenReturn(addressId);
		when(utils.generateEmailVerificationToken(ArgumentMatchers.anyString())).thenReturn(emailVerificationToken);
		when(bCryptPasswordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(ArgumentMatchers.any(UserEntity.class))).thenReturn(userEntity);
		doNothing().when(amazonSES).verifyEmail(ArgumentMatchers.any(UserDto.class));

		UserDto userDto = new UserDto();
		userDto.setFirstName("Shahadat");
		userDto.setLastName("Hossain");
		userDto.setEmail(email);
		userDto.setPassword(password);
		userDto.setAddresses(getAddressDtos());
		
		UserDto savedUserDto = userServiceImpl.createUser(userDto);
		assertNotNull(savedUserDto);
		assertEquals(userEntity.getFirstName(), savedUserDto.getFirstName());
		assertEquals(userEntity.getLastName(), savedUserDto.getLastName());
		assertNotNull(userEntity.getUserId());
		assertEquals(userEntity.getAddresses().size(), savedUserDto.getAddresses().size());
		
		verify(utils, times(userEntity.getAddresses().size())).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode(password);
		verify(userRepository, times(1)).save(ArgumentMatchers.any(UserEntity.class));
	}
	
	@Test
	void testCreateUser_UserServiceException() {
		when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(userEntity);
		UserDto userDto = new UserDto();
		userDto.setFirstName("Shahadat");
		userDto.setLastName("Hossain");
		userDto.setEmail(email);
		userDto.setPassword(password);
		userDto.setAddresses(getAddressDtos());
		
		assertThrows(UserServiceException.class, 
				() -> {
					
					userServiceImpl.createUser(userDto);
				});
	}
	
	
	private List<AddressDto> getAddressDtos() {
		AddressDto addressDto1 = new AddressDto();
		addressDto1.setType("shipping");
		addressDto1.setStreetName("R-8");
		addressDto1.setCity("Dhaka");
		addressDto1.setCountry("Bangladesh");
		addressDto1.setPostalCode("10101");
		
		AddressDto addressDto2 = new AddressDto();
		addressDto2.setType("billing");
		addressDto2.setStreetName("R-5");
		addressDto2.setCity("Comilla");
		addressDto2.setCountry("Bangladesh");
		addressDto2.setPostalCode("1204");
		
		List<AddressDto> addressDtos = new ArrayList<AddressDto>();
		addressDtos.add(addressDto1);
		addressDtos.add(addressDto2);
		return addressDtos;
	}
	
	private List<AddressEntity> getAddressEntities() {
		List<AddressDto> addressDtos = getAddressDtos();
		Type type =  new TypeToken<List<AddressEntity>>() {}.getType();
		List<AddressEntity> addressEntities = new ModelMapper().map(addressDtos, type);
		return addressEntities;
	}
	
	
}

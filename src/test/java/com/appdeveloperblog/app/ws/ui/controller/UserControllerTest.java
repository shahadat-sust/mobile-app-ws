package com.appdeveloperblog.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.appdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appdeveloperblog.app.ws.shared.dto.UserDto;
import com.appdeveloperblog.app.ws.ui.model.response.UserRest;

class UserControllerTest {

	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	private UserDto userDto;
	private String userId = "eyerpwr98dsf2";
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userDto = new UserDto();
		userDto.setId(1L);
		userDto.setUserId(userId);
		userDto.setFirstName("Shahadat");
		userDto.setLastName("Hossain");
		userDto.setEmail("test@test.com");
		userDto.setEmailVerificationToken("pture23nfdsf435");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEncryptedPassword("94f323gur@eort");
		userDto.setAddresses(getAddressDtos());
	}

	@Test
	void testGetUser() {
		when(userService.getUserByUserId(ArgumentMatchers.anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(userId);
		assertNotNull(userRest);
		assertEquals(userDto.getUserId(), userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
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
	
}

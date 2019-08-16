package com.appdeveloperblog.app.ws.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appdeveloperblog.app.ws.exception.UserServiceException;
import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repository.AddressRepository;
import com.appdeveloperblog.app.ws.io.repository.UserRepository;
import com.appdeveloperblog.app.ws.service.AddressService;
import com.appdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appdeveloperblog.app.ws.ui.model.response.ErrorMessages;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> returnValues = new ArrayList<>();
		
		UserEntity userEntity = userRepository.getUserByUserId(userId);
		if (userEntity == null) {
			return returnValues;
		}
		
		List<AddressEntity> addressEntities = addressRepository.findAllByUserDetails(userEntity);
		
		if (addressEntities != null) {
			Type type =  new TypeToken<List<AddressDto>>() {}.getType();
			returnValues = new ModelMapper().map(addressEntities, type);
		}
		
		return returnValues;
	}

	@Override
	public AddressDto getAddress(String addressId) {
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if (addressEntity == null) {
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getMessage());
		}
		
 		return new ModelMapper().map(addressEntity, AddressDto.class);
	}

}

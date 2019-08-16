package com.appdeveloperblog.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appdeveloperblog.app.ws.exception.UserServiceException;
import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.PasswordResetTokenEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;
import com.appdeveloperblog.app.ws.io.repository.PasswordResetTokenRepository;
import com.appdeveloperblog.app.ws.io.repository.UserRepository;
import com.appdeveloperblog.app.ws.service.UserService;
import com.appdeveloperblog.app.ws.shared.AmazonSES;
import com.appdeveloperblog.app.ws.shared.Utils;
import com.appdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appdeveloperblog.app.ws.shared.dto.UserDto;
import com.appdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appdeveloperblog.app.ws.ui.model.response.UserRest;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private Utils utils;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private AmazonSES amazonSES;
	
	@Override
	public UserDto createUser(UserDto userDto) {
		if (userRepository.findByEmail(userDto.getEmail()) != null) {
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getMessage());
		}
		
		userDto.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userDto.setUserId(utils.generateUserId(30));
		userDto.setEmailVerificationToken(utils.generateEmailVerificationToken(userDto.getUserId()));
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		for (AddressDto addressDto : userDto.getAddresses()) {
			addressDto.setUserDetails(userDto);
			addressDto.setAddressId(utils.generateAddressId(30));
		}

		UserEntity userEntity = new ModelMapper().map(userDto, UserEntity.class);
		UserEntity storedEntity = userRepository.save(userEntity);

		UserDto returnValue = new ModelMapper().map(storedEntity, UserDto.class);

		amazonSES.verifyEmail(userDto);
		
		return returnValue;
	}
	
	@Override
	public UserDto findByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());
		}
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		//UserDto returnValue = new ModelMapper().map(userEntity, UserDto.class);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(username);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());
		}
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerficationStatus(),
				true, true, true, new ArrayList<>());
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepository.getUserByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());
		}
		
		UserDto returnValue = new ModelMapper().map(userEntity, UserDto.class);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserEntity userEntity = userRepository.getUserByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());
		}
		
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity updatedUserEntity = userRepository.save(userEntity);
		UserDto returnValue = new ModelMapper().map(updatedUserEntity, UserDto.class);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.getUserByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getMessage());
		}
		
		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		Pageable pageable = PageRequest.of(page, limit);
		Page<UserEntity> userEntities = userRepository.findAll(pageable);
		
		List<UserDto> returnValues = new ArrayList<>();
		for (UserEntity userEntity : userEntities) {
			UserDto returnValue = new ModelMapper().map(userEntity, UserDto.class);
			returnValues.add(returnValue);
		}
		
		return returnValues;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		UserEntity userEntity = userRepository.findByEmailVerificationToken(token);
		if (userEntity == null) {
			return false;
		}
		
		if (!utils.hasTokenExpired(token)) {
			userEntity.setEmailVerificationToken(null);
			userEntity.setEmailVerficationStatus(Boolean.TRUE);
			userRepository.save(userEntity);
			return true;
		} 
		
		return false;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null) {
			return false;
		}

		String token = utils.generatePasswordResetToken(userEntity.getUserId());
		
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		boolean returnValue = amazonSES.sendPasswordResetRequest(
				userEntity.getFirstName(), 
				userEntity.getEmail(), 
				token);
		
		return returnValue;
	}

	@Override
	public boolean resetPassword(String password, String token) {
		boolean returnValue = false;
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		if (passwordResetTokenEntity == null) {
			return returnValue;
		}
		
		if (utils.hasTokenExpired(token)) {
			return returnValue;
		}
		
		String encryptedPassword = bCryptPasswordEncoder.encode(password);
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encryptedPassword);
		UserEntity savedUserEntity = userRepository.save(userEntity);
		
		if (savedUserEntity != null && encryptedPassword.equals(savedUserEntity.getEncryptedPassword())) {
			returnValue = true;
		}
		
		passwordResetTokenRepository.delete(passwordResetTokenEntity);
		
		return returnValue;
	}

}

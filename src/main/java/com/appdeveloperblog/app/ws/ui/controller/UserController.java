package com.appdeveloperblog.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appdeveloperblog.app.ws.exception.UserServiceException;
import com.appdeveloperblog.app.ws.service.AddressService;
import com.appdeveloperblog.app.ws.service.UserService;
import com.appdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appdeveloperblog.app.ws.shared.dto.UserDto;
import com.appdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appdeveloperblog.app.ws.ui.model.request.RequestOperationName;
import com.appdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appdeveloperblog.app.ws.ui.model.response.AddressRest;
import com.appdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appdeveloperblog.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AddressService addressService;

	@GetMapping(
			path = "/{userId}", 
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public UserRest getUser(@PathVariable String userId) {
		UserDto userDto = userService.getUserByUserId(userId);
		UserRest returnValue = new ModelMapper().map(userDto, UserRest.class);
		return returnValue;
	}
	
	@GetMapping( 
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public List<UserRest> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int limit) {
		List<UserRest> returnValues = new ArrayList<UserRest>();
		List<UserDto> userDtos = userService.getUsers(page, limit);
		
		for (UserDto userDto : userDtos) {
			UserRest returnValue = new ModelMapper().map(userDto, UserRest.class);
			returnValues.add(returnValue);
		}
		return returnValues;
	}
	
	@PostMapping(
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
		if (StringUtils.isEmpty(userDetails.getFirstName()) 
				|| StringUtils.isEmpty(userDetails.getLastName())
				|| StringUtils.isEmpty(userDetails.getEmail())
				|| StringUtils.isEmpty(userDetails.getPassword())) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getMessage());
		}

		UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);
		UserDto createdDto = userService.createUser(userDto);
		UserRest returnValue = new ModelMapper().map(createdDto, UserRest.class);
		return returnValue;
	}
	
	@PutMapping(
			path = "/{userId}",
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public UserRest updateUser(@PathVariable String userId, @RequestBody UserDetailsRequestModel userDetails) {
		UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);
		UserDto updateUser = userService.updateUser(userId, userDto);
		UserRest returnValue = new ModelMapper().map(updateUser, UserRest.class);
		return returnValue;
	}
	
	@DeleteMapping(
			path = "/{userId}",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public OperationStatusModel deleteUser(@PathVariable String userId) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(userId);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}
	
	@GetMapping(
			path = "/{userId}/addresses", 
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"}
	)
	public Resources<AddressRest> getUserAddresses(@PathVariable String userId) {
		List<AddressRest> returnValues = new ArrayList<>();
		List<AddressDto> addressDtos = addressService.getAddresses(userId);
		if (addressDtos != null) {
			Type type =  new TypeToken<List<AddressRest>>() {}.getType();
			returnValues = new ModelMapper().map(addressDtos, type);
			
			for (AddressRest addressRest : returnValues) {
				Link addressLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class)
						.getUserAddress(userId, addressRest.getAddressId())).withSelfRel();
				addressRest.add(addressLink);
				Link userLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class)
						.getUser(userId)).withRel("user");
				addressRest.add(userLink);
			}
		}
		return new Resources<AddressRest>(returnValues);
	}
	
	@GetMapping(
			path = "/{userId}/addresses/{addressId}", 
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"}
	)
	public Resource<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		AddressDto addressDto = addressService.getAddress(addressId);
		
		Link addressLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class)
				.getUserAddress(userId, addressId)).withSelfRel();
		Link addressesLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(userId)).withRel("addresses");
		Link userLink = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(UserController.class)
				.getUser(userId)).withRel("user");
		AddressRest addressRest = new ModelMapper().map(addressDto, AddressRest.class);
		addressRest.add(addressLink);
		addressRest.add(addressesLink);
		addressRest.add(userLink);
		return new Resource<AddressRest>(addressRest);
	}
	
	@GetMapping(
			path = "/email-verification",
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public OperationStatusModel verifyEmailToken(@RequestParam(name = "token") String tokenValue) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(tokenValue);
		if (isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.toString());
		} else {
			returnValue.setOperationResult(RequestOperationStatus.FAILED.toString());
		}
		return returnValue;
	}
	
	@PostMapping(
			path = "/password-reset-request",
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public OperationStatusModel requestPasswordReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			returnValue.setOperationResult(RequestOperationStatus.FAILED.name());
		}
		return returnValue;
	}
	
	@PostMapping(
			path = "/password-reset",
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public OperationStatusModel resetPassword(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		boolean operationResult = userService.resetPassword(
				passwordResetRequestModel.getPassword(),
				passwordResetRequestModel.getToken());
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			returnValue.setOperationResult(RequestOperationStatus.FAILED.name());
		}
		return returnValue;
	}
	
}

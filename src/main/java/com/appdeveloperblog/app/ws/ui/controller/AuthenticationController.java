package com.appdeveloperblog.app.ws.ui.controller;

import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.appdeveloperblog.app.ws.security.SecurityConstants;
import com.appdeveloperblog.app.ws.ui.model.request.UserLoginRequestModel;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
public class AuthenticationController {

	@ApiOperation("User Login")
	@ApiResponses({
		@ApiResponse(
				code = HttpStatus.SC_OK,
				message = "Response Headers",
				responseHeaders = {
						@ResponseHeader(
								name = SecurityConstants.AUTHORIZATION_HEADER,
								description = "Bearer <JWT value here>",
								response = String.class
						),
						@ResponseHeader(
								name = SecurityConstants.USER_ID_HEADER,
								description = "<Public userId value here>",
								response = String.class
						)
				})
	})
	@PostMapping("/users/login")
	public void theFakeLogin(@RequestBody UserLoginRequestModel requestModel) {
		throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security.");
	}
	
}

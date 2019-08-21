package com.appdeveloperblog.app.ws.security;

import com.appdeveloperblog.app.ws.SpringApplicationContext;

public class SecurityConstants {

	public static final long EXPIRATION_TIME = 864000000; //10 Days
	public static final long RESET_PASSWORD_EXPIRATION_TIME = 3600000; //1 Hours
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String USER_ID_HEADER = "userId";
	public static final String SIGN_UP_URL = "/users";
	public static final String SIGN_IN_URL = "/users/login";
	public static final String EMAIL_VERIFICATION_URL = "/users/email-verification";
	public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
	public static final String PASSWORD_RESET_URL = "/users/password-reset";
	public static final String H2_CONSOLE_URL = "/h2-console/**";
	
	public static String getTokenSecret() {
		return ((AppProperties)SpringApplicationContext.getBean("appProperties")).getTokenSecret();
	}
	
}
package com.appdeveloperblog.app.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

	@Autowired
	private Environment environment;
	
	public String getTokenSecret() {
		return environment.getProperty("token.secret");
	}
	
	public String getAwsAccessKey() {
		return environment.getProperty("aws.accessKey");
	}
	
	public String getAwsSecretKey() {
		return environment.getProperty("aws.secretKey");
	}
	
}

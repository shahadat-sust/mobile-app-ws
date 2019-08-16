package com.appdeveloperblog.app.ws.shared;

import org.springframework.util.StringUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.appdeveloperblog.app.ws.shared.dto.UserDto;

public class AmazonSES {

	private final String FROM = "shahadat.sust@gmail.com";
	
	private final String VERIFY_EMAIL_SUBJECT = "One last step to complete your registration with PhotoApp";
	private final String VERIFY_EMAIL_HTMLBODY = "<h1>Please verify your email address</h1>"
			+ "<p>Thank you for registering with our mobile app. To complete registration process and be able to login, "
			+ "click on the following link: </p>"
			+ "<a href=\"http://localhost:8080/verification-service/email-verification.html?token=$tokenValue\">"
			+ "First step to complete your registration</a><BR><BR>"
			+ "Thank you!, And we are waiting for you inside!";
	private final String VERIFY_EMAIL_TEXTLBODY = "Please verify your email address. "
			+ "Thank you for registering with our mobile app. To complete registration process and be able to login, "
			+ "open then the following link on your browser window: "
			+ "http://localhost:8080/verification-service/email-verification.html?token=$tokenValue "
			+ "Thank you!, And we are waiting for you inside!";
	
	private final String PASSWORD_RESET_SUBJECT = "Password Reset Request";
	private final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
			+ "<p>Someone has requested to reset your password with our project. If it was not you, please contact with administrator, "
			+ "otherwise please click on the below link to set a new password: </p>"
			+ "<a href=\"http://localhost:8080/verification-service/password_reset.html?token=$tokenValue\">"
			+ "Click this link to reset password</a><BR><BR>"
			+ "Thank you!";
	private final String PASSWORD_RESET_TEXTLBODY = "A request to reset your password. "
			+ "Someone has requested to reset your password with our project. If it was not you, please contact with administrator, "
			+ "otherwise open then the following link on your browser window to set a new password: "
			+ "http://localhost:8080/verification-service/password_reset.html?token=$tokenValue "
			+ "Thank you!";
	
	public void verifyEmail(UserDto userDto) {
	
		AmazonSimpleEmailService service = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.build();
		String htmlBodyWithToken = VERIFY_EMAIL_HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
		String textBodyWithToken = VERIFY_EMAIL_TEXTLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
		
		SendEmailRequest request = new SendEmailRequest()
				.withSource(FROM)
				.withDestination(new Destination().withToAddresses(userDto.getEmail()))
				.withMessage(new Message()
						.withSubject(new Content().withCharset("UTF-8").withData(VERIFY_EMAIL_SUBJECT))
						.withBody(new Body()
								.withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
								.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken))));
		service.sendEmail(request);
		System.out.println("Email Sent!");
	}

	public boolean sendPasswordResetRequest(String firstName, String email, String token) {
		AmazonSimpleEmailService service = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.build();
		
		String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
		String textBodyWithToken = PASSWORD_RESET_TEXTLBODY.replace("$tokenValue", token);
		
		SendEmailRequest request = new SendEmailRequest()
				.withSource(FROM)
				.withDestination(new Destination().withToAddresses(email))
				.withMessage(new Message()
						.withSubject(new Content().withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT))
						.withBody(new Body()
								.withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
								.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken))));
		SendEmailResult result = service.sendEmail(request);
		System.out.println("Email Sent!");
		
		return (result != null 
				&& !StringUtils.isEmpty(result.getMessageId()) 
				&& StringUtils.hasText(result.getMessageId()));
	}
	
}

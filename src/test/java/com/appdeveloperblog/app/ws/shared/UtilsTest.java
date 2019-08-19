package com.appdeveloperblog.app.ws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

	@Autowired
	private Utils utils;
	
	@Test
	void testGenerateUserId() {
		String userId1 = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		assertNotNull(userId1);
		assertNotNull(userId2);
		assertTrue(userId1.length() == 30);
		assertTrue(userId2.length() == 30);
		assertTrue(!userId1.equalsIgnoreCase(userId2));
	}

	@Test
	void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("e7uwklaenxi8ds");
		assertNotNull(token);
		
		boolean hasExpired = utils.hasTokenExpired(token);
		assertFalse(hasExpired);
	}
	
	@Test
	void testHasTokenExpired() {
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1NjUxMTg5OTQsInN1YiI6ImU3dXdrbGFlbnhpOGRzIn0._DZ_i43YK0INj7RW92Sdvnjoj6y9R6m6eW69trmjVKSODU0OEjo46-WTQTwdZy9XRflWiiqiZCfcIehi0WuoHw";
		assertNotNull(token);
		
		boolean hasExpired = utils.hasTokenExpired(token);
		assertTrue(hasExpired);
	}

}

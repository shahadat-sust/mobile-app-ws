package com.appdeveloperblog.app.ws.io.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appdeveloperblog.app.ws.io.entity.UserEntity;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;
	
	private static boolean isRecordCreated = false;
	
	@BeforeEach
	void setUp() throws Exception {
		if (!isRecordCreated) {
			createRecords();
			isRecordCreated = true;
		}
	}

	@Test
	void testGetVerifiedUser() {
		Pageable pageable = PageRequest.of(1, 1);
		Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageable);
		assertNotNull(pages);
		
		List<UserEntity> userEntities = pages.getContent();
		assertNotNull(userEntities);
		assertEquals(1, userEntities.size());
	}
	
	@Test
	void testFindUsersByFirstName() {
		String firstName = "Shahadat";
		List<UserEntity> userEntities = userRepository.findUsersByFirstName(firstName);
		assertNotNull(userEntities);
		assertEquals(2, userEntities.size());
		
		for (UserEntity userEntity : userEntities) {
			assertEquals(firstName, userEntity.getFirstName());
		}
	}
	
	@Test
	void testFindUsersByLastName() {
		String lastName = "Hossain1";
		List<UserEntity> userEntities = userRepository.findUsersByLastName(lastName);
		assertNotNull(userEntities);
		assertEquals(1, userEntities.size());
		
		for (UserEntity userEntity : userEntities) {
			assertEquals(lastName, userEntity.getLastName());
		}
	}
	
	@Test
	void testFindUsersByKeyword() {
		String keyword = "Shah";
		List<UserEntity> userEntities = userRepository.findUsersByKeyword(keyword);
		assertNotNull(userEntities);
		assertEquals(2, userEntities.size());
		
		for (UserEntity userEntity : userEntities) {
			assertTrue(userEntity.getFirstName().contains(keyword)
					|| userEntity.getLastName().contains(keyword));
		}
	}
	
	@Test
	void testFindUserFirstNameAndLastNameByKeyword() {
		String keyword = "Shah";
		List<Object[]> columnsList = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
		assertNotNull(columnsList);
		assertEquals(2, columnsList.size());
		
		for (Object[] columns : columnsList) {
			String firstName = String.valueOf(columns[0]);
			String lastName = String.valueOf(columns[1]);
			System.out.println("firstName : " + firstName);
			System.out.println("lastName : " + lastName);
			assertNotNull(firstName);
			assertNotNull(lastName);
		}
	}
	
	@Test
	void testUpdateUserEmailVerificationStatus() {
		boolean emailVerificationStatus = true;
		userRepository.updateUserEmailVerificationStatus("fwer03ecuwewvy", true);
		
		UserEntity userEntity = userRepository.getUserByUserId("fwer03ecuwewvy");
		assertNotNull(userEntity);
		assertTrue(emailVerificationStatus == userEntity.getEmailVerificationStatus());
	}
	
	private void createRecords() {
		UserEntity userEntity1 = new UserEntity();
		userEntity1.setUserId("fwer03ecuwewvy");
		userEntity1.setFirstName("Shahadat");
		userEntity1.setLastName("Hossain1");
		userEntity1.setEmail("shahadat.hossain1@test.com");
		userEntity1.setEncryptedPassword("xxxx");
		userEntity1.setEmailVerificationStatus(false);
		
		AddressEntity addressEntity1 = new AddressEntity();
		addressEntity1.setAddressId("erruywue94efnnv");
		addressEntity1.setType("shipping");
		addressEntity1.setStreetName("R-8");
		addressEntity1.setCity("Dhaka");
		addressEntity1.setCountry("Bangladesh");
		addressEntity1.setPostalCode("10101");

		List<AddressEntity> addressEntitys1 = new ArrayList<>();
		addressEntitys1.add(addressEntity1);
		userEntity1.setAddresses(addressEntitys1);
		
		userRepository.save(userEntity1);
		
		UserEntity userEntity2 = new UserEntity();
		userEntity2.setUserId("dlsdf94mdv32m");
		userEntity2.setFirstName("Shahadat");
		userEntity2.setLastName("Hossain2");
		userEntity2.setEmail("shahadat.hossain2@test.com");
		userEntity2.setEncryptedPassword("xxxx");
		userEntity2.setEmailVerificationStatus(false);
		
		AddressEntity addressEntity2 = new AddressEntity();
		addressEntity2.setAddressId("psdvb39fvmvs4k");
		addressEntity2.setType("billing");
		addressEntity2.setStreetName("R-5");
		addressEntity2.setCity("Comilla");
		addressEntity2.setCountry("Bangladesh");
		addressEntity2.setPostalCode("1204");
		
		List<AddressEntity> addressEntitys2 = new ArrayList<>();
		addressEntitys2.add(addressEntity2);
		
		userRepository.save(userEntity2);
	}

}

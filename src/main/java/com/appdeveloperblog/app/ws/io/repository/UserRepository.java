package com.appdeveloperblog.app.ws.io.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appdeveloperblog.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Integer> {

	UserEntity findByEmail(String email);

	UserEntity getUserByUserId(String userId);

	UserEntity findByEmailVerificationToken(String token);
	
	@Query(value = "SELECT * FROM users u WHERE u.email_verification_status = 'true'",
			countQuery = "SELECT COUNT(*) FROM users u WHERE u.email_verification_status = 'true'",
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageable);
	
	@Query(value = "SELECT * FROM users u WHERE u.first_name = ?1",
			nativeQuery = true)
	List<UserEntity> findUsersByFirstName(String firstName);
	
	@Query(value = "SELECT * FROM users u WHERE u.last_name = :lastName",
			nativeQuery = true)
	List<UserEntity> findUsersByLastName(@Param("lastName") String lastName);
	
	@Query(value = "SELECT * FROM users u WHERE u.first_name LIKE %:keyword% OR u.last_name LIKE %:keyword%",
			nativeQuery = true)
	List<UserEntity> findUsersByKeyword(@Param("keyword") String keyword);
	
	@Query(value = "SELECT u.first_name, u.last_name FROM users u WHERE u.first_name LIKE %:keyword% OR u.last_name LIKE %:keyword%",
			nativeQuery = true)
	List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE users u SET u.email_verification_status=:status, email_verification_token=NULL WHERE u.user_id = :userId",
			nativeQuery = true)
	void updateUserEmailVerificationStatus(@Param("userId") String userId, @Param("status") boolean emailVerificationStatus);
	
}

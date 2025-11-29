package com.main.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.main.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByEmail(String email);

	boolean existsByPassword(String password);
	
	boolean existsByPhoneNumber(String phoneNumber);

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByPassword(String password);
	
	Optional<UserEntity> findByPhoneNumber(String phoneNumber);
	
	long countByUserType(String userType);
	
	@Query("SELECT u FROM UserEntity u WHERE u.email = :email")
	Optional<UserEntity> findByEmailFresh(@Param("email") String email);
}

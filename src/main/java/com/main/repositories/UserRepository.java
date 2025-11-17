package com.main.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByEmail(String email);

	boolean existsByPassword(String password);
	
	boolean existsByPhoneNumber(String phoneNumber);

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByPassword(String password);
	
	Optional<UserEntity> findByPhoneNumber(String phoneNumber);
	
	long countByUserType(String userType);
}

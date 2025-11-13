package com.main.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.entities.UserEntity;
import com.main.enums.Role;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByEmail(String email);

	boolean existsByPassword(String password);
	
	boolean existsByPhoneNumber(String phoneNumber);

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByPassword(String password);
	
	Optional<UserEntity> findByPhoneNumber(String phoneNumber);

	
	boolean existsByRole(Role role);
	
	long countByRole(Role role);
}

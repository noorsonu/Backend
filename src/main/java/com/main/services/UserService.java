package com.main.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.main.dtos.RegisterRequest;
import com.main.dtos.UserDto;
import com.main.entities.UserEntity;

public interface UserService {

	UserDto createUser(UserDto userDto);
	
	UserDto createUserWithPhone(UserDto userDto, String phoneNumber);

	UserDto getUserById( Long userId);
	
	List<UserDto> getAllUsers();
	
	UserDto updateUser(UserDto userDto );

	String deleteUser(Long userId);
	
	UserEntity register(RegisterRequest req) throws Exception;
	
	UserEntity registerAdmin(RegisterRequest req) throws Exception;

	UserDetails loadUserByEmail(String email) throws UsernameNotFoundException;
	


}



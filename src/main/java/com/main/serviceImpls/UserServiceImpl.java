package com.main.serviceImpls;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder; // Added import
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.dtos.RegisterRequest;
import com.main.dtos.UserDto;
import com.main.entities.UserEntity;
import com.main.mappers.UserMapper;
import com.main.repositories.UserRepository;
import com.main.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDto createUser(UserDto userDto) {

		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}

		try {

			UserEntity userEntity = userMapper.toUser(userDto);

			UserEntity savedUser = userRepository.save(userEntity);

			return userMapper.dtoToUser(savedUser);

		} catch (Exception e) {

			throw new RuntimeException("An unexpected error occurred while saving the user.", e);
		}
	}
	
	@Override
	public UserDto createUserWithPhone(UserDto userDto, String phoneNumber) {
		if (userRepository.existsByEmail(userDto.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}
		if (phoneNumber != null && !phoneNumber.isBlank() && userRepository.existsByPhoneNumber(phoneNumber)) {
			throw new IllegalArgumentException("Phone number already exists");
		}
		try {
			UserEntity userEntity = userMapper.toUser(userDto);
			userEntity.setPhoneNumber(phoneNumber);
			UserEntity savedUser = userRepository.save(userEntity);
			return userMapper.dtoToUser(savedUser);
		} catch (Exception e) {
			throw new RuntimeException("An unexpected error occurred while saving the user.", e);
		}
	}

	@Override
	public UserDto getUserById(Long userId) {

		try {

			UserEntity user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("Account does not exist"));

			UserDto dto = userMapper.dtoToUser(user);
			return dto;

		} catch (RuntimeException e) {

			throw new RuntimeException("Error: " + e.getMessage(), e);

		} catch (Exception e) {

			throw new RuntimeException("An unexpected error occurred while fetching the user.", e);
		}

	}

	@Override
	public List<UserDto> getAllUsers() {
		try {
			List<UserEntity> list = userRepository.findAll();
			if (list.isEmpty()) {
				log.warn("No user found in database.");
				return Collections.emptyList();
			}
			return userMapper.toListUserDto(list);
		} catch (Exception e) {
			log.error("Error while fetching all users.");
			return Collections.emptyList();
		}
	}

	@Override
	public UserDto updateUser(UserDto userDto) {
		try {

			UserEntity existing = userRepository.findByEmail(userDto.getEmail())
					.orElseThrow(() -> new IllegalArgumentException("Email does not match"));

			if (!existing.getPassword().equals(userDto.getPassword())) {
				throw new IllegalArgumentException("Password does not match");
			}

			existing.setName(userDto.getName());
			existing.setEmail(userDto.getEmail());
			existing.setPassword(userDto.getPassword());

			UserEntity saved = userRepository.save(existing);
			return userMapper.dtoToUser(saved);

		} catch (IllegalArgumentException e) {
			System.err.println("Error while updating user: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("An unexpected error occurred while updating user", e);
		}
	}

	@Override
	public String deleteUser(Long userId) {
	    try {
	    	
	        if (userRepository.existsById(userId)) {
	            userRepository.deleteById(userId);
	            return "User deleted successfully with ID: " + userId;
	        } else {
	            return "User not found with ID: " + userId;
	        }
	    } catch (Exception e) {
	    	
	        e.printStackTrace();
	        return "Error occurred while deleting user with ID: " + userId;
	    }
	}

    public UserEntity register(RegisterRequest req) throws Exception {
        try {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new Exception("Email already exists");
            }
            if (req.getPhoneNumber() != null && !req.getPhoneNumber().isBlank() && userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
                throw new Exception("Phone number already exists");
            }
            UserEntity user = new UserEntity();
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPhoneNumber(req.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            user.setUserType("USER");
            
            UserEntity saved = userRepository.save(user);
            return saved;
        } catch (Exception e) {
            throw new Exception("Registration failed: " + e.getMessage());
        }
    }
	
	
	@Override
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.isBlocked()) {
            throw new DisabledException("User is blocked and cannot log in.");
        }

        return new User(
                user.getEmail(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserType()))
        );
    }

	@Override
	public void blockUser(Long userId) {
		// Get current authenticated user's email
		String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		UserEntity currentAdmin = userRepository.findByEmail(currentAdminEmail)
				.orElseThrow(() -> new RuntimeException("Authenticated admin not found"));

		if (currentAdmin.getId().equals(userId)) {
			throw new IllegalArgumentException("Admin cannot block themselves.");
		}

		UserEntity userToBlock = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		if ("ADMIN".equals(userToBlock.getUserType())) {
			throw new IllegalArgumentException("Admin can only block regular users, not other admins.");
		}

		userToBlock.setBlocked(true);
		userRepository.save(userToBlock);
	}

	@Override
	public void unblockUser(Long userId) {
		// Get current authenticated user's email
		String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		UserEntity currentAdmin = userRepository.findByEmail(currentAdminEmail)
				.orElseThrow(() -> new RuntimeException("Authenticated admin not found"));

		if (currentAdmin.getId().equals(userId)) {
			throw new IllegalArgumentException("Admin cannot unblock themselves.");
		}

		UserEntity userToUnblock = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		if ("ADMIN".equals(userToUnblock.getUserType())) {
			throw new IllegalArgumentException("Admin can only unblock regular users, not other admins.");
		}

		userToUnblock.setBlocked(false);
		userRepository.save(userToUnblock);
	}

	
}

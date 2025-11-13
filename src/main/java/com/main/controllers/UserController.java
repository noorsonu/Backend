//package com.main.controllers;
//
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.main.dtos.UserDto;
//import com.main.services.UserService;
//
//import jakarta.validation.ConstraintViolationException;
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "*")
//public class UserController {
//
//	@Autowired
//	private UserService userService;
//
////	@PostMapping("/create")
////	public ResponseEntity<Object> createAccount(@Valid @RequestBody UserDto userDto,
////												@RequestParam(name = "phoneNumber", required = false) String phoneNumber) {
////
////		try {
////
////			UserDto createdUser = (phoneNumber == null || phoneNumber.isBlank())
////					? userService.createUser(userDto)
////					: userService.createUserWithPhone(userDto, phoneNumber);
////
////			return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
////
////		} catch (DataIntegrityViolationException e) {
////
////			return ResponseEntity.status(HttpStatus.CONFLICT)
////					.body("Error: Email already exists. Please use a different email.");
////
////		} catch (ConstraintViolationException e) {
////
////			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation failed: " + e.getMessage());
////
////		} catch (RuntimeException e) {
////
////			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
////
////		} catch (Exception e) {
////
////			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////					.body("An unexpected error occurred while creating the account.");
////		}
////	}
//
//	@GetMapping("/get/{userId}")
//	public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
//		try {
//
//			UserDto userDto = userService.getUserById(userId);
//
//			return ResponseEntity.ok(userDto);
//
//		} catch (NoSuchElementException e) {
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User with ID " + userId + " not found.");
//
//		} catch (RuntimeException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("An unexpected error occurred while fetching the user.");
//		}
//	}
//
//	@PreAuthorize("hasRole('ADMIN')")
//	@GetMapping("/allUsers")
//	public ResponseEntity<?> getAllUsers() {
//		try {
//			List<UserDto> list = userService.getAllUsers();
//
//			if (list.isEmpty()) {
//				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users found in the system.");
//			}
//
//			return ResponseEntity.ok(list);
//
//		} catch (RuntimeException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("An unexpected error occurred while fetching users.");
//		}
//	}
//
//	@PutMapping("/update/{userId}")
//	public ResponseEntity<?> updateByCredentials(@Valid @RequestBody UserDto userDto) {
//		
//		try {
//			
//			UserDto updated = userService.updateUser(userDto);
//			return ResponseEntity.ok(updated);
//			
//		} catch (IllegalArgumentException e) {
//			
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//			
//		} catch (RuntimeException e) {
//			
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//			
//		} catch (Exception e) {
//			
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("An unexpected error occurred while updating the user.");
//		}
//	}
//	
//	@DeleteMapping("/delete/{userId}")
//	public ResponseEntity<String> deleteUser(@PathVariable Long userId, Authentication authentication){
//		try {
//			boolean isAdmin = authentication.getAuthorities().stream()
//					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//			if (!isAdmin) {
//				// Non-admins can delete only their own account
//				com.main.dtos.UserDto target = userService.getUserById(userId);
//				if (target == null || target.getEmail() == null || !target.getEmail().equals(authentication.getName())) {
//					return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own account");
//				}
//			}
//			String delete = userService.deleteUser(userId);
//			return ResponseEntity.ok(delete);
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//		} catch (RuntimeException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("An unexpected error occurred while deleting the user.");
//		}
//	}
//	
//	
//    @GetMapping("/me")
//    public ResponseEntity<?> me(Authentication auth) {
//        return ResponseEntity.ok(Map.of("email", auth.getName()));
//    }
//}

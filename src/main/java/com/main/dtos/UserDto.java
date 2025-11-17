package com.main.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private Long id; 
	
	@NotBlank(message = "Name must not be blank")
	private String name;

	@NotBlank(message = "Email must not be blank")
	@Email(message = "Email must be a valid email")
	private String email;

	private String userType;

	@NotBlank(message = "Password must not be blank")
	@Size(min = 8, max = 20,message = "Password atleast 8 characters and atmost 20 characters")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$", message = "Password must contain uppercase, lowercase, number, and special character")
	private String password;
}

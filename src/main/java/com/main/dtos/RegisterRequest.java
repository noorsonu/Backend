package com.main.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email")
    private String email;

    @NotBlank(message = "Phone no must not be blank")
    private String phoneNumber;

    @NotBlank(message = "Name no must not be blank")
    private String name;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String userType;
}

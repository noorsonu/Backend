package com.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateUserRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String role; // ADMIN or USER
}
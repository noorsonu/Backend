package com.main.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminSingleConfirmDto {
	
    @NotBlank
    private String channel; // "phone" or "email"
    @NotBlank
    private String code;

    private String newName;
    private String newUsername;
    private String newEmail;
    private String newPassword;
    private String newPhoneNumber;
}

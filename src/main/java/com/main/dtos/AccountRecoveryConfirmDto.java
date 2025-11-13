package com.main.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountRecoveryConfirmDto {
	
    @NotBlank
    private String by; // phone or email
    @NotBlank
    private String identifier; // value for lookup
    @NotBlank
    private String code;

    private String newName;
    private String newUsername;
    private String newEmail;
    private String newPassword;
    private String newPhoneNumber;
}

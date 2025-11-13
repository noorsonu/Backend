package com.main.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetPhoneRequest {
	
    @NotBlank
    private String phoneNumber;
}

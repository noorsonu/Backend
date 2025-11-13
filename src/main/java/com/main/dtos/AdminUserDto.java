package com.main.dtos;

import com.main.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {
	
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private Instant createdAt;
    private long postCount;
    private long commentCount;
}
package com.main.dtos;

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
    private String userType;
    private boolean blocked;
    private Instant createdAt;
    private long postCount;
    private long commentCount;
}
package com.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCommentDto {
	
    private Long id;
    private String content;
    private String postTitle;
    private String userName;
    private String userEmail;
    private Instant createdAt;
    private Instant updatedAt;
}
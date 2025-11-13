package com.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostDto {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String authorEmail;
    private Instant createdAt;
    private Instant updatedAt;
    private long commentCount;
}
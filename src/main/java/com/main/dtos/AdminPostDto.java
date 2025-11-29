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
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private long commentCount;
    private boolean liked = false;
    private int likeCount = 0;

    // Convenience constructor without the "liked" flag
    public AdminPostDto(Long id,
                        String title,
                        String content,
                        String authorName,
                        String authorEmail,
                        String imageUrl,
                        Instant createdAt,
                        Instant updatedAt,
                        long commentCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentCount = commentCount;
        // "liked" remains at its default value (false)
    }
}

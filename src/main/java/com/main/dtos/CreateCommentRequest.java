package com.main.dtos;

import lombok.Data;

@Data
public class CreateCommentRequest {
	
    private String content;
    private Long parentCommentId;
    private String replyToUser;
    private String authorName;
}

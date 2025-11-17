package com.main.services;

import com.main.dtos.CommentResponseDto;
import com.main.entities.Comment;
import com.main.entities.Post;
import com.main.entities.UserEntity;

import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(Post post, UserEntity user, String content);
    CommentResponseDto updateComment(Long commentId, UserEntity currentUser, boolean isAdmin, String content);
    void deleteComment(Long commentId, UserEntity currentUser, boolean isAdmin);
    List<CommentResponseDto> listByPost(Long postId);
    List<CommentResponseDto> listByUserId(Long userId);
    List<CommentResponseDto> listByUserName(String name);
    void deleteAllByUserId(Long userId);
}

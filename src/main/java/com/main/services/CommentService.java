package com.main.services;

import com.main.entities.Comment;
import com.main.entities.Post;
import com.main.entities.UserEntity;

import java.util.List;

public interface CommentService {
    Comment addComment(Post post, UserEntity user, String content);
    Comment updateComment(Long commentId, UserEntity currentUser, boolean isAdmin, String content);
    void deleteComment(Long commentId, UserEntity currentUser, boolean isAdmin);
    List<Comment> listByPost(Long postId);
    List<Comment> listByUserId(Long userId);
    List<Comment> listByUserName(String name);
    void deleteAllByUserId(Long userId);
}

package com.main.services;

import com.main.dtos.UserPublicDto;
import com.main.entities.UserEntity;

import java.util.List;

public interface LikeService {
    void toggleLike(Long postId, UserEntity user);
    List<UserPublicDto> getLikesForPost(Long postId);
    boolean isPostLikedByUser(Long postId, UserEntity user);
}

package com.main.services;

import com.main.dtos.*;
import java.util.List;

public interface AdminService {

    AdminDashboardStatsDto getDashboardStats();

    List<AdminUserDto> getAllUsersWithStats();

    AdminUserDto getUserWithStats(Long userId);

    AdminUserDto updateUser(Long userId, AdminUpdateUserRequest updateRequest);

    List<AdminPostDto> getAllPostsWithStats();

    AdminPostDto getPostWithStats(Long postId);

    void deletePost(Long postId);

    List<AdminCommentDto> getAllCommentsWithDetails();

    void deleteComment(Long commentId);

    void deleteUser(Long userId);
}
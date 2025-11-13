package com.main.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDto {
    private long totalUsers;
    private long totalPosts;
    private long totalComments;
    private long totalAdmins;
    private long usersToday;
    private long postsToday;
    private long commentsToday;
}
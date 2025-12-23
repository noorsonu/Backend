package com.main.serviceImpls;

import com.main.dtos.*;
import com.main.entities.Comment;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.CommentRepository;
import com.main.repositories.PostRepository;
import com.main.repositories.UserRepository;
import com.main.repositories.LikeRepository;
import com.main.repositories.ImageRepository;
import com.main.services.AdminService;
import com.main.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FileStorageService storageService;

    @Override
    public AdminDashboardStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalComments = commentRepository.count();
        long totalAdmins = userRepository.countByUserType("ADMIN");
        
        // Get today's start time in UTC
        Instant todayStart = java.time.LocalDate.now(java.time.ZoneOffset.UTC).atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        
        long usersToday = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(todayStart))
                .count();
        
        long postsToday = postRepository.findAll().stream()
                .filter(post -> post.getCreatedAt().isAfter(todayStart))
                .count();
        
        long commentsToday = commentRepository.findAll().stream()
                .filter(comment -> comment.getCreatedAt().isAfter(todayStart))
                .count();

        return new AdminDashboardStatsDto(totalUsers, totalPosts, totalComments, totalAdmins, 
                                        usersToday, postsToday, commentsToday);
    }

    @Override
    public List<AdminUserDto> getAllUsersWithStats() {
        return userRepository.findAll().stream()
                .map(this::convertToAdminUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminUserDto getUserWithStats(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToAdminUserDto(user);
    }

    @Override
    public AdminUserDto updateUser(Long userId, AdminUpdateUserRequest updateRequest) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getUserType() != null) {
            user.setUserType(updateRequest.getUserType());
        }

        UserEntity updatedUser = userRepository.save(user);
        return convertToAdminUserDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminPostDto> getAllPostsWithStats() {
        List<Post> posts = postRepository.findAll();
        if (posts == null || posts.isEmpty()) {
            return Collections.emptyList();
        }
        return posts.stream()
                .map(post -> {
                    // Force load images to avoid lazy loading issues
                    if (post.getImages() != null) {
                        post.getImages().size(); // This triggers lazy loading
                    }
                    return convertToAdminPostDto(post);
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public AdminPostDto getPostWithStats(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return convertToAdminPostDto(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        // Load post or throw if not found (will be mapped to 404/400 by controller)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 1) Delete likes for this post (FK: likes.post_id)
        java.util.List<com.main.entities.Like> likes = likeRepository.findByPost(post);
        if (!likes.isEmpty()) {
            likeRepository.deleteAll(likes);
        }

        // 2) Delete comments linked to this post (FK: comments.post_id)
        java.util.List<com.main.entities.Comment> comments = commentRepository.findByPost_Id(postId);
        if (!comments.isEmpty()) {
            commentRepository.deleteAll(comments);
        }

        // 3) Delete images + physical files for this post
        java.util.List<com.main.entities.Image> images = imageRepository.findByPost_Id(postId);
        for (com.main.entities.Image img : images) {
            // delete file from storage if present
            if (img.getFileName() != null) {
                storageService.delete(img.getFileName());
            }
        }
        if (!images.isEmpty()) {
            imageRepository.deleteAll(images);
        }

        // 4) Now safely delete the post itself
        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminCommentDto> getAllCommentsWithDetails() {
        try {
            List<Comment> comments = commentRepository.findAll();
            return comments.stream()
                    .map(this::convertToAdminCommentDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getAllCommentsWithDetails: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    private AdminUserDto convertToAdminUserDto(UserEntity user) {
        long postCount = postRepository.countByCreatedBy(user);
        long commentCount = commentRepository.countByUser(user);
        
        return new AdminUserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUserType(),
                user.isBlocked(),
                user.getCreatedAt(),
                postCount,
                commentCount
        );
    }

    private AdminPostDto convertToAdminPostDto(Post post) {
        long commentCount = commentRepository.countByPost(post);

        String authorName = null;
        String authorEmail = null;
        UserEntity createdBy = post.getCreatedBy();
        if (createdBy != null) {
            authorName = createdBy.getName();
            authorEmail = createdBy.getEmail();
        }

        // Get image URL from post's images
        String imageUrl = null;
        
        // Direct database query for images
        List<com.main.entities.Image> images = imageRepository.findByPost_Id(post.getId());
        if (!images.isEmpty()) {
            // Use the stored URL from the Image entity instead of constructing manually
            imageUrl = images.get(0).getUrl();
        }

        return new AdminPostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                authorName,
                authorEmail,
                imageUrl,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                commentCount
        );
    }

    private AdminCommentDto convertToAdminCommentDto(Comment comment) {
        try {
            String userName = "Anonymous";
            String userEmail = "Not provided";
            String userPhone = "Not provided";
            
            if (comment.getUser() != null) {
                userName = comment.getUser().getName() != null ? comment.getUser().getName() : "Anonymous";
                userEmail = comment.getUser().getEmail() != null ? comment.getUser().getEmail() : "Not provided";
                userPhone = comment.getUser().getPhoneNumber() != null ? comment.getUser().getPhoneNumber() : "Not provided";
            } else if (comment.getAuthorName() != null) {
                userName = comment.getAuthorName();
            }
            
            // Safely get post information
            String postTitle = "Unknown Post";
            Long postId = null;
            try {
                if (comment.getPost() != null) {
                    postTitle = comment.getPost().getTitle();
                    postId = comment.getPost().getId();
                }
            } catch (Exception e) {
                System.err.println("Error accessing post for comment " + comment.getId() + ": " + e.getMessage());
            }
            
            Long parentCommentId = null;
            if (comment.getParentComment() != null) {
                parentCommentId = comment.getParentComment().getId();
            }

            return new AdminCommentDto(
                    comment.getId(),
                    comment.getContent(),
                    postTitle,
                    postId,
                    userName,
                    userEmail,
                    userPhone,
                    parentCommentId,
                    comment.getReplyToUser(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt()
            );
        } catch (Exception e) {
            System.err.println("Error converting comment " + comment.getId() + " to DTO: " + e.getMessage());
            // Return a basic DTO with minimal info
            Long parentCommentId = null;
            if (comment.getParentComment() != null) {
                parentCommentId = comment.getParentComment().getId();
            }

            return new AdminCommentDto(
                    comment.getId(),
                    comment.getContent() != null ? comment.getContent() : "Error loading content",
                    "Unknown Post",
                    null,
                    "Unknown User",
                    "Not provided",
                    "Not provided",
                    parentCommentId,
                    comment.getReplyToUser(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt()
            );
        }
    }

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// 1) Delete user's comments using existing method
		commentRepository.deleteByUser_Id(userId);

		// 2) Delete user's posts (this will cascade to images, likes, comments)
		java.util.List<com.main.entities.Post> userPosts = postRepository.findByCreatedBy(user);
		for (com.main.entities.Post post : userPosts) {
			deletePost(post.getId());
		}

		// 3) Finally delete the user
		userRepository.delete(user);
	}
}
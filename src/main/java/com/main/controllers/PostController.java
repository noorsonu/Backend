package com.main.controllers;

import com.main.dtos.AdminPostDto;
import com.main.dtos.CreatePostRequest;
import com.main.dtos.UpdatePostRequest;
import com.main.dtos.UserPublicDto;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.UserRepository;
import com.main.services.AdminService;
import com.main.services.ImageService;
import com.main.services.LikeService;
import com.main.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private com.main.repositories.ImageRepository imageRepository;

	@Autowired
	private ImageService imageService;

	@Autowired
	private LikeService likeService;

	private UserEntity currentUser(Authentication auth) {
		return userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
	}

	private UserEntity getCurrentUserOrNull(Authentication auth) {
		if (auth == null || auth.getName() == null) {
			return null;
		}
		return userRepository.findByEmail(auth.getName()).orElse(null);
	}

	// Admin CRUD (multipart: title, content, optional image)
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(value = "/admin/posts", consumes = { "multipart/form-data" })
	public ResponseEntity<?> create(@RequestParam("title") String title, @RequestParam("content") String content,
			@RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
			Authentication auth) {
		UserEntity admin = currentUser(auth);

		Post p = postService.createPost(title, content, admin);
		com.main.entities.Image img = null;
		if (image != null && !image.isEmpty()) {
			img = imageService.addImageToPost(p, admin, image);
		}
		java.util.Map<String, Object> body = new java.util.HashMap<>();
		body.put("message", "Post created successfully");
		body.put("post", p);
		if (img != null) {
			body.put("image", img);
			body.put("imageUploaded", true);
		} else {
			body.put("imageUploaded", false);
		}
		return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(body);
	}

	@GetMapping("/posts")
	public ResponseEntity<?> getAllPostsForAdmin(Authentication auth) {
		try {
			List<AdminPostDto> posts = adminService.getAllPostsWithStats();
			UserEntity currentUser = getCurrentUserOrNull(auth);
			
			// Add like status and counts for each post
			for (AdminPostDto post : posts) {
				if (currentUser != null) {
					boolean isLiked = likeService.isPostLikedByUser(post.getId(), currentUser);
					post.setLiked(isLiked);
				}
				int likeCount = likeService.getLikesForPost(post.getId()).size();
				post.setLikeCount(likeCount);
			}
			
			return ResponseEntity.ok(posts);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching posts");
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping(value = "/admin/posts/{postId}", consumes = { "multipart/form-data" })
	public ResponseEntity<?> update(@PathVariable Long postId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
			Authentication auth) {
		UserEntity admin = currentUser(auth);
		Post p = postService.updatePost(postId, title, content, admin);
		if (image != null && !image.isEmpty()) {
			java.util.List<com.main.entities.Image> existing = imageRepository.findByPost_Id(postId);
			for (com.main.entities.Image ex : existing) {
				imageService.deleteImage(ex.getId());
			}
			com.main.entities.Image img = imageService.addImageToPost(p, admin, image);
			return ResponseEntity.ok(java.util.Map.of("post", p, "image", img));
		}
		return ResponseEntity.ok(java.util.Map.of("post", p));
	}

	@PostMapping("/posts/{postId}/like")
	public ResponseEntity<?> toggleLikePost(@PathVariable Long postId, Authentication auth) {
		try {
			if (auth == null || auth.getName() == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
			}
			UserEntity user = currentUser(auth);
			likeService.toggleLike(postId, user);
			boolean isLiked = likeService.isPostLikedByUser(postId, user);
			return ResponseEntity.ok(Map.of("message", "Post like status toggled successfully", "isLiked", isLiked));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error toggling like status"));
		}
	}

	@GetMapping("/posts/{postId}/likes")
	public ResponseEntity<?> getLikesForPost(@PathVariable Long postId) {
		try {
			List<UserPublicDto> likedUsers = likeService.getLikesForPost(postId);
			return ResponseEntity.ok(likedUsers);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error fetching likes"));
		}
	}

	@GetMapping("/posts/{postId}/liked")
	public ResponseEntity<?> isPostLiked(@PathVariable Long postId, Authentication auth) {
		try {
			if (auth == null || auth.getName() == null) {
				return ResponseEntity.ok(Map.of("isLiked", false));
			}
			UserEntity user = currentUser(auth);
			boolean isLiked = likeService.isPostLikedByUser(postId, user);
			return ResponseEntity.ok(Map.of("isLiked", isLiked));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error checking like status"));
		}
	}

	@GetMapping("/posts/search")
	public ResponseEntity<List<Post>> searchPosts(@RequestParam("query") String query) {
		List<Post> posts = postService.searchPosts(query);
		return ResponseEntity.ok(posts);
	}
}

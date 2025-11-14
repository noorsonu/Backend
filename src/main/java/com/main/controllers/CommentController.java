package com.main.controllers;

import com.main.dtos.CreateCommentRequest;
import com.main.dtos.UpdateCommentRequest;
import com.main.entities.Comment;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.PostRepository;
import com.main.repositories.UserRepository;
import com.main.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Add a comment to an admin post (any authenticated user)
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CreateCommentRequest req, Authentication auth) {
        UserEntity user = currentUser(auth);
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Comment c = commentService.addComment(post, user, req.getContent());
        return ResponseEntity.ok(c);
    }

    // List comments by post
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> listByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.listByPost(postId));
    }

    // Update own comment (or admin any)
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody UpdateCommentRequest req, Authentication auth) {
        UserEntity user = currentUser(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Comment updated = commentService.updateComment(commentId, user, isAdmin, req.getContent());
        return ResponseEntity.ok(updated);
    }

    // Delete own comment (or admin any)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication auth) {
        UserEntity user = currentUser(auth);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        commentService.deleteComment(commentId, user, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Comment deleted"));
    }

    // ADMIN: get comments by user id or name
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/comments/search")
    public ResponseEntity<?> adminGetComments(@RequestParam(required = false) Long userId,
                                              @RequestParam(required = false) String name) {
        if (userId != null) {
            return ResponseEntity.ok(commentService.listByUserId(userId));
        }
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(commentService.listByUserName(name));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Provide userId or name"));
    }

    // ADMIN: delete ALL comments by a user (e.g., blocked user)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/comments/user/{userId}")
    public ResponseEntity<?> adminDeleteByUser(@PathVariable Long userId) {
        commentService.deleteAllByUserId(userId);
        return ResponseEntity.ok(Map.of("message", "All comments by user deleted"));
    }
}

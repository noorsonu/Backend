package com.main.controllers;

import com.main.dtos.CreatePostRequest;
import com.main.dtos.UpdatePostRequest;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.enums.Role;
import com.main.repositories.UserRepository;
import com.main.services.PostService;
import com.main.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.main.repositories.ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    private UserEntity currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Public reads
//    @GetMapping("/posts")
//    public ResponseEntity<List<Post>> listPosts() {
//        return ResponseEntity.ok(postService.listAll());
//    }
//
//    @GetMapping("/posts/{id}")
//    public ResponseEntity<Post> getPost(@PathVariable Long id) {
//        return ResponseEntity.ok(postService.getById(id));
//    }




    // Admin CRUD (multipart: title, content, optional image)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin/posts", consumes = { "multipart/form-data" })
    public ResponseEntity<?> create(@RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile image,
                                    Authentication auth) {
        UserEntity admin = currentUser(auth);
        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        Post p = postService.createPost(title, content, admin);
        com.main.entities.Image img = null;
        if (image != null && !image.isEmpty()) {
            img = imageService.addImageToPost(p, admin, image);
        }
        return ResponseEntity.ok(java.util.Map.of("post", p, "image", img));
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
}

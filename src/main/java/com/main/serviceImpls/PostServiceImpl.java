package com.main.serviceImpls;

import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.PostRepository;
import com.main.repositories.ImageRepository;
import com.main.services.PostService;
import com.main.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FileStorageService storageService;

    @Override
    public Post createPost(String title, String content, UserEntity admin) {
        Post p = new Post();
        p.setTitle(title);
        p.setContent(content);
        p.setCreatedBy(admin);
        return postRepository.save(p);
    }

    @Override
    public Post updatePost(Long postId, String title, String content, UserEntity admin) {
        Post p = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        // ensure only creator admin can update
        if (!p.getCreatedBy().getId().equals(admin.getId())) {
            throw new RuntimeException("Not allowed to update this post");
        }
        if (title != null && !title.isBlank()) p.setTitle(title);
        if (content != null && !content.isBlank()) p.setContent(content);
        return postRepository.save(p);
    }

    @Override
    public void deletePost(Long postId, UserEntity admin) {
        Post p = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!p.getCreatedBy().getId().equals(admin.getId())) {
            throw new RuntimeException("Not allowed to delete this post");
        }
        // delete associated images first (files + DB)
        java.util.List<com.main.entities.Image> imgs = imageRepository.findByPost_Id(postId);
        for (com.main.entities.Image img : imgs) {
            storageService.delete(img.getFileName());
        }
        imageRepository.deleteByPost_Id(postId);
        postRepository.delete(p);
    }

    @Override
    public List<Post> listAll() {
        return postRepository.findAll();
    }

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of(); // Or throw an exception, depending on desired behavior
        }
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Post> filterPosts(String categoryName, String tagName, String authorEmail) {
        return postRepository.findByFilters(categoryName, tagName, authorEmail);
    }
}

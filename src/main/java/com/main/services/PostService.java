package com.main.services;

import com.main.entities.Post;
import com.main.entities.UserEntity;

import java.util.List;

public interface PostService {
	
    Post createPost(String title, String content, UserEntity admin);
    
    Post updatePost(Long postId, String title, String content, UserEntity admin);
    
    void deletePost(Long postId, UserEntity admin);
    
    List<Post> listAll();
    
    Post getById(Long id);
    List<Post> searchPosts(String keyword);
    List<Post> filterPosts(String categoryName, String tagName, String authorEmail);
   
}



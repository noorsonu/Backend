package com.main.repositories;

import com.main.entities.Comment;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
    List<Comment> findByPost_Id(Long postId);
    List<Comment> findByUser_Id(Long userId);
    List<Comment> findByUser_NameIgnoreCase(String name);
    void deleteByUser_Id(Long userId);
    long countByUser(UserEntity user);
    long countByPost(Post post);
}

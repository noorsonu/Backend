package com.main.repositories;

import com.main.entities.Post;
import com.main.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCreatedBy(UserEntity user);
    long countByCreatedBy(UserEntity user);
}

package com.main.repositories;

import com.main.entities.Like;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(UserEntity user, Post post);
    List<Like> findByPost(Post post);
    void deleteByUserAndPost(UserEntity user, Post post);
    long countByPost(Post post);
}

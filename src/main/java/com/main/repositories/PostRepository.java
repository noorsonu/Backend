package com.main.repositories;

import com.main.entities.Post;
import com.main.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCreatedBy(UserEntity user);
    long countByCreatedBy(UserEntity user);
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String titleKeyword, String contentKeyword);

    @Query("SELECT p FROM Post p JOIN p.category c JOIN p.tags t JOIN p.createdBy u " +
           "WHERE (:categoryName IS NULL OR c.name = :categoryName) " +
           "AND (:tagName IS NULL OR t.name = :tagName) " +
           "AND (:authorEmail IS NULL OR u.email = :authorEmail)")
    List<Post> findByFilters(@Param("categoryName") String categoryName,
                             @Param("tagName") String tagName,
                             @Param("authorEmail") String authorEmail);
}

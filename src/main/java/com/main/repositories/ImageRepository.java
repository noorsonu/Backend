package com.main.repositories;

import com.main.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPost_Id(Long postId);
    void deleteByPost_Id(Long postId);
}

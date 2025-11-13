package com.main.services;

import com.main.entities.Image;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
	
    Image addImageToPost(Post post, UserEntity admin, MultipartFile file);
    List<Image> listImagesByPost(Long postId);
    void deleteImage(Long imageId);
    
}
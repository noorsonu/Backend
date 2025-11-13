package com.main.serviceImpls;

import com.main.entities.Image;
import com.main.entities.Post;
import com.main.entities.UserEntity;
import com.main.repositories.ImageRepository;
import com.main.services.FileStorageService;
import com.main.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FileStorageService storageService;

    @Override
    public Image addImageToPost(Post post, UserEntity admin, MultipartFile file) {
        String url = storageService.store(file);
        Image img = new Image();
        img.setPost(post);
        img.setUploadedBy(admin);
        img.setUrl(url);
        img.setFileName(storageService.getFileNameFromUrl(url));
        img.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        img.setSize(file.getSize());
        return imageRepository.save(img);
    }

    @Override
    public List<Image> listImagesByPost(Long postId) {
        return imageRepository.findByPost_Id(postId);
    }

    @Override
    public void deleteImage(Long imageId) {
        Image img = imageRepository.findById(imageId).orElse(null);
        if (img != null) {
            String fileName = img.getFileName();
            storageService.delete(fileName);
            imageRepository.delete(img);
        }
    }
}

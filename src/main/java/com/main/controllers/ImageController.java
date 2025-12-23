package com.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.main.repositories.ImageRepository;
import com.main.entities.Image;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ImageController {

    private final Path uploadPath = Paths.get("uploads");
    
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // First try to serve the file directly if it has an extension
            if (filename.contains(".")) {
                Path file = uploadPath.resolve(filename);
                Resource resource = new UrlResource(file.toUri());
                
                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                            .contentType(MediaType.IMAGE_PNG)
                            .body(resource);
                }
            } else {
                // If no extension, try to find the file in database by UUID
                List<Image> images = imageRepository.findAll();
                for (Image img : images) {
                    String fileName = img.getFileName();
                    if (fileName != null && fileName.startsWith(filename)) {
                        Path file = uploadPath.resolve(fileName);
                        Resource resource = new UrlResource(file.toUri());
                        
                        if (resource.exists() || resource.isReadable()) {
                            return ResponseEntity.ok()
                                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                                    .contentType(MediaType.IMAGE_PNG)
                                    .body(resource);
                        }
                    }
                }
            }
            
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
package com.main.serviceImpls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.main.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@Primary
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            // Generate unique public_id
            String publicId = "yaallah/" + UUID.randomUUID().toString();
            
            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "yaallah",
                    "resource_type", "auto",
                    "quality", "auto:good",
                    "fetch_format", "auto"
                )
            );

            // Return the secure URL
            return (String) uploadResult.get("secure_url");
            
        } catch (IOException e) {
            throw new RuntimeException("Could not upload file to Cloudinary", e);
        }
    }

    @Override
    public String getFileNameFromUrl(String url) {
        if (url == null) return null;
        
        // Extract public_id from Cloudinary URL
        // Example: https://res.cloudinary.com/demo/image/upload/v1234567890/yaallah/abc123.jpg
        try {
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                // Remove file extension if present
                int dotIndex = lastPart.lastIndexOf('.');
                return dotIndex > 0 ? lastPart.substring(0, dotIndex) : lastPart;
            }
        } catch (Exception e) {
            // Fallback to extracting from URL
        }
        
        int idx = url.lastIndexOf('/');
        return idx >= 0 ? url.substring(idx + 1) : url;
    }

    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        
        try {
            // If fileName doesn't contain folder, add yaallah prefix
            String publicId = fileName.contains("/") ? fileName : "yaallah/" + fileName;
            
            // Delete from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Failed to delete file from Cloudinary: " + fileName + ", Error: " + e.getMessage());
        }
    }
}
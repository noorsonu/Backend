package com.main.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    // Stores file and returns the public URL
    String store(MultipartFile file);
    // Returns the stored file name (last segment of URL)
    String getFileNameFromUrl(String url);
    // Delete a stored file by name (not URL)
    void delete(String fileName);
}

package com.example.lostandfound.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "/home/sharon/Uploads/"; // specify your path

    // Method to handle file uploads
    public String uploadFile(MultipartFile file) throws IOException {
        // Generate a unique filename (e.g., based on timestamp)
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Create file path (combine UPLOAD_DIR and filename)
        Path filePath = Paths.get(UPLOAD_DIR, filename);

        // Ensure the directory exists
        Files.createDirectories(filePath.getParent());

        // Save the file
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not store file " + filename + ". Please try again!", e);
        }

        return filename; // Returning the file name (or you could return the full path)
    }
}

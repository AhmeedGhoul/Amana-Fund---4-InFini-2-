package com.ghoul.AmanaFund.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final String UPLOAD_DIR = "uploads/";  // Folder where PDFs will be stored

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty!");
        }

        // Create folder if it doesn't exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save file with unique name
        String filePath = UPLOAD_DIR + file.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        return filePath;  // Return the saved file path
    }
}

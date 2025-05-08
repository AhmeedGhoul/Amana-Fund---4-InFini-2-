package com.ghoul.AmanaFund.controller;

import com.ghoul.AmanaFund.Dao.AuthenticationResponse;
import com.ghoul.AmanaFund.service.FaceRecognitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/face-auth")
@RequiredArgsConstructor
public class FaceRecognitionController {
    private final FaceRecognitionService faceService;

    @PostMapping("/register")
    public ResponseEntity<String> registerFace(@RequestParam("userId") int userId, @RequestParam("image") MultipartFile image) throws IOException {
        faceService.registerFace(userId, image);
        return ResponseEntity.ok("Face registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginWithFace(@RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(faceService.authenticateWithFace(image));
    }
}

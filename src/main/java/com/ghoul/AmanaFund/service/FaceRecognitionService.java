package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.Dao.AuthenticationResponse;
import com.ghoul.AmanaFund.entity.FaceRecognitionData;
import com.ghoul.AmanaFund.entity.Users;
import com.ghoul.AmanaFund.repository.FaceRecognitionRepository;
import com.ghoul.AmanaFund.repository.UserRepository;
import com.ghoul.AmanaFund.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceRecognitionService {
    private final FaceRecognitionRepository faceRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PythonFaceApiService pythonFaceApiService; // Call to Python API

    @Transactional
    public void registerFace(int userId, MultipartFile image) throws IOException {
        byte[] encoded = pythonFaceApiService.extractEncoding(image); // Use REST call to Python
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FaceRecognitionData data = new FaceRecognitionData();
        data.setUser(user);
        data.setFaceEncoding(encoded);
        faceRepository.save(data);
    }

    public AuthenticationResponse authenticateWithFace(MultipartFile image) throws IOException {
        byte[] inputEncoding = pythonFaceApiService.extractEncoding(image);
        List<FaceRecognitionData> faces = faceRepository.findAll();

        for (FaceRecognitionData stored : faces) {
            if (pythonFaceApiService.compareFaces(inputEncoding, stored.getFaceEncoding())) {
                Users user = stored.getUser();
                var claims = new HashMap<String, Object>();
                claims.put("fullName", user.getFirstName() + " " + user.getLastName());
                String token = jwtService.generateToken(claims, user);
                return AuthenticationResponse.builder()
                        .token(token)
                        .build();
            }
        }

        throw new RuntimeException("Face not recognized");
    }
}

package com.ghoul.AmanaFund.repository;


import com.ghoul.AmanaFund.entity.FaceRecognitionData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaceRecognitionRepository extends JpaRepository<FaceRecognitionData, Long> {
}
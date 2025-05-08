package com.ghoul.AmanaFund.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FaceRecognitionData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] faceEncoding; // Or a JSON string if you use face landmarks or embeddings

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
}

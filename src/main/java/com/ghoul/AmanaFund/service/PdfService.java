package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.repository.SinistreRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final SinistreRepository sinistreRepository;

    public byte[] generateSinistrePdf(Long idSinistre) throws Exception {
        Optional<Sinistre> optionalSinistre = sinistreRepository.findById(idSinistre);
        if (optionalSinistre.isEmpty()) {
            throw new RuntimeException("Sinistre non trouvé avec l'ID : " + idSinistre);
        }

        Sinistre sinistre = optionalSinistre.get();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Ajouter le titre
        document.add(new Paragraph("Détails du Sinistre").setBold().setFontSize(16));

        // Ajouter les détails du sinistre
        document.add(new Paragraph("ID Sinistre : " + sinistre.getIdSinistre()));
        document.add(new Paragraph("Montant de la Réclamation : " + sinistre.getClaimAmount() + " TND"));
        document.add(new Paragraph("Part de Réassurance : " + (sinistre.getReinsuranceShaire() * 100) + "%"));
        document.add(new Paragraph("Date de Règlement : " + sinistre.getSettlementDate()));
        document.add(new Paragraph("Montant du Règlement : " + sinistre.getSettlementAmount() + " TND"));

        // Ajouter les détails de l'utilisateur si disponibles
        if (sinistre.getUser() != null) {
            document.add(new Paragraph("Utilisateur : " + sinistre.getUser().fullName()));
        }

        document.close();
        return baos.toByteArray();
    }
}

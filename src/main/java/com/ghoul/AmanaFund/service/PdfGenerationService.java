package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Contract;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationService {

    public byte[] generatePdf(Contract contract) {
        // Créer un document PDF
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Associer le document à un flux de sortie
            PdfWriter.getInstance(document, outputStream);

            // Ouvrir le document
            document.open();

            // Ajouter du contenu au PDF
            document.add(new Paragraph("Contrat de Crédit"));
            document.add(new Paragraph(" ")); // Ligne vide
            document.add(new Paragraph("ID du Contrat: " + contract.getId_Contract()));
            document.add(new Paragraph("Date du Contrat: " + contract.getDate_Contract().format(DateTimeFormatter.ISO_DATE)));
            document.add(new Paragraph("Montant du Contrat: " + contract.getAmount()));
            document.add(new Paragraph("Montant Payé: " + contract.getPayed()));
            document.add(new Paragraph("Date de Retrait: " + contract.getWithdrawal_date().format(DateTimeFormatter.ISO_DATE)));
            document.add(new Paragraph("Numéro de File: " + contract.getQueue_Number()));
            document.add(new Paragraph(" ")); // Ligne vide
            document.add(new Paragraph("Informations sur l'utilisateur:"));
            document.add(new Paragraph("Nom de l'utilisateur: " + contract.getUser().getName()));
            document.add(new Paragraph("Email de l'utilisateur: " + contract.getUser().getEmail()));

            // Fermer le document
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }

        // Retourner le PDF sous forme de tableau de bytes
        return outputStream.toByteArray();
    }
}
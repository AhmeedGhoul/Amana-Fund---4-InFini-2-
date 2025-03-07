package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Sinistre;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class excelSinistre{
    private final SinistreService sinistreService;

    public excelSinistre(SinistreService sinistreService) {
        this.sinistreService = sinistreService;
    }

    public byte[] generateSinistresExcelForUser(Long userId) throws IOException {
        List<Sinistre> sinistres = sinistreService.getSinistresByUserId(userId);

        if (sinistres.isEmpty()) {
            throw new IOException("Aucun sinistre trouvé pour cet utilisateur");
        }

        // Création d'un fichier Excel avec Apache POI
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sinistres");

        // Création de la ligne d'en-têtes
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID Sinistre");
        header.createCell(1).setCellValue("Montant de la Réclamation");
        header.createCell(2).setCellValue("Part de Réassurance");
        header.createCell(3).setCellValue("Date de Règlement");
        header.createCell(4).setCellValue("Montant du Règlement");

        // Remplissage des données
        int rowNum = 1;
        for (Sinistre sinistre : sinistres) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sinistre.getIdSinistre());
            row.createCell(1).setCellValue(sinistre.getClaimAmount());
            row.createCell(2).setCellValue(sinistre.getReinsuranceShaire());
            row.createCell(3).setCellValue(sinistre.getSettlementDate().toString());
            row.createCell(4).setCellValue(sinistre.getSettlementAmount());
        }

        // Écriture dans un flux de sortie
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return out.toByteArray();  // Retourne le fichier Excel sous forme de tableau d'octets
        } catch (IOException e) {
            throw new IOException("Erreur lors de la génération du fichier Excel", e);
        }
    }}
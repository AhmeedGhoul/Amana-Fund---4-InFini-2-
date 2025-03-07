package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Account;
import com.ghoul.AmanaFund.entity.AccountType;
import com.ghoul.AmanaFund.entity.Sinistre;
import com.ghoul.AmanaFund.repository.AccountRepository;
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
    private final AccountRepository accountRepository;

    // Génération PDF pour Sinistre (inchangé)
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

        document.add(new Paragraph("Détails du Sinistre").setBold().setFontSize(16));
        document.add(new Paragraph("ID Sinistre : " + sinistre.getIdSinistre()));
        document.add(new Paragraph("Montant de la Réclamation : " + sinistre.getClaimAmount() + " TND"));
        document.add(new Paragraph("Part de Réassurance : " + (sinistre.getReinsuranceShaire() * 100) + "%"));
        document.add(new Paragraph("Date de Règlement : " + sinistre.getSettlementDate()));
        document.add(new Paragraph("Montant du Règlement : " + sinistre.getSettlementAmount() + " TND"));

        if (sinistre.getUser() != null) {
            document.add(new Paragraph("Utilisateur : " + sinistre.getUser().fullName()));
        }

        document.close();
        return baos.toByteArray();
    }

    // Génération PDF pour l'historique Zakat
    public byte[] generateZakatHistoryPdf(Integer accountId) throws Exception {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new RuntimeException("Compte non trouvé avec l'ID : " + accountId);
        }

        Account account = optionalAccount.get();
        if (account.getAccountType() != AccountType.EPARGNE_ZEKET) {
            throw new RuntimeException("Ce compte n'est pas un compte Zakat");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Historique des Transactions Zakat").setBold().setFontSize(16));
        document.add(new Paragraph("RIB : " + account.getRib()));
        document.add(new Paragraph("Montant actuel : " + account.getAmount() + " TND"));
        document.add(new Paragraph("---------------------------------------"));

        if (account.getZakatTransactions().isEmpty()) {
            document.add(new Paragraph("Aucune transaction Zakat enregistrée."));
        } else {
            for (int i = 0; i < account.getZakatTransactions().size(); i++) {
                document.add(new Paragraph(
                        "Date : " + account.getZakatTransactionDates().get(i) +
                                " | Montant Zakat : " + account.getZakatTransactions().get(i) + " TND"
                ));
            }
        }

        document.close();
        return baos.toByteArray();
    }
}
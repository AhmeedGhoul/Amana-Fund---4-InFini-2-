package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Police;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PDFpoliceService {

    public byte[] generatePoliceContract(Police police, InputStream signatureImageStream, InputStream logoStream) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float pageWidth = page.getMediaBox().getWidth();
                float margin = 50;
                float contentWidth = pageWidth - 2 * margin;
                float yPosition = page.getMediaBox().getHeight() - margin;

                // Brand Colors
                PDColor darkBrown = new PDColor(new float[]{61 / 255f, 11 / 255f, 11 / 255f}, PDDeviceRGB.INSTANCE);
                PDColor goldenYellow = new PDColor(new float[]{211 / 255f, 153 / 255f, 24 / 255f}, PDDeviceRGB.INSTANCE);
                PDColor lightCream = new PDColor(new float[]{255 / 255f, 247 / 255f, 224 / 255f}, PDDeviceRGB.INSTANCE);

                // Logo (top-left)
                if (logoStream != null) {
                    PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo.png");
                    contentStream.drawImage(logo, margin, yPosition - 20, 80, 50);
                }

                // Date (top-right)
                contentStream.setNonStrokingColor(darkBrown);
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(pageWidth - margin - 140, yPosition - 20);
                contentStream.showText("Creation Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                contentStream.endText();

                // Title Block
                yPosition -= 90;
                drawSectionBackground(contentStream, margin, yPosition, contentWidth, 60, darkBrown);
                contentStream.setNonStrokingColor(goldenYellow);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                centerText(contentStream, "AMANA", pageWidth, yPosition + 40);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                centerText(contentStream, "Insurance Policy Contract", pageWidth, yPosition + 20);

                // Parties Section
                yPosition -= 80;
                drawSectionBackground(contentStream, margin, yPosition, contentWidth, 25, lightCream);
                contentStream.setNonStrokingColor(darkBrown);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                addLabel(contentStream, "Parties:", margin + 10, yPosition + 8);

                yPosition -= 25;
                contentStream.setFont(PDType1Font.HELVETICA, 11);
                addLabel(contentStream, "Client: " + police.getStart(), margin + 20, yPosition);
                yPosition -= 15;
                addLabel(contentStream, "Company: AMANA", margin + 20, yPosition);

                // Contract Details Section
                yPosition -= 40;
                drawSectionBackground(contentStream, margin, yPosition, contentWidth, 25, lightCream);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                addLabel(contentStream, "Contract Details:", margin + 10, yPosition + 8);

                yPosition -= 25;
                contentStream.setNonStrokingColor(darkBrown);
                addContractRow(contentStream, "Policy ID", String.valueOf(police.getIdPolice()), margin + 20, yPosition);
                yPosition -= 20;
                addContractRow(contentStream, "Premium", String.format("%.2f", police.getAmount()) + " $", margin + 20, yPosition);
                yPosition -= 20;
                addContractRow(contentStream, "Frequency", police.getFrequency().toString(), margin + 20, yPosition);
                yPosition -= 20;
                addContractRow(contentStream, "Start Date", police.getStart().toString(), margin + 20, yPosition);
                yPosition -= 20;
                addContractRow(contentStream, "End Date", police.getEnd().toString(), margin + 20, yPosition);

                // General Conditions Section
                yPosition -= 40;
                drawSectionBackground(contentStream, margin, yPosition, contentWidth, 25, lightCream);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                addLabel(contentStream, "General Conditions:", margin + 10, yPosition + 8);

                yPosition -= 25;
                contentStream.setNonStrokingColor(darkBrown);
                addBulletPoint(contentStream, "The contract takes effect from the signature date.", margin + 20, yPosition);
                yPosition -= 15;
                addBulletPoint(contentStream, "The premium is due upon activation of the contract.", margin + 20, yPosition);

                // Signature Section
                yPosition -= 60;
                drawSectionBackground(contentStream, margin, yPosition, contentWidth, 25, lightCream);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                addLabel(contentStream, "Signature:", margin + 10, yPosition + 8);

                if (signatureImageStream != null) {
                    PDImageXObject signatureImage = PDImageXObject.createFromByteArray(document, signatureImageStream.readAllBytes(), "signature.png");
                    contentStream.drawImage(signatureImage, pageWidth - margin - 120, yPosition - 40, 100, 40);

                    // Signature Line
                    contentStream.setStrokingColor(darkBrown);
                    contentStream.moveTo(pageWidth - margin - 120, yPosition - 5);
                    contentStream.lineTo(pageWidth - margin - 20, yPosition - 5);
                    contentStream.stroke();
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void addLabel(PDPageContentStream stream, String text, float x, float y) throws IOException {
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.endText();
    }

    private void addContractRow(PDPageContentStream stream, String label, String value, float x, float y) throws IOException {
        stream.setFont(PDType1Font.HELVETICA_BOLD, 11);
        addLabel(stream, label + ":", x, y);
        stream.setFont(PDType1Font.HELVETICA, 11);
        addLabel(stream, value, x + 120, y);
    }

    private void addBulletPoint(PDPageContentStream stream, String text, float x, float y) throws IOException {
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText("• " + text);
        stream.endText();
    }

    private void centerText(PDPageContentStream stream, String text, float pageWidth, float y) throws IOException {
        float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(text) / 1000 * 22;
        float centerX = (pageWidth - textWidth) / 2;
        stream.beginText();
        stream.newLineAtOffset(centerX, y);
        stream.showText(text);
        stream.endText();
    }

    private void drawSectionBackground(PDPageContentStream stream, float x, float y, float width, float height, PDColor color) throws IOException {
        stream.setNonStrokingColor(color);
        stream.addRect(x, y, width, height);
        stream.fill();
    }
}

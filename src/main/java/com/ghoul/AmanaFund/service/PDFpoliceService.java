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

import java.awt.*;
import java.io.ByteArrayOutputStream;
        import java.io.IOException;
import java.io.InputStream;

@Service
public class PDFpoliceService {

    public byte[] generatePoliceContract(Police police, InputStream signatureImageStream) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set margins and initial y-position
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float yPosition = yStart;

                // Define colors
                PDColor titleColor = new PDColor(new float[]{0.0f, 0.4f, 0.8f}, PDDeviceRGB.INSTANCE);
                PDColor lineColor = new PDColor(new float[]{0.8f, 0.8f, 0.8f}, PDDeviceRGB.INSTANCE);
                PDColor labelColor = new PDColor(new float[]{0.2f, 0.2f, 0.2f}, PDDeviceRGB.INSTANCE);

                // Title Section
                contentStream.setNonStrokingColor(titleColor);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Insurance Policy Contract");
                contentStream.endText();

                // Draw a line under the title
                yPosition -= 15;
                contentStream.setStrokingColor(lineColor);
                contentStream.setLineWidth(1.5f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();

                // Move below the line
                yPosition -= 40;

                // Policy Details Section
                contentStream.setNonStrokingColor(labelColor);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                addText(contentStream, "Policy ID: ", String.valueOf(police.getIdPolice()), margin, yPosition);
                yPosition -= 25;
                addText(contentStream, "Amount: ", "$" + police.getAmount(), margin, yPosition);
                yPosition -= 25;
                addText(contentStream, "Start Date: ", police.getStart().toString(), margin, yPosition);
                yPosition -= 25;
                addText(contentStream, "End Date: ", police.getEnd().toString(), margin, yPosition);
                yPosition -= 25;
                addText(contentStream, "Frequency: ", police.getFrequency().toString(), margin, yPosition);
                yPosition -= 40;

                // Signature Area
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Authorized Signature:");
                contentStream.endText();

                // Load signature image
                if (signatureImageStream != null) {
                    PDImageXObject signatureImage = PDImageXObject.createFromByteArray(document, signatureImageStream.readAllBytes(), "signature.png");
                    float imageWidth = 100;
                    float imageHeight = signatureImage.getHeight() * (imageWidth / signatureImage.getWidth());
                    contentStream.drawImage(signatureImage, margin + 150, yPosition - imageHeight, imageWidth, imageHeight);
                }

            }

            // Output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    // Helper method to format text as key-value pairs
    private void addText(PDPageContentStream contentStream, String label, String value, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(label);
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.showText(value);
        contentStream.endText();
        y -= 20;
    }

}


package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Request;
import com.ghoul.AmanaFund.repository.IrequestRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfGenerationService {

    @Autowired
    private IrequestRepository irequestRepository;

    public byte[] generatePdfForDateRange(LocalDate startDate, LocalDate endDate) throws IOException {
        List<Request> requests = irequestRepository.findByDateRange(startDate, endDate);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDocument);

        // Insert custom logo image in place of "Amana Fund"
        String imagePath = "C:/Users/soyre/Downloads/logo.png"; // Update with your actual image path
        Image logo = new Image(ImageDataFactory.create(imagePath));
        logo.setHeight(50)  // Set desired image height (adjust accordingly)
                .setWidth(150);   // Set desired image width (adjust accordingly)
        document.add(logo);

        // Add space between logo and title
        document.add(new Paragraph(" ")); // Add some space between logo and title

        // Request Report Title (Centered with red color)
        Paragraph requestReportTitle = new Paragraph("Request Report")
                .setFontSize(26)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(0, 102, 204));
        document.add(requestReportTitle);

        // Date Range
        Paragraph dateRange = new Paragraph("From " + startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                + " to " + endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setItalic()
                .setFontColor(new DeviceRgb(80, 80, 80));
        document.add(dateRange);

        document.add(new Paragraph(" "));

        // Table
        Table table = new Table(4);
        table.setWidth(500);

        // Table Header Styling
        table.addHeaderCell(createStyledCell("Request ID", true));
        table.addHeaderCell(createStyledCell("Product", true));
        table.addHeaderCell(createStyledCell("Request Date", true));
        table.addHeaderCell(createStyledCell("Document Description", true));

        // Table Rows (alternating colors)
        boolean isOddRow = true;
        for (Request request : requests) {
            table.addCell(createStyledCell(request.getId_request().toString(), false, isOddRow));
            table.addCell(createStyledCell(request.getProduct().toString(), false, isOddRow));
            table.addCell(createStyledCell(request.getDate_Request().format(DateTimeFormatter.ofPattern("dd MMM yyyy")), false, isOddRow));
            table.addCell(createStyledCell(request.getDocument(), false, isOddRow));
            isOddRow = !isOddRow;
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                + " | Contact: support@amanafund.com")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setFontColor(new DeviceRgb(120, 120, 120))
                .setItalic();
        document.add(footer);

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private Cell createStyledCell(String content, boolean isHeader) {
        return createStyledCell(content, isHeader, false);
    }

    private Cell createStyledCell(String content, boolean isHeader, boolean isOddRow) {
        DeviceRgb headerColor = new DeviceRgb(0, 153, 153);  // Blue-green header color
        DeviceRgb rowColor = isOddRow ? new DeviceRgb(230, 240, 255) : new DeviceRgb(255, 255, 255);

        Cell cell = new Cell().add(new Paragraph(content))
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12);

        if (isHeader) {
            cell.setBold().setFontColor(new DeviceRgb(255, 255, 255)).setBackgroundColor(headerColor);
        } else {
            cell.setBackgroundColor(rowColor);
        }

        return cell;
    }
}

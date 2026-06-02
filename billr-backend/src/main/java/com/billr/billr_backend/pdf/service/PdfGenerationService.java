package com.billr.billr_backend.pdf.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.billr.billr_backend.invoice.repository.InvoiceRepository;
import com.billr.billr_backend.invoice.model.Invoice;
import com.billr.billr_backend.invoice.model.InvoiceItem;

@Service
public class PdfGenerationService {

    private final InvoiceRepository invoiceRepository;

    public PdfGenerationService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Async
    public void generateInvoicePdf(Invoice invoice) {
        try {
            Files.createDirectories(Path.of("pdfs"));
            String fileName = "pdfs/invoice_" + invoice.getInvoiceNumber() + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.addTitle("Invoice " + invoice.getInvoiceNumber());
            document.addAuthor(invoice.getBusiness().getName());
            document.addSubject("Invoice for " + invoice.getClient().getName());

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

            document.add(new Paragraph("INVOICE", titleFont));
            document.add(new Paragraph("From: " + invoice.getBusiness().getName(), bodyFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Bill To: " + invoice.getClient().getName(), bodyFont));
            document.add(new Paragraph("Email: " + invoice.getClient().getEmail(), bodyFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber(), bodyFont));
            document.add(new Paragraph("Due Date: " + invoice.getDueDate(), bodyFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell(new PdfPCell(new Phrase("Description", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Quantity", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Unit Price", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Total", headerFont)));

            for (InvoiceItem item : invoice.getItems()) {
                table.addCell(new PdfPCell(new Phrase(item.getDescription(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(item.getQuantity().toString(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase("₹" + item.getUnitPrice(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase("₹" + item.getTotalPrice(), bodyFont)));
            }
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Amount: ₹" + invoice.getSubTotal(), bodyFont));
            document.add(new Paragraph("GST: ₹" + invoice.getGstAmount(), bodyFont));
            document.add(new Paragraph("Total: ₹" + invoice.getTotalAmount(), titleFont));

            document.close();

            invoice.setPdfUrl(fileName);
            invoiceRepository.save(invoice);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF for invoice " + invoice.getId(), e);
        }
    }
}

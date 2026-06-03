package com.billr.billr_backend.notification.service;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import com.billr.billr_backend.invoice.model.Invoice;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendInvoiceEmail(Invoice invoice) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(invoice.getClient().getEmail());
            message.setSubject("Invoice " + invoice.getInvoiceNumber() + " from " + invoice.getBusiness().getName());
            message.setText(
                    "Dear " + invoice.getClient().getName() + ",\n\n" +
                            "Please find your invoice details below:\n\n" +
                            "Invoice Number: " + invoice.getInvoiceNumber() + "\n" +
                            "Due Date: " + invoice.getDueDate() + "\n" +
                            "Amount: " + invoice.getSubTotal() + "\n" +
                            "GST: " + invoice.getGstAmount() + "\n" +
                            "Total: " + invoice.getTotalAmount() + "\n\n" +
                            "Please contact us if you have any questions.\n\n" +
                            "Regards,\n" +
                            invoice.getBusiness().getName());
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + invoice.getInvoiceNumber(), e);
        }
    }

    @Async
    public void sendOverdueReminderEmail(Invoice invoice) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(invoice.getClient().getEmail());
            message.setSubject("Payment Overdue - Invoice " + invoice.getInvoiceNumber());
            message.setText(
                    "Dear " + invoice.getClient().getName() + ",\n\n" +
                            "This is a reminder that the following invoice is overdue:\n\n" +
                            "Invoice Number: " + invoice.getInvoiceNumber() + "\n" +
                            "Due Date: " + invoice.getDueDate() + "\n" +
                            "Total Amount: ₹" + invoice.getTotalAmount() + "\n\n" +
                            "Please make the payment at your earliest convenience.\n\n" +
                            "Regards,\n" +
                            invoice.getBusiness().getName());
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reminder email: " + invoice.getInvoiceNumber(), e);
        }
    }
}

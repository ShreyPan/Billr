package com.billr.billr_backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.billr.billr_backend.invoice.model.InvoiceStatus;
import com.billr.billr_backend.invoice.repository.InvoiceRepository;
import com.billr.billr_backend.notification.service.EmailService;
import java.util.List;
import java.time.LocalDate;
import com.billr.billr_backend.invoice.model.Invoice;

@Component
public class InvoiceScheduler {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;

    public InvoiceScheduler(InvoiceRepository invoiceRepository, EmailService emailService) {
        this.invoiceRepository = invoiceRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void markOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findAllByStatusAndDueDateBefore(InvoiceStatus.SENT,
                LocalDate.now());
        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        }

        invoiceRepository.saveAll(overdueInvoices);

        for (Invoice invoice : overdueInvoices) {
            emailService.sendOverdueReminderEmail(invoice);
        }
    }
}

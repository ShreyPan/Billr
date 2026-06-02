package com.billr.billr_backend.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.billr.billr_backend.invoice.model.InvoiceStatus;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private UUID id;
    private UUID clientId;
    private String clientName;
    private String invoiceNumber;
    private InvoiceStatus status;
    private LocalDate dueDate;
    private BigDecimal subTotal;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private String notes;
    private String pdfUrl;
    private List<InvoiceItemResponse> items;
    private LocalDateTime createdAt;
}

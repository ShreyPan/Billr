package com.billr.billr_backend.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    @NotNull
    private UUID clientId;

    @NotBlank
    private String invoiceNumber;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private BigDecimal gstPercentage;

    private String notes;

    @NotNull
    private List<InvoiceItemRequest> items;
}

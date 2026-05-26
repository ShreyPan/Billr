package com.billr.billr_backend.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemRequest {

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal quantity;

    @NotNull
    private BigDecimal unitPrice;
}

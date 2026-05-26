package com.billr.billr_backend.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String phoneNumber;
    private String gstin;
}

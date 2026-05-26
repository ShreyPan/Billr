package com.billr.billr_backend.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String gstin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

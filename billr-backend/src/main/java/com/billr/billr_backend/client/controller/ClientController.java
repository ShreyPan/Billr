package com.billr.billr_backend.client.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import com.billr.billr_backend.client.dto.ClientRequest;
import com.billr.billr_backend.client.dto.ClientResponse;
import com.billr.billr_backend.client.service.ClientService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {

        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<ClientResponse>> getAllClients() {

        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable UUID id) {

        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable UUID id,
            @Valid @RequestBody ClientRequest request) {

        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {

        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}

package com.billr.billr_backend.client.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import com.billr.billr_backend.client.model.Client;
import com.billr.billr_backend.client.repository.ClientRepository;
import com.billr.billr_backend.auth.repository.BusinessRepository;
import com.billr.billr_backend.auth.model.User;
import com.billr.billr_backend.auth.model.Business;
import com.billr.billr_backend.client.dto.ClientRequest;
import com.billr.billr_backend.client.dto.ClientResponse;
import java.util.UUID;
import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final BusinessRepository businessRepository;

    public ClientService(ClientRepository clientRepository, BusinessRepository businessRepository) {
        this.clientRepository = clientRepository;
        this.businessRepository = businessRepository;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Business getCurrentBusiness() {
        User user = getCurrentUser();
        return businessRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found for the current user"));
    }

    public ClientResponse createClient(ClientRequest request) {

        Business business = getCurrentBusiness();
        if (clientRepository.existsByEmailAndBusinessId(request.getEmail(), business.getId())) {
            throw new IllegalArgumentException("Client with this email already exists for the business");
        }

        Client client = Client.builder()
                .business(business)
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .gstin(request.getGstin())
                .build();

        Client savedClient = clientRepository.save(client);
        return mapToResponse(savedClient);
    }

    public List<ClientResponse> getAllClients() {
        Business business = getCurrentBusiness();
        List<Client> clients = clientRepository.findAllByBusinessId(business.getId());
        return clients.stream().map(this::mapToResponse).toList();
    }

    public ClientResponse getClientById(UUID clientId) {
        Business business = getCurrentBusiness();
        Client client = clientRepository.findById(clientId)
                .filter(c -> c.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        return mapToResponse(client);
    }

    public ClientResponse updateClient(UUID clientId, ClientRequest request) {

        Business business = getCurrentBusiness();
        Client client = clientRepository.findById(clientId)
                .filter(c -> c.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (!client.getEmail().equals(request.getEmail())
                && clientRepository.existsByEmailAndBusinessId(request.getEmail(), business.getId())) {
            throw new IllegalArgumentException("Client with this email already exists for the business");
        }

        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setGstin(request.getGstin());
        Client updatedClient = clientRepository.save(client);
        return mapToResponse(updatedClient);
    }

    public void deleteClient(UUID clientId) {
        Business business = getCurrentBusiness();
        Client client = clientRepository.findById(clientId)
                .filter(c -> c.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
        clientRepository.delete(client);
    }

    private ClientResponse mapToResponse(Client client) {

        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .gstin(client.getGstin())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}

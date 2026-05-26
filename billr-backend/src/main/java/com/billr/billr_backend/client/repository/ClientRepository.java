package com.billr.billr_backend.client.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.billr.billr_backend.client.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findAllByBusinessId(UUID businessId);

    boolean existsByEmailAndBusinessId(String email, UUID businessId);
}

package com.billr.billr_backend.invoice.repository;

import org.springframework.stereotype.Repository;
import com.billr.billr_backend.invoice.model.Invoice;
import com.billr.billr_backend.invoice.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    List<Invoice> findAllByBusinessId(UUID businessId);

    List<Invoice> findAllByBusinessIdAndStatus(UUID businessId, InvoiceStatus status);

    Optional<Invoice> findByIdAndBusinessId(UUID id, UUID businessId);

    boolean existsByInvoiceNumber(String invoiceNumber);
}

package com.billr.billr_backend.invoice.repository;

import org.springframework.stereotype.Repository;
import com.billr.billr_backend.invoice.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, UUID> {

    List<InvoiceItem> findAllByInvoiceId(UUID invoiceId);
}

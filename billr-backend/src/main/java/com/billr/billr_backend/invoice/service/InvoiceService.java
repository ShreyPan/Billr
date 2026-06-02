package com.billr.billr_backend.invoice.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.billr.billr_backend.invoice.repository.InvoiceRepository;
import com.billr.billr_backend.invoice.repository.InvoiceItemRepository;
import com.billr.billr_backend.client.repository.ClientRepository;
import com.billr.billr_backend.auth.repository.BusinessRepository;
import com.billr.billr_backend.invoice.dto.InvoiceRequest;
import com.billr.billr_backend.invoice.dto.InvoiceResponse;
import com.billr.billr_backend.invoice.dto.InvoiceItemResponse;
import com.billr.billr_backend.invoice.model.Invoice;
import com.billr.billr_backend.invoice.model.InvoiceItem;
import com.billr.billr_backend.pdf.service.PdfGenerationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.billr.billr_backend.auth.model.User;
import com.billr.billr_backend.auth.model.Business;
import com.billr.billr_backend.client.model.Client;
import com.billr.billr_backend.invoice.model.InvoiceStatus;
import java.math.BigDecimal;

@Service
public class InvoiceService {

        private final InvoiceRepository invoiceRepository;
        private final InvoiceItemRepository invoiceItemRepository;
        private final ClientRepository clientRepository;
        private final BusinessRepository businessRepository;
        private final PdfGenerationService pdfGenerationService;

        public InvoiceService(InvoiceRepository invoiceRepository, InvoiceItemRepository invoiceItemRepository,
                        ClientRepository clientRepository, BusinessRepository businessRepository,
                        PdfGenerationService pdfGenerationService) {
                this.invoiceRepository = invoiceRepository;
                this.invoiceItemRepository = invoiceItemRepository;
                this.clientRepository = clientRepository;
                this.businessRepository = businessRepository;
                this.pdfGenerationService = pdfGenerationService;
        }

        private User getCurrentUser() {
                return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        private Business getCurrentBusiness() {
                User user = getCurrentUser();
                return businessRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Business not found for user"));
        }

        public InvoiceResponse createInvoice(InvoiceRequest request) {

                Business business = getCurrentBusiness();

                if (invoiceRepository.existsByInvoiceNumber(request.getInvoiceNumber())) {
                        throw new IllegalArgumentException("Invoice number already exists");
                }

                Client client = clientRepository.findById(request.getClientId())
                                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

                BigDecimal subTotal = request.getItems().stream()
                                .map(item -> item.getUnitPrice().multiply(item.getQuantity()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal gstAmount = subTotal
                                .multiply(request.getGstPercentage().divide(BigDecimal.valueOf(100)));

                BigDecimal totalAmount = subTotal.add(gstAmount);

                Invoice invoice = Invoice.builder()
                                .business(business)
                                .client(client)
                                .invoiceNumber(request.getInvoiceNumber())
                                .dueDate(request.getDueDate())
                                .notes(request.getNotes())
                                .status(InvoiceStatus.DRAFT)
                                .subTotal(subTotal)
                                .gstAmount(gstAmount)
                                .totalAmount(totalAmount)
                                .build();

                Invoice savedInvoice = invoiceRepository.save(invoice);

                List<InvoiceItem> items = request.getItems().stream()
                                .map(itemRequest -> InvoiceItem.builder()
                                                .invoice(savedInvoice)
                                                .description(itemRequest.getDescription())
                                                .quantity(itemRequest.getQuantity())
                                                .unitPrice(itemRequest.getUnitPrice())
                                                .totalPrice(itemRequest.getUnitPrice()
                                                                .multiply(itemRequest.getQuantity()))
                                                .build())
                                .collect(Collectors.toList());
                invoiceItemRepository.saveAll(items);
                savedInvoice.setItems(items);
                return mapToResponse(savedInvoice);
        }

        public List<InvoiceResponse> getAllInvoices() {

                Business business = getCurrentBusiness();
                List<Invoice> invoices = invoiceRepository.findAllByBusinessId(business.getId());
                return invoices.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public InvoiceResponse getInvoiceById(UUID id) {

                Business business = getCurrentBusiness();
                Invoice invoice = invoiceRepository.findByIdAndBusinessId(id, business.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
                return mapToResponse(invoice);
        }

        public InvoiceResponse updateInvoice(UUID id, InvoiceStatus newStatus) {

                Business business = getCurrentBusiness();
                Invoice invoice = invoiceRepository.findByIdAndBusinessId(id, business.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

                invoice.setStatus(newStatus);
                Invoice updatedInvoice = invoiceRepository.save(invoice);

                if (newStatus == InvoiceStatus.SENT) {
                        pdfGenerationService.generateInvoicePdf(updatedInvoice);
                }
                return mapToResponse(updatedInvoice);
        }

        public void deleteInvoice(UUID id) {

                Business business = getCurrentBusiness();
                Invoice invoice = invoiceRepository.findByIdAndBusinessId(id, business.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

                if (invoice.getStatus() != InvoiceStatus.DRAFT) {
                        throw new IllegalStateException("Only draft invoices can be deleted");
                }
                invoiceRepository.delete(invoice);
        }

        private InvoiceResponse mapToResponse(Invoice invoice) {
                List<InvoiceItemResponse> itemResponse = invoice.getItems().stream()
                                .map(item -> InvoiceItemResponse.builder()
                                                .id(item.getId())
                                                .description(item.getDescription())
                                                .quantity(item.getQuantity())
                                                .unitPrice(item.getUnitPrice())
                                                .totalPrice(item.getTotalPrice())
                                                .build())
                                .collect(Collectors.toList());
                return InvoiceResponse.builder()
                                .id(invoice.getId())
                                .clientId(invoice.getClient().getId())
                                .clientName(invoice.getClient().getName())
                                .invoiceNumber(invoice.getInvoiceNumber())
                                .status(invoice.getStatus())
                                .dueDate(invoice.getDueDate())
                                .subTotal(invoice.getSubTotal())
                                .gstAmount(invoice.getGstAmount())
                                .totalAmount(invoice.getTotalAmount())
                                .notes(invoice.getNotes())
                                .pdfUrl(invoice.getPdfUrl())
                                .items(itemResponse)
                                .createdAt(invoice.getCreatedAt())
                                .build();
        }
}

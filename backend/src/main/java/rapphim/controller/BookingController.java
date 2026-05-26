package rapphim.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import rapphim.model.Invoice;
import rapphim.model.Ticket;
import rapphim.model.dto.CheckoutRequest;
import rapphim.service.BookingService;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Invoice> checkout(@Valid @RequestBody CheckoutRequest request) {
        // Resolve employeeId from request or spring security context
        String employeeId = request.getEmployeeId();
        if (employeeId == null || employeeId.trim().isEmpty()) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof String) {
                employeeId = (String) principal;
            } else {
                employeeId = "EMP001"; // Fallback default
            }
        }
        
        Invoice invoice = bookingService.processCheckout(
                request.getShowtimeId(),
                request.getSeatIds(),
                request.getPaymentMethod(),
                employeeId,
                request.getNote()
        );
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(bookingService.getAllInvoices());
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String id) {
        return bookingService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/invoices/{id}/tickets")
    public ResponseEntity<List<Ticket>> getTicketsByInvoice(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getTicketsByInvoice(id));
    }

    @GetMapping("/invoices/{id}/pdf")
    public ResponseEntity<Resource> getInvoicePdf(@PathVariable String id) {
        File file = new File("invoice/invoice_" + id + ".pdf");
        if (!file.exists()) {
            boolean success = rapphim.util.InvoicePdfExporter.exportInvoice(id);
            if (!success) {
                return ResponseEntity.notFound().build();
            }
            file = new File("invoice/invoice_" + id + ".pdf");
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/invoices/{id}/tickets/pdf")
    public ResponseEntity<Resource> getTicketsPdf(@PathVariable String id) {
        File file = new File("invoice/tickets/tickets_" + id + ".pdf");
        if (!file.exists()) {
            boolean success = rapphim.util.TicketsExporter.exportTickets(id);
            if (!success) {
                return ResponseEntity.notFound().build();
            }
            file = new File("invoice/tickets/tickets_" + id + ".pdf");
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}

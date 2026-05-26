package rapphim.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rapphim.event.SeatBookedEvent;
import rapphim.model.*;
import rapphim.model.enums.InvoiceStatus;
import rapphim.model.enums.Payment;
import rapphim.model.enums.ShowSeatStatus;
import rapphim.repository.*;
import rapphim.service.payment.PaymentService;
import rapphim.util.InvoicePdfExporter;
import rapphim.util.TicketsExporter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final InvoiceRepository invoiceRepository;
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ShowSeatRepository showSeatRepository;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;

    public BookingService(InvoiceRepository invoiceRepository,
                          TicketRepository ticketRepository,
                          ShowtimeRepository showtimeRepository,
                          ShowSeatRepository showSeatRepository,
                          PaymentService paymentService,
                          ApplicationEventPublisher eventPublisher,
                          MovieRepository movieRepository,
                          SeatRepository seatRepository) {
        this.invoiceRepository = invoiceRepository;
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
        this.showSeatRepository = showSeatRepository;
        this.paymentService = paymentService;
        this.eventPublisher = eventPublisher;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Invoice> getInvoiceById(String invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    public List<Ticket> getTicketsByInvoice(String invoiceId) {
        List<Ticket> tickets = ticketRepository.findByInvoiceId(invoiceId);
        for (Ticket t : tickets) {
            String showSeatId = t.getShowSeatId();
            if (showSeatId != null) {
                showSeatRepository.findById(showSeatId).ifPresent(showSeat -> {
                    seatRepository.findById(showSeat.getSeatId()).ifPresent(seat -> {
                        t.setSeatLabel(seat.getSeatName());
                    });
                    
                    showtimeRepository.findById(showSeat.getShowtimeId()).ifPresent(showtime -> {
                        t.setHallId(showtime.getHallId());
                        t.setStartTime(showtime.getStartTime().toString());
                        
                        movieRepository.findById(showtime.getMovieId()).ifPresent(movie -> {
                            t.setMovieTitle(movie.getTitle());
                        });
                    });
                });
            }
        }
        return tickets;
    }

    @Transactional
    public Invoice processCheckout(String showtimeId, List<String> seatIds, String paymentMethod, String employeeId, String note) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ghế chọn không được trống.");
        }
        
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new IllegalArgumentException("Suất chiếu không tồn tại: " + showtimeId);
        }

        String nextInvoiceId = getNextInvoiceId();
        
        double totalAmount = 0.0;
        List<ShowSeat> showSeatsToBook = new ArrayList<>();
        
        for (String seatId : seatIds) {
            ShowSeat showSeat = showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Ghế " + seatId + " không được cấu hình cho suất chiếu này."));
            
            if (showSeat.getStatus() != ShowSeatStatus.AVAILABLE) {
                throw new IllegalStateException("Ghế " + seatId + " đã bị đặt hoặc đang bị giữ.");
            }
            
            totalAmount += showSeat.getPrice();
            showSeatsToBook.add(showSeat);
        }

        // Mock payment process using Payment Strategy Pattern
        paymentService.processPayment(paymentMethod, totalAmount, nextInvoiceId);

        // Create Invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(nextInvoiceId);
        invoice.setEmployeeId(employeeId);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setTotalAmount(totalAmount);
        invoice.setTotalTickets(seatIds.size());
        invoice.setPaymentMethod(Payment.fromString(paymentMethod));
        invoice.setStatus(InvoiceStatus.CONFIRMED);
        invoice.setNote(note);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Update seats and create tickets
        int ticketCounter = getNextTicketNumber();
        List<String> bookedSeatLabels = new ArrayList<>();
        
        for (ShowSeat showSeat : showSeatsToBook) {
            showSeat.setStatus(ShowSeatStatus.BOOKED);
            showSeatRepository.save(showSeat);
            
            String newTicketId = String.format("TKT%03d", ticketCounter++);
            String barcode = "BC" + System.currentTimeMillis() + showSeat.getSeatId();
            
            Ticket ticket = new Ticket(
                    newTicketId,
                    nextInvoiceId,
                    showSeat.getShowSeatId(),
                    showSeat.getPrice(),
                    showSeat.getPrice(),
                    barcode,
                    "VALID"
            );
            ticketRepository.save(ticket);
            bookedSeatLabels.add(showSeat.getSeatId());
        }

        // Publish Event (Observer Pattern)
        SeatBookedEvent event = new SeatBookedEvent(this, savedInvoice.getInvoiceId(), showtimeId, bookedSeatLabels, totalAmount);
        eventPublisher.publishEvent(event);

        // PDF Generation
        try {
            TicketsExporter.exportTickets(nextInvoiceId);
            InvoicePdfExporter.exportInvoice(nextInvoiceId);
        } catch (Exception e) {
            // Log PDF generation failure but do not fail transaction
        }

        return savedInvoice;
    }

    public String getNextInvoiceId() {
        String maxId = invoiceRepository.findMaxInvoiceId();
        if (maxId != null) {
            try {
                int next = Integer.parseInt(maxId.replaceAll("[^0-9]", "")) + 1;
                return String.format("INV%03d", next);
            } catch (NumberFormatException ignored) {}
        }
        return "INV001";
    }

    private int getNextTicketNumber() {
        String maxId = ticketRepository.findMaxTicketId();
        if (maxId != null) {
            try {
                return Integer.parseInt(maxId.replaceAll("[^0-9]", "")) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return 1;
    }
}

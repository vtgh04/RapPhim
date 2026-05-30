package rapphim.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import rapphim.model.*;
import rapphim.model.enums.*;
import rapphim.repository.*;
import rapphim.service.payment.PaymentService;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private ShowSeatRepository showSeatRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private BookingService bookingService;

    private final String showtimeId = "ST001";
    private final String employeeId = "EMP001";
    private final String seatId1 = "HALL01_A1";
    private final String seatId2 = "HALL01_A2";

    @Test
    public void testProcessCheckout_Success() {
        // Arrange
        when(showtimeRepository.existsById(showtimeId)).thenReturn(true);
        when(invoiceRepository.findMaxInvoiceId()).thenReturn("INV005");

        ShowSeat seat1 = new ShowSeat();
        seat1.setShowSeatId("ST001_A1");
        seat1.setSeatId(seatId1);
        seat1.setPrice(80000.0);
        seat1.setStatus(ShowSeatStatus.AVAILABLE);

        ShowSeat seat2 = new ShowSeat();
        seat2.setShowSeatId("ST001_A2");
        seat2.setSeatId(seatId2);
        seat2.setPrice(80000.0);
        seat2.setStatus(ShowSeatStatus.AVAILABLE);

        when(showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId1)).thenReturn(Optional.of(seat1));
        when(showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId2)).thenReturn(Optional.of(seat2));
        
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ticketRepository.findMaxTicketId()).thenReturn("TKT010");

        // Act
        Invoice invoice = bookingService.processCheckout(showtimeId, Arrays.asList(seatId1, seatId2), "CASH", employeeId, "Test note");

        // Assert
        assertNotNull(invoice);
        assertEquals("INV006", invoice.getInvoiceId());
        assertEquals(160000.0, invoice.getTotalAmount());
        assertEquals(2, invoice.getTotalTickets());
        assertEquals(ShowSeatStatus.BOOKED, seat1.getStatus());
        assertEquals(ShowSeatStatus.BOOKED, seat2.getStatus());

        verify(paymentService, times(1)).processPayment("CASH", 160000.0, "INV006");
        verify(showSeatRepository, times(2)).save(any(ShowSeat.class));
        verify(ticketRepository, times(2)).save(any(Ticket.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    public void testProcessCheckout_ShowtimeNotFound() {
        // Arrange
        when(showtimeRepository.existsById(showtimeId)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.processCheckout(showtimeId, Arrays.asList(seatId1), "CASH", employeeId, "Note");
        });

        assertTrue(exception.getMessage().contains("Suất chiếu không tồn tại"));
        verify(showSeatRepository, never()).save(any());
    }

    @Test
    public void testProcessCheckout_SeatAlreadyBooked() {
        // Arrange
        when(showtimeRepository.existsById(showtimeId)).thenReturn(true);

        ShowSeat seat1 = new ShowSeat();
        seat1.setShowSeatId("ST001_A1");
        seat1.setSeatId(seatId1);
        seat1.setPrice(80000.0);
        seat1.setStatus(ShowSeatStatus.BOOKED); // Already booked

        when(showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId1)).thenReturn(Optional.of(seat1));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.processCheckout(showtimeId, Arrays.asList(seatId1), "CASH", employeeId, "Note");
        });

        assertTrue(exception.getMessage().contains("đã bị đặt hoặc đang bị giữ"));
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    public void testHoldSeat_Success() {
        // Arrange
        ShowSeat seat = new ShowSeat();
        seat.setShowSeatId("ST001_A1");
        seat.setSeatId(seatId1);
        seat.setStatus(ShowSeatStatus.AVAILABLE);

        when(showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId1)).thenReturn(Optional.of(seat));

        // Act
        bookingService.holdSeat(showtimeId, seatId1);

        // Assert
        assertEquals(ShowSeatStatus.HELD, seat.getStatus());
        assertNotNull(seat.getHeldUntil());
        assertTrue(seat.getHeldUntil().isAfter(java.time.LocalDateTime.now()));
        verify(showSeatRepository, times(1)).save(seat);
    }

    @Test
    public void testHoldSeat_AlreadyBooked() {
        // Arrange
        ShowSeat seat = new ShowSeat();
        seat.setShowSeatId("ST001_A1");
        seat.setSeatId(seatId1);
        seat.setStatus(ShowSeatStatus.BOOKED);

        when(showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId1)).thenReturn(Optional.of(seat));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            bookingService.holdSeat(showtimeId, seatId1);
        });
        verify(showSeatRepository, never()).save(any());
    }

    @Test
    public void testHoldSeat_AlreadyHeld() {
        // Arrange
        ShowSeat seat = new ShowSeat();
        seat.setShowSeatId("ST001_A1");
        seat.setSeatId(seatId1);
        seat.setStatus(ShowSeatStatus.HELD);
        seat.setHeldUntil(java.time.LocalDateTime.now().plusMinutes(2));

        when(showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId1)).thenReturn(Optional.of(seat));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            bookingService.holdSeat(showtimeId, seatId1);
        });
        verify(showSeatRepository, never()).save(any());
    }

    @Test
    public void testReleaseSeat_Success() {
        // Arrange
        ShowSeat seat = new ShowSeat();
        seat.setShowSeatId("ST001_A1");
        seat.setSeatId(seatId1);
        seat.setStatus(ShowSeatStatus.HELD);
        seat.setHeldUntil(java.time.LocalDateTime.now().plusMinutes(2));

        when(showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId1)).thenReturn(Optional.of(seat));

        // Act
        bookingService.releaseSeat(showtimeId, seatId1);

        // Assert
        assertEquals(ShowSeatStatus.AVAILABLE, seat.getStatus());
        assertNull(seat.getHeldUntil());
        verify(showSeatRepository, times(1)).save(seat);
    }
}

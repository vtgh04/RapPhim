package rapphim.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket controller for realtime seat status broadcasting.
 * Clients send seat lock/unlock events to /app/showtime/{id}/seat-update
 * Server broadcasts to /topic/showtime/{id}/seats for all subscribers.
 */
@Controller
public class SeatStatusController {

    private final SimpMessagingTemplate messagingTemplate;
    private final rapphim.service.BookingService bookingService;
    private final rapphim.repository.ShowSeatRepository showSeatRepository;

    public SeatStatusController(SimpMessagingTemplate messagingTemplate,
                                rapphim.service.BookingService bookingService,
                                rapphim.repository.ShowSeatRepository showSeatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.bookingService = bookingService;
        this.showSeatRepository = showSeatRepository;
    }

    /**
     * Receives a seat status update, attempts to persist it in the database (with transactional isolation),
     * and broadcasts it to all subscribers of that showtime.
     * Payload: { seatId: "A1", status: "LOCKED" | "AVAILABLE" }
     */
    @MessageMapping("/showtime/{showtimeId}/seat-update")
    public void broadcastSeatUpdate(
            @DestinationVariable String showtimeId,
            Map<String, String> payload) {
        
        String seatId = payload.get("seatId");
        String status = payload.get("status");

        if (seatId == null || status == null) {
            return;
        }

        try {
            if ("LOCKED".equalsIgnoreCase(status)) {
                bookingService.holdSeat(showtimeId, seatId);
                messagingTemplate.convertAndSend(
                        "/topic/showtime/" + showtimeId + "/seats",
                        payload
                );
            } else if ("AVAILABLE".equalsIgnoreCase(status)) {
                bookingService.releaseSeat(showtimeId, seatId);
                messagingTemplate.convertAndSend(
                        "/topic/showtime/" + showtimeId + "/seats",
                        payload
                );
            }
        } catch (Exception e) {
            // Transaction/locking failed (e.g. seat already taken or held by someone else).
            // Sync caller's state by broadcasting the actual database state to everyone.
            String currentStatusStr = "AVAILABLE";
            try {
                rapphim.model.ShowSeat showSeat = showSeatRepository.findByShowtimeIdAndSeatId(showtimeId, seatId).orElse(null);
                if (showSeat != null) {
                    if (showSeat.getStatus() == rapphim.model.enums.ShowSeatStatus.BOOKED) {
                        currentStatusStr = "BOOKED";
                    } else if (showSeat.getStatus() == rapphim.model.enums.ShowSeatStatus.HELD) {
                        if (showSeat.getHeldUntil() != null && showSeat.getHeldUntil().isBefore(java.time.LocalDateTime.now())) {
                            currentStatusStr = "AVAILABLE";
                        } else {
                            currentStatusStr = "LOCKED";
                        }
                    }
                }
            } catch (Exception ex) {
                // Ignore nested read error
            }
            
            payload.put("status", currentStatusStr);
            messagingTemplate.convertAndSend(
                    "/topic/showtime/" + showtimeId + "/seats",
                    payload
            );
        }
    }
}

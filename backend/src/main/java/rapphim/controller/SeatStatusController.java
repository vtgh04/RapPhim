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

    public SeatStatusController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Receives a seat status update and broadcasts it to all subscribers of that showtime.
     * Payload: { seatId: "A1", status: "LOCKED" | "AVAILABLE" }
     */
    @MessageMapping("/showtime/{showtimeId}/seat-update")
    public void broadcastSeatUpdate(
            @DestinationVariable String showtimeId,
            Map<String, String> payload) {

        messagingTemplate.convertAndSend(
                "/topic/showtime/" + showtimeId + "/seats",
                payload
        );
    }
}

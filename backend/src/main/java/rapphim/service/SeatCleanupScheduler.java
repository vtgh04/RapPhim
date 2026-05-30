package rapphim.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rapphim.model.ShowSeat;
import rapphim.model.enums.ShowSeatStatus;
import rapphim.repository.ShowSeatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class SeatCleanupScheduler {

    private final ShowSeatRepository showSeatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public SeatCleanupScheduler(ShowSeatRepository showSeatRepository, SimpMessagingTemplate messagingTemplate) {
        this.showSeatRepository = showSeatRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Periodically runs every 10 seconds to release seats that have been in HELD status for more than 5 minutes.
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void cleanupExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<ShowSeat> expiredSeats = showSeatRepository.findByStatusAndHeldUntilBefore(ShowSeatStatus.HELD, now);
        
        for (ShowSeat ss : expiredSeats) {
            ss.setStatus(ShowSeatStatus.AVAILABLE);
            ss.setHeldUntil(null);
            showSeatRepository.save(ss);

            // Broadcast the state update via WebSocket so other clients update in real-time
            Map<String, String> payload = new HashMap<>();
            payload.put("seatId", ss.getSeatId());
            payload.put("status", "AVAILABLE");
            
            messagingTemplate.convertAndSend(
                    "/topic/showtime/" + ss.getShowtimeId() + "/seats",
                    payload
            );
        }
    }
}

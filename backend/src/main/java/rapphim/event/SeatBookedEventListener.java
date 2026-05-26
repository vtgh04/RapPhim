package rapphim.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SeatBookedEventListener {
    private static final Logger log = LoggerFactory.getLogger(SeatBookedEventListener.class);

    @EventListener
    public void handleSeatBooked(SeatBookedEvent event) {
        log.info("[NOTIFICATION EVENT] Hóa đơn {} được thanh toán thành công!", event.getInvoiceId());
        log.info("[NOTIFICATION EVENT] Suất chiếu: {}, Ghế đặt: {}, Tổng thanh toán: {} VND", 
                event.getShowtimeId(), event.getSeatIds(), event.getTotalAmount());
    }
}

package rapphim.event;

import org.springframework.context.ApplicationEvent;
import java.util.List;

public class SeatBookedEvent extends ApplicationEvent {
    private final String invoiceId;
    private final String showtimeId;
    private final List<String> seatIds;
    private final double totalAmount;

    public SeatBookedEvent(Object source, String invoiceId, String showtimeId, List<String> seatIds, double totalAmount) {
        super(source);
        this.invoiceId = invoiceId;
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
        this.totalAmount = totalAmount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public List<String> getSeatIds() {
        return seatIds;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}

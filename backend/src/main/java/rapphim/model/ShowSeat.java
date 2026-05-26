package rapphim.model;

import rapphim.model.enums.ShowSeatStatus;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "show_seats")
public class ShowSeat {

    @Id
    @Column(name = "show_seat_id")
    private String showSeatId;

    @Column(name = "showtime_id")
    private String showtimeId;

    @Column(name = "seat_id")
    private String seatId;

    @Column(name = "price")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShowSeatStatus status;

    @Column(name = "held_until")
    private LocalDateTime heldUntil;

    public ShowSeat() {
        this.status = ShowSeatStatus.AVAILABLE;
    }

    public ShowSeat(String showSeatId, String showtimeId, String seatId, double price, ShowSeatStatus status, LocalDateTime heldUntil) {
        this.showSeatId = showSeatId;
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        setPrice(price);
        this.status = status != null ? status : ShowSeatStatus.AVAILABLE;
        this.heldUntil = heldUntil;
    }

    public String getShowSeatId() { return showSeatId; }
    public void setShowSeatId(String showSeatId) { this.showSeatId = showSeatId; }

    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Giá vé không được nhỏ hơn 0");
        }
        this.price = price;
    }

    public ShowSeatStatus getStatus() { return status; }
    public void setStatus(ShowSeatStatus status) { this.status = status; }

    public LocalDateTime getHeldUntil() { return heldUntil; }
    public void setHeldUntil(LocalDateTime heldUntil) { this.heldUntil = heldUntil; }

    @Override
    public String toString() {
        return "ShowSeat{" +
                "showSeatId='" + showSeatId + '\'' +
                ", showtimeId='" + showtimeId + '\'' +
                ", seatId='" + seatId + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", heldUntil=" + heldUntil +
                '}';
    }
}

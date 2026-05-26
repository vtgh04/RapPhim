package rapphim.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tickets")
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ticket_id")
    private String ticketId;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "show_seat_id")
    private String showSeatId;

    @Column(name = "original_price")
    private double originalPrice;

    @Column(name = "final_price")
    private double finalPrice;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "status")
    private String status;

    public Ticket() {
        this.status = "VALID";
    }

    public Ticket(String ticketId, String invoiceId, String showSeatId, double originalPrice, double finalPrice, String barcode, String status) {
        this.ticketId = ticketId;
        this.invoiceId = invoiceId;
        this.showSeatId = showSeatId;
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.barcode = barcode;
        this.status = status != null ? status : "VALID";
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getShowSeatId() {
        return showSeatId;
    }

    public void setShowSeatId(String showSeatId) {
        this.showSeatId = showSeatId;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", showSeatId='" + showSeatId + '\'' +
                ", finalPrice=" + finalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}

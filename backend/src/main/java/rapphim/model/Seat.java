package rapphim.model;

import rapphim.model.enums.SeatType;
import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "seat_id")
    private String seatId;

    @Column(name = "hall_id")
    private String hallId;

    @Column(name = "row_char")
    private char rowChar;

    @Column(name = "col_number")
    private int colNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType;

    @Column(name = "seat_factor")
    private double seatFactor;

    @Column(name = "is_broken")
    private boolean isBroken;

    public Seat() {
    }

    public Seat(String seatId, String hallId, char rowChar, int colNumber, SeatType seatType, double seatFactor) {
        this.seatId = seatId;
        this.hallId = hallId;
        this.rowChar = rowChar;
        this.colNumber = colNumber;
        this.seatType = seatType;
        this.seatFactor = seatFactor;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
    }

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }

    public char getRowChar() {
        return rowChar;
    }

    public void setRowChar(char rowChar) {
        this.rowChar = rowChar;
    }

    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public double getSeatFactor() {
        return seatFactor;
    }

    public void setSeatFactor(double seatFactor) {
        this.seatFactor = seatFactor;
    }

    public String getSeatName() {
        return String.valueOf(rowChar) + colNumber;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId='" + seatId + '\'' +
                ", hallId='" + hallId + '\'' +
                ", rowChar=" + rowChar +
                ", colNumber=" + colNumber +
                ", seatType=" + seatType +
                ", seatFactor=" + seatFactor +
                ", isBroken=" + isBroken +
                '}';
    }
}

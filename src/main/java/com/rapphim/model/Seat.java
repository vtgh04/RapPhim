package com.rapphim.model;

import com.rapphim.model.enums.SeatType;
import java.io.Serializable;

public class Seat implements Serializable {
    private String seatId;
    private String hallId;
    private char rowChar;
    private int colNumber;
    private SeatType seatType;
    private double seatFactor;
    private boolean isBroken;

    public Seat() {}

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

    /**
     * Phương thức tiện ích để lấy tên hiển thị của ghế (VD: A1, B5)
     */
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

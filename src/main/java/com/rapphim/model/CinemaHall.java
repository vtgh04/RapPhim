package com.rapphim.model;

import com.rapphim.model.enums.CinemaHallStatus;
import java.io.Serializable;

public class CinemaHall implements Serializable {
    private String hallId;
    private String name;
    private String hallType;
    private int totalRows;
    private int totalCols;
    private CinemaHallStatus status;

    public CinemaHall() {
    }

    public CinemaHall(String hallId, String name, String hallType, int totalRows, int totalCols,
            CinemaHallStatus status) {
        this.hallId = hallId;
        this.name = name;
        this.hallType = hallType;
        this.totalRows = totalRows;
        this.totalCols = totalCols;
        this.status = status;
    }

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHallType() {
        return hallType;
    }

    public void setHallType(String hallType) {
        this.hallType = hallType;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalCols() {
        return totalCols;
    }

    public void setTotalCols(int totalCols) {
        this.totalCols = totalCols;
    }

    public CinemaHallStatus getStatus() {
        return status;
    }

    public void setStatus(CinemaHallStatus status) {
        this.status = status;
    }

    public int getTotalSeats() {
        return totalRows * totalCols;
    }

    @Override
    public String toString() {
        return "CinemaHall{" +
                "hallId='" + hallId + '\'' +
                ", name='" + name + '\'' +
                ", hallType='" + hallType + '\'' +
                ", totalRows=" + totalRows +
                ", totalCols=" + totalCols +
                ", totalSeats=" + getTotalSeats() +
                ", status=" + status +
                '}';
    }
}

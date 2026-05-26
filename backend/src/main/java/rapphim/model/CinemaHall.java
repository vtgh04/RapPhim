package rapphim.model;

import rapphim.model.enums.CinemaHallStatus;
import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "cinema_halls")
public class CinemaHall implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "hall_id")
    private String hallId;

    @Column(name = "name")
    private String name;

    @Column(name = "hall_type")
    private String hallType;

    @Column(name = "total_rows")
    private int totalRows;

    @Column(name = "total_cols")
    private int totalCols;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
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

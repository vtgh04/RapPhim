package com.rapphim.model;

import com.rapphim.model.enums.ShowtimeStatus;
import java.time.LocalDateTime;

public class Showtime {
    private String showtimeId;
    private String movieId;
    private String hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double basePrice;
    private ShowtimeStatus status;

    public Showtime() {
        this.status = ShowtimeStatus.SCHEDULED;
    }

    public Showtime(String showtimeId, String movieId, String hallId, LocalDateTime startTime, LocalDateTime endTime,
            double basePrice, ShowtimeStatus status) {
        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
        this.status = status != null ? status : ShowtimeStatus.SCHEDULED;
    }

    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getHallId() { return hallId; }
    public void setHallId(String hallId) { this.hallId = hallId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) {
        if (basePrice < 0) throw new IllegalArgumentException("Giá vé cơ bản không được nhỏ hơn 0");
        this.basePrice = basePrice;
    }

    public ShowtimeStatus getStatus() { return status; }
    public void setStatus(ShowtimeStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Showtime{" +
                "showtimeId='" + showtimeId + '\'' +
                ", movieId='" + movieId + '\'' +
                ", hallId='" + hallId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", basePrice=" + basePrice +
                ", status=" + status +
                '}';
    }
}

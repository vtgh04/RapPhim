package com.rapphim.service;

import com.rapphim.dao.ShowtimeDAO;
import com.rapphim.model.Movie;
import com.rapphim.model.Showtime;
import com.rapphim.model.enums.ShowSeatStatus;
import com.rapphim.model.enums.ShowtimeStatus;
import com.rapphim.util.ShowtimeExcelUtils;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ShowtimeService {

    private ShowtimeDAO showtimeDAO;

    public ShowtimeService() {
        this.showtimeDAO = new ShowtimeDAO();
    }

    public List<Showtime> getAllShowtimes() throws SQLException {
        return showtimeDAO.findAll();
    }

    public List<Showtime> getTodayShowtimes() throws SQLException {
        return showtimeDAO.findTodayShowtimes();
    }

    public List<Showtime> getShowtimesByDateRange(LocalDateTime from, LocalDateTime to) throws SQLException {
        return showtimeDAO.findByDateRange(from, to);
    }

    public Showtime getShowtimeById(String showtimeId) throws SQLException {
        return showtimeDAO.findById(showtimeId);
    }

    public int countAll() throws SQLException {
        return showtimeDAO.countAll();
    }

    public int countByStatus(ShowtimeStatus status) throws SQLException {
        return showtimeDAO.countByStatus(status);
    }

    public int countActiveHalls() throws SQLException {
        return showtimeDAO.countActiveHalls();
    }

    public void addShowtime(Showtime st) throws SQLException {
        // Kiểm tra hợp lệ thời gian
        if (st.getStartTime().isAfter(st.getEndTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu không thể sau thời gian kết thúc.");
        }
        // Kiểm tra trùng lịch
        if (hasOverlap(st.getHallId(), st.getStartTime(), st.getEndTime())) {
            throw new IllegalArgumentException("Phòng chiếu đã có suất chiếu trong khung giờ này.");
        }
        showtimeDAO.insert(st);
    }

    public void updateBasePrice(String showtimeId, double newPrice) throws SQLException {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Giá không được âm.");
        }
        showtimeDAO.updateBasePrice(showtimeId, newPrice);
    }

    public void updateStatus(String showtimeId, ShowtimeStatus status) throws SQLException {
        showtimeDAO.updateStatus(showtimeId, status);
    }

    public void updateInfo(Showtime st) throws SQLException {
        if (st.getStartTime().isAfter(st.getEndTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu không thể sau thời gian kết thúc.");
        }
        showtimeDAO.updateInfo(st);
    }

    public void autoUpdateStatuses(LocalDateTime now) throws SQLException {
        showtimeDAO.autoUpdateStatuses(now);
    }

    public String getNextShowtimeId() throws SQLException {
        return showtimeDAO.getNextShowtimeId();
    }

    public boolean hasOverlap(String hallId, LocalDateTime newStart, LocalDateTime newEnd) throws SQLException {
        return showtimeDAO.hasOverlap(hallId, newStart, newEnd);
    }

    public void generateShowSeats(String showtimeId, String hallId, double basePrice) throws SQLException {
        showtimeDAO.generateShowSeats(showtimeId, hallId, basePrice);
    }

    public Map<String, ShowSeatStatus> getShowSeatStatuses(String showtimeId) throws SQLException {
        return showtimeDAO.getShowSeatStatuses(showtimeId);
    }

    public void exportToExcel(List<Showtime> showtimes, Map<String, Movie> movieCache, File file) throws Exception {
        ShowtimeExcelUtils.exportToExcel(showtimes, movieCache, file);
    }
}

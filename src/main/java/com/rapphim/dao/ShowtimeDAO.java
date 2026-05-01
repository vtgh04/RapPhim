package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Showtime;
import com.rapphim.model.enums.ShowtimeStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {

    private static final String SQL_FIND_ALL =
            "SELECT showtime_id, movie_id, hall_id, start_time, end_time, base_price, status FROM showtimes ORDER BY start_time";

    private static final String SQL_FIND_BY_DATE_RANGE =
            "SELECT showtime_id, movie_id, hall_id, start_time, end_time, base_price, status FROM showtimes WHERE start_time >= ? AND start_time < ? ORDER BY movie_id, start_time";

    private static final String SQL_FIND_BY_ID =
            "SELECT showtime_id, movie_id, hall_id, start_time, end_time, base_price, status FROM showtimes WHERE showtime_id = ?";

    private static final String SQL_UPDATE_BASE_PRICE = "UPDATE showtimes SET base_price = ? WHERE showtime_id = ?";
    private static final String SQL_UPDATE_STATUS = "UPDATE showtimes SET status = ? WHERE showtime_id = ?";
    private static final String SQL_UPDATE_INFO =
            "UPDATE showtimes SET movie_id = ?, hall_id = ?, start_time = ?, end_time = ?, base_price = ?, status = ? WHERE showtime_id = ?";

    private static final String SQL_COUNT_ALL = "SELECT COUNT(*) FROM showtimes";
    private static final String SQL_COUNT_BY_STATUS = "SELECT COUNT(*) FROM showtimes WHERE status = ?";
    private static final String SQL_COUNT_ACTIVE_HALLS =
            "SELECT COUNT(DISTINCT hall_id) FROM showtimes WHERE status IN ('SCHEDULED','ONGOING')";

    public List<Showtime> findAll() throws SQLException {
        List<Showtime> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public List<Showtime> findTodayShowtimes() throws SQLException {
        LocalDateTime from = LocalDateTime.now().toLocalDate().atTime(0, 0);
        LocalDateTime to = from.plusHours(24);
        return findByDateRange(from, to);
    }

    public List<Showtime> findByDateRange(LocalDateTime from, LocalDateTime to) throws SQLException {
        List<Showtime> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_DATE_RANGE)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        }
        return list;
    }

    public Showtime findById(String showtimeId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setString(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
            }
        }
        return null;
    }

    public void updateBasePrice(String showtimeId, double newPrice) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_BASE_PRICE)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, showtimeId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(String showtimeId, ShowtimeStatus status) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.getValue());
            ps.setString(2, showtimeId);
            ps.executeUpdate();
        }
    }

    public void updateInfo(Showtime st) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_INFO)) {
            ps.setString(1, st.getMovieId());
            ps.setString(2, st.getHallId());
            ps.setTimestamp(3, Timestamp.valueOf(st.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(st.getEndTime()));
            ps.setDouble(5, st.getBasePrice());
            ps.setString(6, st.getStatus().getValue());
            ps.setString(7, st.getShowtimeId());
            ps.executeUpdate();
        }
    }

    public int countAll() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_ALL);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int countByStatus(ShowtimeStatus status) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_STATUS)) {
            ps.setString(1, status.getValue());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int countActiveHalls() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_ACTIVE_HALLS);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Showtime map(ResultSet rs) throws SQLException {
        return new Showtime(
                rs.getString("showtime_id"),
                rs.getString("movie_id"),
                rs.getString("hall_id"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                rs.getTimestamp("end_time").toLocalDateTime(),
                rs.getDouble("base_price"),
                ShowtimeStatus.fromString(rs.getString("status")));
    }
}

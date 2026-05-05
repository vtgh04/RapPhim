package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Showtime;
import com.rapphim.model.enums.ShowSeatStatus;
import com.rapphim.model.enums.ShowtimeStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowtimeDAO {

    // ── Columns fragment shared across SELECT queries ────────────────────────
    private static final String COLS =
            "showtime_id, movie_id, hall_id, start_time, end_time, base_price, status";

    // ── Queries ──────────────────────────────────────────────────────────────
    private static final String SQL_FIND_ALL =
            "SELECT " + COLS + " FROM showtimes ORDER BY start_time";

    private static final String SQL_FIND_BY_DATE_RANGE =
            "SELECT " + COLS + " FROM showtimes" +
            " WHERE start_time >= ? AND start_time < ?" +
            " ORDER BY movie_id, start_time";

    private static final String SQL_FIND_BY_ID =
            "SELECT " + COLS + " FROM showtimes WHERE showtime_id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO showtimes (showtime_id, movie_id, hall_id, start_time, end_time, base_price, status)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_BASE_PRICE =
            "UPDATE showtimes SET base_price = ? WHERE showtime_id = ?";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE showtimes SET status = ? WHERE showtime_id = ?";

    private static final String SQL_UPDATE_INFO =
            "UPDATE showtimes SET movie_id = ?, hall_id = ?, start_time = ?, end_time = ?, base_price = ?, status = ?" +
            " WHERE showtime_id = ?";

    private static final String SQL_COUNT_ALL =
            "SELECT COUNT(*) FROM showtimes";

    private static final String SQL_COUNT_BY_STATUS =
            "SELECT COUNT(*) FROM showtimes WHERE status = ?";

    private static final String SQL_COUNT_ACTIVE_HALLS =
            "SELECT COUNT(DISTINCT hall_id) FROM showtimes WHERE status IN ('SCHEDULED','ONGOING')";

    private static final String SQL_MAX_ID =
            "SELECT MAX(showtime_id) AS max_id FROM showtimes";

    private static final String SQL_HAS_OVERLAP =
            "SELECT COUNT(*) FROM showtimes" +
            " WHERE hall_id = ? AND status != 'CANCELLED' AND start_time < ? AND end_time > ?";

    private static final String SQL_FIND_SEATS =
            "SELECT seat_id, seat_factor FROM seats WHERE hall_id = ? AND is_broken = 0";

    private static final String SQL_INSERT_SHOW_SEAT =
            "INSERT INTO show_seats (show_seat_id, showtime_id, seat_id, price, status) VALUES (?, ?, ?, ?, 'AVAILABLE')";

    private static final String SQL_GET_SEAT_STATUSES =
            "SELECT seat_id, status FROM show_seats WHERE showtime_id = ?";

    private static final String SQL_AUTO_COMPLETED =
            "UPDATE showtimes SET status = 'COMPLETED'" +
            " WHERE status NOT IN ('COMPLETED', 'CANCELLED') AND end_time < ?";

    private static final String SQL_AUTO_ONGOING =
            "UPDATE showtimes SET status = 'ONGOING'" +
            " WHERE status NOT IN ('ONGOING', 'CANCELLED') AND start_time <= ? AND end_time >= ?";

    private static final String SQL_AUTO_SCHEDULED =
            "UPDATE showtimes SET status = 'SCHEDULED'" +
            " WHERE status NOT IN ('SCHEDULED', 'CANCELLED') AND start_time > ?";

    // ═════════════════════════════════════════════════════════════════════════
    // Read
    // ═════════════════════════════════════════════════════════════════════════

    public List<Showtime> findAll() throws SQLException {
        List<Showtime> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Showtime> findTodayShowtimes() throws SQLException {
        LocalDateTime from = LocalDateTime.now().toLocalDate().atStartOfDay();
        return findByDateRange(from, from.plusHours(24));
    }

    public List<Showtime> findByDateRange(LocalDateTime from, LocalDateTime to) throws SQLException {
        List<Showtime> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_DATE_RANGE)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Showtime findById(String showtimeId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setString(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Count
    // ═════════════════════════════════════════════════════════════════════════

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

    // ═════════════════════════════════════════════════════════════════════════
    // Write
    // ═════════════════════════════════════════════════════════════════════════

    public void insert(Showtime st) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, st.getShowtimeId());
            ps.setString(2, st.getMovieId());
            ps.setString(3, st.getHallId());
            ps.setTimestamp(4, Timestamp.valueOf(st.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(st.getEndTime()));
            ps.setDouble(6, st.getBasePrice());
            ps.setString(7, st.getStatus().getValue());
            ps.executeUpdate();
        }
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

    /**
     * Tự động đồng bộ trạng thái tất cả suất chiếu theo thời điểm hiện tại.
     * Thực thi 3 UPDATE trong một round-trip duy nhất bằng cách tái dùng cùng Timestamp.
     */
    public void autoUpdateStatuses(LocalDateTime now) throws SQLException {
        Timestamp ts = Timestamp.valueOf(now); // Tính 1 lần, dùng cho cả 3 câu UPDATE
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement psCompleted = conn.prepareStatement(SQL_AUTO_COMPLETED);
             PreparedStatement psOngoing   = conn.prepareStatement(SQL_AUTO_ONGOING);
             PreparedStatement psScheduled = conn.prepareStatement(SQL_AUTO_SCHEDULED)) {

            psCompleted.setTimestamp(1, ts);
            psCompleted.executeUpdate();

            psOngoing.setTimestamp(1, ts);
            psOngoing.setTimestamp(2, ts);
            psOngoing.executeUpdate();

            psScheduled.setTimestamp(1, ts);
            psScheduled.executeUpdate();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Utility
    // ═════════════════════════════════════════════════════════════════════════

    public String getNextShowtimeId() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SQL_MAX_ID)) {
            if (rs.next()) {
                String maxId = rs.getString("max_id");
                if (maxId != null) {
                    int next = Integer.parseInt(maxId.replaceAll("[^0-9]", "")) + 1;
                    return String.format("SHW%03d", next);
                }
            }
        }
        return "SHW001";
    }

    /**
     * Kiểm tra xem suất chiếu mới có bị trùng giờ với suất chiếu khác trong cùng phòng không.
     * Điều kiện trùng: newStart < existingEnd AND newEnd > existingStart
     */
    public boolean hasOverlap(String hallId, LocalDateTime newStart, LocalDateTime newEnd) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_HAS_OVERLAP)) {
            ps.setString(1, hallId);
            ps.setTimestamp(2, Timestamp.valueOf(newEnd));
            ps.setTimestamp(3, Timestamp.valueOf(newStart));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Tự động sinh show_seats cho toàn bộ ghế trong phòng chiếu.
     * Dùng executeBatch() để ghi nhiều dòng trong 1 round-trip.
     */
    public void generateShowSeats(String showtimeId, String hallId, double basePrice) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement psFind   = conn.prepareStatement(SQL_FIND_SEATS);
             PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT_SHOW_SEAT)) {
            psFind.setString(1, hallId);
            try (ResultSet rs = psFind.executeQuery()) {
                while (rs.next()) {
                    String seatId = rs.getString("seat_id");
                    double factor = rs.getDouble("seat_factor");
                    psInsert.setString(1, showtimeId + "_" + seatId);
                    psInsert.setString(2, showtimeId);
                    psInsert.setString(3, seatId);
                    psInsert.setDouble(4, basePrice * factor);
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }
        }
    }

    public Map<String, ShowSeatStatus> getShowSeatStatuses(String showtimeId) throws SQLException {
        Map<String, ShowSeatStatus> result = new HashMap<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_SEAT_STATUSES)) {
            ps.setString(1, showtimeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("seat_id"),
                               ShowSeatStatus.fromString(rs.getString("status")));
                }
            }
        }
        return result;
    }

    // ── Mapper ───────────────────────────────────────────────────────────────
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

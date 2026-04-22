package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.CinemaHall;
import com.rapphim.model.Seat;
import com.rapphim.model.enums.CinemaHallStatus;
import com.rapphim.model.enums.SeatType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HallDao {

    private static final String SQL_FIND_ALL_HALLS = "SELECT hall_id, name, hall_type, total_rows, total_cols, status FROM cinema_halls ORDER BY hall_id";

    private static final String SQL_FIND_HALL_BY_ID = "SELECT hall_id, name, hall_type, total_rows, total_cols, status FROM cinema_halls WHERE hall_id = ?";

    private static final String SQL_FIND_SEATS_BY_HALL = "SELECT seat_id, hall_id, row_char, col_number, seat_type, seat_price, is_broken FROM seats WHERE hall_id = ? ORDER BY row_char, col_number";

    private static final String SQL_UPDATE_SEAT_PRICE_BY_TYPE = "UPDATE seats SET seat_price = ? WHERE hall_id = ? AND seat_type = ?";

    private static final String SQL_UPDATE_HALL_INFO = "UPDATE cinema_halls SET name = ?, hall_type = ? WHERE hall_id = ?";

    public List<CinemaHall> findAllHalls() throws SQLException {
        List<CinemaHall> halls = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_HALLS);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                halls.add(mapHall(rs));
            }
        }
        return halls;
    }

    public CinemaHall findHallById(String hallId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_HALL_BY_ID)) {
            ps.setString(1, hallId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapHall(rs);
                }
            }
        }
        return null;
    }

    public void updateHallInfo(String hallId, String name, String hallType) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_HALL_INFO)) {
            ps.setString(1, name);
            ps.setString(2, hallType);
            ps.setString(3, hallId);
            ps.executeUpdate();
        }
    }

    public List<Seat> findSeatsByHall(String hallId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_SEATS_BY_HALL)) {
            ps.setString(1, hallId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapSeat(rs));
                }
            }
        }
        return seats;
    }

    /**
     * Cập nhật giá mặc định cho tất cả ghế cùng loại trong một phòng chiếu.
     *
     * @param hallId   mã phòng chiếu
     * @param seatType loại ghế (REGULAR hoặc VIP)
     * @param newPrice giá mới (VND)
     */
    public void updateSeatPriceByType(String hallId, SeatType seatType, double newPrice) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_SEAT_PRICE_BY_TYPE)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, hallId);
            ps.setString(3, seatType.name()); // "REGULAR" hoặc "VIP"
            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật trạng thái hỏng của nhiều ghế cùng lúc.
     */
    public void updateSeatStatuses(Iterable<Seat> seats) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        String sql = "UPDATE seats SET is_broken = ? WHERE seat_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Seat seat : seats) {
                ps.setBoolean(1, seat.isBroken());
                ps.setString(2, seat.getSeatId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Mappers
    // ─────────────────────────────────────────────────────────────

    private CinemaHall mapHall(ResultSet rs) throws SQLException {
        return new CinemaHall(
                rs.getString("hall_id"),
                rs.getString("name"),
                rs.getString("hall_type"),
                rs.getInt("total_rows"),
                rs.getInt("total_cols"),
                CinemaHallStatus.fromString(rs.getString("status")));
    }

    private Seat mapSeat(ResultSet rs) throws SQLException {
        String seatTypeStr = rs.getString("seat_type");
        SeatType seatType = "VIP".equalsIgnoreCase(seatTypeStr) ? SeatType.VIP : SeatType.REGULAR;
        Seat seat = new Seat(
                rs.getString("seat_id"),
                rs.getString("hall_id"),
                rs.getString("row_char").charAt(0),
                rs.getInt("col_number"),
                seatType,
                rs.getDouble("seat_price"));
        seat.setBroken(rs.getBoolean("is_broken"));
        return seat;
    }
}

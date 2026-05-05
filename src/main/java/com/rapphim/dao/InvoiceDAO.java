package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Invoice;
import com.rapphim.model.enums.InvoiceStatus;
import com.rapphim.model.enums.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // ── Find by date range ───────────────────────────────────────────────────
    private static final String SQL_FIND_BY_DATE = "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "WHERE CAST(i.created_at AS DATE) BETWEEN ? AND ? " +
            "ORDER BY i.created_at DESC";

    // ── Find all (no date filter) ────────────────────────────────────────────
    private static final String SQL_FIND_ALL = "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "ORDER BY i.created_at DESC";

    // ── Find invoice detail lines ────────────────────────────────────────────
    private static final String SQL_FIND_DETAILS = "SELECT mv.title, " +
            "       mv.movie_id, " +
            "       t.ticket_id, " +
            "       CONCAT(se.row_char, se.col_number) AS seat_label, " +
            "       t.final_price AS price " +
            "FROM tickets t " +
            "JOIN show_seats ss ON t.show_seat_id = ss.show_seat_id " +
            "JOIN showtimes sh  ON ss.showtime_id  = sh.showtime_id " +
            "JOIN movies mv     ON sh.movie_id     = mv.movie_id " +
            "JOIN seats se      ON ss.seat_id      = se.seat_id " +
            "WHERE t.invoice_id = ? " +
            "ORDER BY se.row_char, se.col_number";

    // ── Update status ────────────────────────────────────────────────────────
    private static final String SQL_UPDATE_STATUS = "UPDATE invoices SET status = ? WHERE invoice_id = ?";

    // ── Find by date range + employee ────────────────────────────────────────
    private static final String SQL_FIND_BY_DATE_AND_EMP =
            "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "WHERE CAST(i.created_at AS DATE) BETWEEN ? AND ? AND i.employee_id = ? " +
            "ORDER BY i.created_at DESC";


    // ═════════════════════════════════════════════════════════════════════════

    public List<Invoice> findAll() throws SQLException {
        List<Invoice> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public List<Invoice> findByDate(Date from, Date to) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_DATE)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        }
        return list;
    }

    public List<Invoice> findByDateAndEmployee(Date from, Date to, String employeeId) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_DATE_AND_EMP)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            ps.setString(3, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }


    public List<String[]> findInvoiceDetails(String invoiceId) throws SQLException {
        List<String[]> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_DETAILS)) {
            ps.setString(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[] {
                            rs.getString("title"),
                            rs.getString("movie_id"),
                            rs.getString("ticket_id"),
                            rs.getString("seat_label"),
                            String.valueOf(rs.getDouble("price"))
                    });
                }
            }
        }
        return list;
    }

    public void updateStatus(String invoiceId, InvoiceStatus status) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.getValue());
            ps.setString(2, invoiceId);
            ps.executeUpdate();
        }
    }

    // ── Mapper ───────────────────────────────────────────────────────────────
    private Invoice map(ResultSet rs) throws SQLException {
        return new Invoice(
                rs.getString("invoice_id"),
                rs.getString("employee_id"),
                rs.getString("full_name"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getDouble("total_amount"),
                rs.getInt("total_tickets"),
                Payment.fromString(rs.getString("payment_method")),
                InvoiceStatus.fromString(rs.getString("status")),
                rs.getString("note"));
    }

    // ── ID Generators ────────────────────────────────────────────────────────
    private String generateNextInvoiceId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(invoice_id, 4, LEN(invoice_id)) AS INT)) FROM invoices WHERE invoice_id LIKE 'INV%' AND LEN(invoice_id) <= 10";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int maxId = rs.next() ? rs.getInt(1) : 0;
            return String.format("INV%03d", maxId + 1);
        }
    }

    private int generateNextTicketNumber(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(ticket_id, 4, LEN(ticket_id)) AS INT)) FROM tickets WHERE ticket_id LIKE 'TKT%' AND LEN(ticket_id) <= 10";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    // ── Process Checkout (Transaction) ───────────────────────────────────────
    public boolean processCheckout(String showtimeId, java.util.Map<com.rapphim.model.Seat, Double> cart,
            double totalAmount, String paymentMethod, String status) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        boolean previousAutoCommit = true;

        try {
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // Bắt đầu giao dịch (Transaction)

            // 1. Sinh ID Hóa Đơn và ID Vé kế tiếp
            String newInvoiceId = generateNextInvoiceId(conn);
            int nextTicketNum = generateNextTicketNumber(conn);

            // 2. Thêm Hóa Đơn
            String sqlInvoice = "INSERT INTO invoices (invoice_id, employee_id, created_at, total_amount, total_tickets, payment_method, status) VALUES (?, ?, GETDATE(), ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInvoice)) {
                ps.setString(1, newInvoiceId);
                ps.setString(2, EmployeeDAO.getLoggedInEmployee()); // ID của nhân viên đăng nhập
                ps.setDouble(3, totalAmount);
                ps.setInt(4, cart.size());
                ps.setString(5, paymentMethod);
                ps.setString(6, status);
                ps.executeUpdate();
            }

            // 3. Xử lý từng ghế
            String sqlGetSeat = "SELECT show_seat_id FROM show_seats WHERE showtime_id = ? AND seat_id = ?";
            String sqlUpdateSeat = "UPDATE show_seats SET status = 'BOOKED' WHERE show_seat_id = ?";
            String sqlTicket = "INSERT INTO tickets (ticket_id, invoice_id, show_seat_id, original_price, final_price, barcode, status) VALUES (?, ?, ?, ?, ?, ?, 'VALID')";

            try (PreparedStatement psGetSeat = conn.prepareStatement(sqlGetSeat);
                    PreparedStatement psUpdateSeat = conn.prepareStatement(sqlUpdateSeat);
                    PreparedStatement psTicket = conn.prepareStatement(sqlTicket)) {

                for (java.util.Map.Entry<com.rapphim.model.Seat, Double> entry : cart.entrySet()) {
                    com.rapphim.model.Seat seat = entry.getKey();
                    double price = entry.getValue();

                    // 3.1. Lấy mã show_seat_id
                    String showSeatId = null;
                    psGetSeat.setString(1, showtimeId);
                    psGetSeat.setString(2, seat.getSeatId());
                    try (ResultSet rs = psGetSeat.executeQuery()) {
                        if (rs.next()) {
                            showSeatId = rs.getString("show_seat_id");
                        } else {
                            throw new SQLException("Ghế " + seat.getSeatId() + " không tồn tại cho suất chiếu này!");
                        }
                    }

                    // 3.2. Cập nhật trạng thái ghế
                    psUpdateSeat.setString(1, showSeatId);
                    if (psUpdateSeat.executeUpdate() == 0) {
                        throw new SQLException("Không thể cập nhật trạng thái ghế " + seat.getSeatId() + "!");
                    }

                    // 3.3. Thêm Vé
                    String newTicketId = String.format("TKT%03d", nextTicketNum++);
                    String barcode = "BC" + System.currentTimeMillis() + seat.getSeatId();

                    psTicket.setString(1, newTicketId);
                    psTicket.setString(2, newInvoiceId);
                    psTicket.setString(3, showSeatId);
                    psTicket.setDouble(4, price);
                    psTicket.setDouble(5, price);
                    psTicket.setString(6, barcode);
                    psTicket.executeUpdate();
                }
            }

            conn.commit(); // Hoàn tất giao dịch

            // 4. Xuất vé và Hóa đơn ra file PDF
            com.rapphim.util.TicketsExporter.exportTickets(newInvoiceId);
            com.rapphim.util.InvoicePdfExporter.exportInvoice(newInvoiceId);

            return true;

        } catch (SQLException e) {
            try {
                conn.rollback(); // Hoàn tác nếu có lỗi
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;

        } finally {
            try {
                conn.setAutoCommit(previousAutoCommit); // Trả lại trạng thái ban đầu
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

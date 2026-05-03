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
    private static final String SQL_FIND_BY_DATE =
            "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "WHERE CAST(i.created_at AS DATE) BETWEEN ? AND ? " +
            "ORDER BY i.created_at DESC";

    // ── Find all (no date filter) ────────────────────────────────────────────
    private static final String SQL_FIND_ALL =
            "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "ORDER BY i.created_at DESC";

    // ── Find invoice detail lines ────────────────────────────────────────────
    private static final String SQL_FIND_DETAILS =
            "SELECT mv.title, " +
            "       CONCAT(se.row_char, se.col_number) AS seat_label, " +
            "       t.final_price AS price " +
            "FROM tickets t " +
            "JOIN showtimes sh ON t.showtime_id = sh.showtime_id " +
            "JOIN movies mv    ON sh.movie_id    = mv.movie_id " +
            "JOIN seats se     ON t.seat_id      = se.seat_id " +
            "WHERE t.invoice_id = ? " +
            "ORDER BY se.row_char, se.col_number";

    // ── Update status ────────────────────────────────────────────────────────
    private static final String SQL_UPDATE_STATUS =
            "UPDATE invoices SET status = ? WHERE invoice_id = ?";

    // ═════════════════════════════════════════════════════════════════════════

    public List<Invoice> findAll() throws SQLException {
        List<Invoice> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
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
                    list.add(new String[]{
                            rs.getString("title"),
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
                rs.getString("note")
        );
    }
}

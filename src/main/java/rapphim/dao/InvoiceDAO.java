package rapphim.dao;

import rapphim.config.DatabaseConnection;
import rapphim.model.Invoice;
import rapphim.model.enums.InvoiceStatus;
import rapphim.model.enums.Payment;

import java.sql.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    private static final String SQL_FIND_BY_DATE = "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "WHERE CAST(i.created_at AS DATE) BETWEEN ? AND ? " +
            "ORDER BY i.created_at DESC";

    private static final String SQL_FIND_ALL = "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "ORDER BY i.created_at DESC";

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

    private static final String SQL_UPDATE_STATUS = "UPDATE invoices SET status = ? WHERE invoice_id = ?";

    private static final String SQL_FIND_BY_DATE_AND_EMP = "SELECT i.invoice_id, i.employee_id, e.full_name, " +
            "       i.created_at, i.total_amount, i.total_tickets, " +
            "       i.payment_method, i.status, i.note " +
            "FROM invoices i " +
            "JOIN employees e ON i.employee_id = e.employee_id " +
            "WHERE CAST(i.created_at AS DATE) BETWEEN ? AND ? AND i.employee_id = ? " +
            "ORDER BY i.created_at DESC";

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
                while (rs.next())
                    list.add(map(rs));
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

    public String getNextInvoiceId(java.sql.Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(invoice_id, 4, LEN(invoice_id)) AS INT)) FROM invoices WHERE invoice_id LIKE 'INV%' AND LEN(invoice_id) <= 10";
        try (Statement st = conn.createStatement(); java.sql.ResultSet rs = st.executeQuery(sql)) {
            int maxId = rs.next() ? rs.getInt(1) : 0;
            return String.format("INV%03d", maxId + 1);
        }
    }

    public void insertInvoice(java.sql.Connection conn, String invoiceId, String employeeId,
            double totalAmount, int totalTickets, String paymentMethod, String status) throws SQLException {
        String sql = "INSERT INTO invoices (invoice_id, employee_id, created_at, total_amount, total_tickets, payment_method, status) VALUES (?, ?, GETDATE(), ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            ps.setString(2, employeeId);
            ps.setDouble(3, totalAmount);
            ps.setInt(4, totalTickets);
            ps.setString(5, paymentMethod);
            ps.setString(6, status);
            ps.executeUpdate();
        }
    }

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

}

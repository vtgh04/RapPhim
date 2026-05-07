package rapphim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Thao tác trực tiếp với cơ sở dữ liệu bảng tickets.
 * Chỉ chứa các câu lệnh SQL thuần (INSERT, SELECT, UPDATE).
 * Mọi business logic (barcode, workflow) nằm ở TicketService hoặc SaleService.
 */
public class TicketDao {

    public TicketDao() {
    }

    /**
     * Sinh số thứ tự vé kế tiếp (dùng trong transaction checkout).
     * Nhận Connection từ bên ngoài để tham gia cùng transaction.
     */
    public int getNextTicketNumber(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(ticket_id, 4, LEN(ticket_id)) AS INT)) FROM tickets WHERE ticket_id LIKE 'TKT%' AND LEN(ticket_id) <= 10";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    /**
     * Thêm một bản ghi vé vào database (dùng trong transaction checkout).
     * Nhận Connection từ bên ngoài để tham gia cùng transaction.
     */
    public void insertTicket(Connection conn, String ticketId, String invoiceId,
            String showSeatId, double originalPrice, double finalPrice, String barcode) throws SQLException {
        String sql = "INSERT INTO tickets (ticket_id, invoice_id, show_seat_id, original_price, final_price, barcode, status) VALUES (?, ?, ?, ?, ?, ?, 'VALID')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketId);
            ps.setString(2, invoiceId);
            ps.setString(3, showSeatId);
            ps.setDouble(4, originalPrice);
            ps.setDouble(5, finalPrice);
            ps.setString(6, barcode);
            ps.executeUpdate();
        }
    }
}

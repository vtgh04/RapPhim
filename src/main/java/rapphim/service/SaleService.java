package rapphim.service;

import rapphim.config.DatabaseConnection;
import rapphim.dao.InvoiceDAO;
import rapphim.dao.ShowtimeDAO;
import rapphim.dao.TicketDao;
import rapphim.model.Seat;
import rapphim.util.InvoicePdfExporter;
import rapphim.util.TicketsExporter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Service điều phối nghiệp vụ bán vé (Checkout).
 * 
 * Chịu trách nhiệm:
 * - Quản lý transaction (commit/rollback)
 * - Sinh mã ID (Invoice, Ticket, Barcode)
 * - Orchestration nhiều DAO trong 1 giao dịch
 * - Xuất PDF sau khi checkout thành công
 * 
 * Các DAO chỉ chứa SQL thuần, nhận Connection từ Service.
 */
public class SaleService {

    private final InvoiceDAO invoiceDAO;
    private final TicketDao ticketDao;
    private final ShowtimeDAO showtimeDAO;

    public SaleService() {
        this.invoiceDAO = new InvoiceDAO();
        this.ticketDao = new TicketDao();
        this.showtimeDAO = new ShowtimeDAO();
    }

    /**
     * Xử lý thanh toán toàn bộ giỏ hàng (Checkout Workflow):
     * 
     * 1. Validate giỏ hàng
     * 2. Bắt đầu Transaction
     * 3. Sinh mã Hóa đơn và mã Vé kế tiếp
     * 4. Thêm bản ghi Hóa đơn
     * 5. Với mỗi ghế trong giỏ:
     *    - Tìm show_seat_id
     *    - Cập nhật trạng thái ghế thành BOOKED
     *    - Sinh barcode
     *    - Thêm bản ghi Vé
     * 6. Commit Transaction
     * 7. Xuất PDF Hóa đơn & Vé
     * 
     * @param showtimeId    Mã suất chiếu
     * @param cart          Map ghế → giá
     * @param totalAmount   Tổng tiền thanh toán
     * @param paymentMethod Phương thức thanh toán (CASH/CARD/TRANSFER)
     * @param status        Trạng thái hóa đơn (CONFIRMED)
     * @return true nếu thanh toán thành công
     * @throws SQLException nếu có lỗi database
     */
    public boolean processCheckout(String showtimeId, Map<Seat, Double> cart,
            double totalAmount, String paymentMethod, String status) throws SQLException {

        // ── Validate nghiệp vụ ──────────────────────────────────────────
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng không được trống.");
        }
        if (showtimeId == null || showtimeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã suất chiếu không hợp lệ.");
        }

        // ── Transaction Management ──────────────────────────────────────
        Connection conn = DatabaseConnection.getInstance();
        boolean previousAutoCommit = true;

        try {
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Sinh mã Hóa đơn và mã Vé kế tiếp
            String newInvoiceId = invoiceDAO.getNextInvoiceId(conn);
            int nextTicketNum = ticketDao.getNextTicketNumber(conn);

            // 2. Lấy mã nhân viên đang đăng nhập
            String employeeId = AuthService.getLoggedInEmployee();

            // 3. Thêm bản ghi Hóa đơn
            invoiceDAO.insertInvoice(conn, newInvoiceId, employeeId,
                    totalAmount, cart.size(), paymentMethod, status);

            // 4. Xử lý từng ghế trong giỏ hàng
            for (Map.Entry<Seat, Double> entry : cart.entrySet()) {
                Seat seat = entry.getKey();
                double price = entry.getValue();

                // 4.1. Tìm mã show_seat_id
                String showSeatId = showtimeDAO.findShowSeatId(conn, showtimeId, seat.getSeatId());
                if (showSeatId == null) {
                    throw new SQLException("Ghế " + seat.getSeatId() + " không tồn tại cho suất chiếu này!");
                }

                // 4.2. Cập nhật trạng thái ghế thành BOOKED
                int updated = showtimeDAO.updateShowSeatStatus(conn, showSeatId);
                if (updated == 0) {
                    throw new SQLException("Không thể cập nhật trạng thái ghế " + seat.getSeatId() + "!");
                }

                // 4.3. Sinh mã vé và barcode (Business Logic)
                String newTicketId = String.format("TKT%03d", nextTicketNum++);
                String barcode = generateBarcode(seat.getSeatId());

                // 4.4. Thêm bản ghi Vé
                ticketDao.insertTicket(conn, newTicketId, newInvoiceId,
                        showSeatId, price, price, barcode);
            }

            // 5. Commit giao dịch
            conn.commit();

            // 6. Xuất PDF Hóa đơn & Vé (sau khi commit thành công)
            TicketsExporter.exportTickets(newInvoiceId);
            InvoicePdfExporter.exportInvoice(newInvoiceId);

            return true;

        } catch (SQLException e) {
            // Rollback nếu có lỗi
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;

        } finally {
            // Trả lại trạng thái autoCommit ban đầu
            try {
                conn.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ── Business Logic helpers ───────────────────────────────────────────────

    /**
     * Sinh mã barcode duy nhất cho vé.
     * Format: BC + timestamp + seatId
     */
    private String generateBarcode(String seatId) {
        return "BC" + System.currentTimeMillis() + seatId;
    }
}

package rapphim.dao;

/**
 * @deprecated Toàn bộ business logic đã chuyển sang {@link rapphim.service.SaleService}.
 * Các thao tác SQL thuần đã phân bổ vào:
 * - {@link InvoiceDAO#insertInvoice(java.sql.Connection, String, String, double, int, String, String)}
 * - {@link InvoiceDAO#getNextInvoiceId(java.sql.Connection)}
 * - {@link TicketDao#insertTicket(java.sql.Connection, String, String, String, double, double, String)}
 * - {@link TicketDao#getNextTicketNumber(java.sql.Connection)}
 * - {@link ShowtimeDAO#findShowSeatId(java.sql.Connection, String, String)}
 * - {@link ShowtimeDAO#updateShowSeatStatus(java.sql.Connection, String)}
 *
 * Lớp này chỉ được giữ lại để tham chiếu. Sẽ bị xóa trong bản release tiếp theo.
 */
@Deprecated(forRemoval = true)
public class SaleDao {

    public SaleDao() {
    }
}

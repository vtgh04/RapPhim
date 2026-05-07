package rapphim.dao;

import rapphim.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO chuyên biệt cho các truy vấn Dashboard/Analytics.
 * Tách ra từ InvoiceDAO để tuân thủ Single Responsibility Principle.
 */
public class DashboardDao {

    /**
     * Lấy doanh thu theo ngày trong 30 ngày gần nhất.
     */
    public List<Object[]> getRevenueByDay() throws SQLException {
        String sql = """
                    SELECT DAY(created_at) AS day,
                           SUM(total_amount) AS revenue
                    FROM invoices
                    WHERE created_at >= DATEADD(DAY, -30, GETDATE())
                    GROUP BY DAY(created_at)
                    ORDER BY day
                """;

        List<Object[]> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("day"),
                        rs.getDouble("revenue")
                });
            }
        }
        return list;
    }

    /**
     * Lấy top 5 phim bán chạy nhất (kèm poster).
     */
    public List<Object[]> getTopMovies() throws SQLException {

        String sql = """
                    SELECT TOP 5
                           m.title,
                           m.poster_url,
                           COUNT(*) AS tickets_sold
                    FROM tickets t
                    JOIN show_seats ss ON t.show_seat_id = ss.show_seat_id
                    JOIN showtimes st ON ss.showtime_id = st.showtime_id
                    JOIN movies m ON st.movie_id = m.movie_id
                    GROUP BY m.title, m.poster_url
                    ORDER BY tickets_sold DESC
                """;

        List<Object[]> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[] {
                        rs.getString("title"),
                        rs.getString("poster_url"),
                        rs.getInt("tickets_sold")
                });
            }
        }
        return list;
    }
}

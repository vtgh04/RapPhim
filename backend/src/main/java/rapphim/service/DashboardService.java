package rapphim.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> getRevenueByDay() {
        String sql = """
                    SELECT DAY(created_at) AS day,
                           SUM(total_amount) AS revenue
                    FROM invoices
                    WHERE created_at >= DATEADD(DAY, -30, GETDATE())
                    GROUP BY DAY(created_at)
                    ORDER BY day
                """;
        Query query = entityManager.createNativeQuery(sql);
        List<?> results = query.getResultList();
        
        List<Object[]> list = new ArrayList<>();
        for (Object obj : results) {
            Object[] row = (Object[]) obj;
            int day = ((Number) row[0]).intValue();
            double revenue = ((Number) row[1]).doubleValue();
            list.add(new Object[] { day, revenue });
        }
        return list;
    }

    public List<Object[]> getTopMovies() {
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
        Query query = entityManager.createNativeQuery(sql);
        List<?> results = query.getResultList();
        
        List<Object[]> list = new ArrayList<>();
        for (Object obj : results) {
            Object[] row = (Object[]) obj;
            String title = (String) row[0];
            String posterUrl = (String) row[1];
            int ticketsSold = ((Number) row[2]).intValue();
            list.add(new Object[] { title, posterUrl, ticketsSold });
        }
        return list;
    }
}

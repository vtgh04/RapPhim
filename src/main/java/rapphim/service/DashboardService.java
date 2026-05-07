package rapphim.service;

import rapphim.dao.DashboardDao;

import java.util.List;

public class DashboardService {

    private final DashboardDao dashboardDao;

    public DashboardService() {
        this.dashboardDao = new DashboardDao();
    }

    /**
     * Lấy doanh thu theo ngày trong 30 ngày gần nhất.
     * Mỗi phần tử: [int day, double revenue]
     */
    public List<Object[]> getRevenueByDay() throws Exception {
        return dashboardDao.getRevenueByDay();
    }

    /**
     * Lấy top 5 phim bán chạy nhất.
     * Mỗi phần tử: [String title, String posterUrl, int ticketsSold]
     */
    public List<Object[]> getTopMovies() throws Exception {
        return dashboardDao.getTopMovies();
    }
}

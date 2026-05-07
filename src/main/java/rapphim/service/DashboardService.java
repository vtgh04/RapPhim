package rapphim.service;

import rapphim.dao.DashboardDao;

import java.util.List;

/**
 * Service chuyên biệt cho nghiệp vụ Dashboard/Thống kê.
 * Lấy dữ liệu từ DashboardDao và có thể thêm business logic xử lý
 * trước khi trả về cho Panel (ví dụ: tính toán tỷ lệ, format dữ liệu).
 */
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

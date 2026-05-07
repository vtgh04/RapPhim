package rapphim.service;

import rapphim.dao.HallDao;
import rapphim.model.CinemaHall;
import rapphim.model.Seat;
import rapphim.model.enums.CinemaHallStatus;
import rapphim.model.enums.SeatType;

import java.sql.SQLException;
import java.util.List;

public class HallService {

    private HallDao hallDao;

    public HallService() {
        this.hallDao = new HallDao();
    }

    /**
     * Lấy danh sách tất cả các phòng chiếu hiện có trong rạp.
     */
    public List<CinemaHall> getAllHalls() throws SQLException {
        return hallDao.findAllHalls();
    }

    /**
     * Tìm phòng chiếu theo mã số (ID).
     */
    public CinemaHall getHallById(String hallId) throws SQLException {
        if (hallId == null || hallId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng chiếu không hợp lệ.");
        }
        return hallDao.findHallById(hallId);
    }

    /**
     * Lấy danh sách toàn bộ ghế thuộc một phòng chiếu cụ thể.
     */
    public List<Seat> getSeatsByHall(String hallId) throws SQLException {
        if (hallId == null || hallId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng chiếu không hợp lệ.");
        }
        return hallDao.findSeatsByHall(hallId);
    }

    /**
     * Cập nhật thông tin cơ bản của phòng chiếu (tên rạp, loại phòng).
     */
    public void updateHallInfo(String hallId, String name, String hallType) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng chiếu không được để trống.");
        }
        if (hallType == null || hallType.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại phòng chiếu không được để trống.");
        }
        hallDao.updateHallInfo(hallId, name, hallType);
    }

    /**
     * Cập nhật trạng thái hoạt động của phòng chiếu.
     */
    public void updateHallStatus(String hallId, CinemaHallStatus status) throws SQLException {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái phòng chiếu không hợp lệ.");
        }
        hallDao.updateHallStatus(hallId, status);
    }

    /**
     * Cập nhật hệ số giá mặc định cho tất cả ghế cùng loại trong một phòng chiếu.
     */
    public void updateSeatFactorByType(String hallId, SeatType seatType, double newFactor) throws SQLException {
        if (newFactor < 0) {
            throw new IllegalArgumentException("Hệ số giá không thể nhỏ hơn 0.");
        }
        hallDao.updateSeatFactorByType(hallId, seatType, newFactor);
    }

    /**
     * Cập nhật trạng thái hỏng của nhiều ghế cùng lúc.
     */
    public void updateSeatStatuses(Iterable<Seat> seats) throws SQLException {
        if (seats == null) {
            throw new IllegalArgumentException("Danh sách ghế không hợp lệ.");
        }
        hallDao.updateSeatStatuses(seats);
    }
}

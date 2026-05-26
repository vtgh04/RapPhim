package rapphim.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rapphim.model.CinemaHall;
import rapphim.model.Seat;
import rapphim.model.enums.CinemaHallStatus;
import rapphim.model.enums.SeatType;
import rapphim.repository.CinemaHallRepository;
import rapphim.repository.SeatRepository;
import rapphim.repository.ShowtimeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class HallService {

    private final CinemaHallRepository cinemaHallRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    public HallService(CinemaHallRepository cinemaHallRepository,
                       SeatRepository seatRepository,
                       ShowtimeRepository showtimeRepository) {
        this.cinemaHallRepository = cinemaHallRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public List<CinemaHall> getAllHalls() {
        return cinemaHallRepository.findAll();
    }

    public CinemaHall getHallById(String hallId) {
        if (hallId == null || hallId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng chiếu không hợp lệ.");
        }
        return cinemaHallRepository.findById(hallId).orElse(null);
    }

    public List<Seat> getSeatsByHall(String hallId) {
        if (hallId == null || hallId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng chiếu không hợp lệ.");
        }
        return seatRepository.findByHallIdOrderByRowCharAscColNumberAsc(hallId);
    }

    @Transactional
    public void updateHallInfo(String hallId, String name, String hallType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng chiếu không được để trống.");
        }
        if (hallType == null || hallType.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại phòng chiếu không được để trống.");
        }
        CinemaHall hall = cinemaHallRepository.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Phòng chiếu không tồn tại: " + hallId));
        hall.setName(name);
        hall.setHallType(hallType);
        cinemaHallRepository.save(hall);
    }

    @Transactional
    public void updateHallStatus(String hallId, CinemaHallStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái phòng chiếu không hợp lệ.");
        }
        CinemaHall hall = cinemaHallRepository.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Phòng chiếu không tồn tại: " + hallId));
        hall.setStatus(status);
        cinemaHallRepository.save(hall);
    }

    @Transactional
    public void updateSeatFactorByType(String hallId, SeatType seatType, double newFactor) {
        if (newFactor < 0) {
            throw new IllegalArgumentException("Hệ số giá không thể nhỏ hơn 0.");
        }
        List<Seat> seats = seatRepository.findByHallIdOrderByRowCharAscColNumberAsc(hallId);
        for (Seat seat : seats) {
            if (seat.getSeatType() == seatType) {
                seat.setSeatFactor(newFactor);
            }
        }
        seatRepository.saveAll(seats);
    }

    @Transactional
    public void updateSeatStatuses(Iterable<Seat> seats) {
        if (seats == null) {
            throw new IllegalArgumentException("Danh sách ghế không hợp lệ.");
        }
        seatRepository.saveAll(seats);
    }

    /**
     * Creates a new cinema hall and auto-generates seats row by row.
     * Rows are labeled A, B, C... up to totalRows.
     * All seats default to STANDARD type with seatFactor=1.0.
     */
    @Transactional
    public CinemaHall createHall(CinemaHall hall) {
        if (cinemaHallRepository.existsById(hall.getHallId())) {
            throw new IllegalArgumentException("Mã phòng đã tồn tại: " + hall.getHallId());
        }
        hall.setStatus(CinemaHallStatus.ACTIVE);
        cinemaHallRepository.save(hall);

        List<Seat> seats = new ArrayList<>();
        for (int r = 0; r < hall.getTotalRows(); r++) {
            char rowChar = (char) ('A' + r);
            for (int c = 1; c <= hall.getTotalCols(); c++) {
                String seatId = hall.getHallId() + "-" + rowChar + c;
                // Last 2 rows are VIP
                SeatType type = (r >= hall.getTotalRows() - 2) ? SeatType.VIP : SeatType.REGULAR;
                double factor = type == SeatType.VIP ? 1.5 : 1.0;
                seats.add(new Seat(seatId, hall.getHallId(), rowChar, c, type, factor));
            }
        }
        seatRepository.saveAll(seats);
        return hall;
    }

    /**
     * Deletes a hall and all its seats.
     * Refuses if there are SCHEDULED showtimes linked to this hall.
     */
    @Transactional
    public void deleteHall(String hallId) {
        long scheduled = showtimeRepository.countByHallIdAndStatus(hallId,
                rapphim.model.enums.ShowtimeStatus.SCHEDULED);
        if (scheduled > 0) {
            throw new IllegalStateException(
                "Không thể xóa phòng " + hallId + " vì còn " + scheduled + " suất chiếu đang lên lịch.");
        }
        seatRepository.deleteByHallId(hallId);
        cinemaHallRepository.deleteById(hallId);
    }
}

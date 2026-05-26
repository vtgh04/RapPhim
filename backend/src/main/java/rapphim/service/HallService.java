package rapphim.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rapphim.model.CinemaHall;
import rapphim.model.Seat;
import rapphim.model.enums.CinemaHallStatus;
import rapphim.model.enums.SeatType;
import rapphim.repository.CinemaHallRepository;
import rapphim.repository.SeatRepository;

import java.util.List;

@Service
public class HallService {

    private final CinemaHallRepository cinemaHallRepository;
    private final SeatRepository seatRepository;

    public HallService(CinemaHallRepository cinemaHallRepository, SeatRepository seatRepository) {
        this.cinemaHallRepository = cinemaHallRepository;
        this.seatRepository = seatRepository;
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
}

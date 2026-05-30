package rapphim.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rapphim.model.Movie;
import rapphim.model.Showtime;
import rapphim.model.ShowSeat;
import rapphim.model.Seat;
import rapphim.model.enums.ShowSeatStatus;
import rapphim.model.enums.ShowtimeStatus;
import rapphim.repository.ShowtimeRepository;
import rapphim.repository.SeatRepository;
import rapphim.repository.ShowSeatRepository;
import rapphim.util.ShowtimeExcelUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final ShowSeatRepository showSeatRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, SeatRepository seatRepository, ShowSeatRepository showSeatRepository) {
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.showSeatRepository = showSeatRepository;
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public List<Showtime> getTodayShowtimes() {
        LocalDateTime from = LocalDateTime.now().toLocalDate().atStartOfDay();
        return getShowtimesByDateRange(from, from.plusHours(24));
    }

    public List<Showtime> getShowtimesByDateRange(LocalDateTime from, LocalDateTime to) {
        return showtimeRepository.findByStartTimeBetweenOrderByMovieIdAscStartTimeAsc(from, to);
    }

    public Showtime getShowtimeById(String showtimeId) {
        return showtimeRepository.findById(showtimeId).orElse(null);
    }

    public long countAll() {
        return showtimeRepository.count();
    }

    public long countByStatus(ShowtimeStatus status) {
        return showtimeRepository.countByStatus(status);
    }

    public long countActiveHalls() {
        return showtimeRepository.countActiveHalls();
    }

    @Transactional
    public Showtime addShowtime(Showtime st) {
        if (st.getStartTime().isAfter(st.getEndTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu không thể sau thời gian kết thúc.");
        }
        if (hasOverlap(st.getHallId(), st.getStartTime(), st.getEndTime())) {
            throw new IllegalArgumentException("Phòng chiếu đã có suất chiếu trong khung giờ này.");
        }
        if (st.getShowtimeId() == null || st.getShowtimeId().trim().isEmpty()) {
            st.setShowtimeId(getNextShowtimeId());
        }
        Showtime saved = showtimeRepository.save(st);
        generateShowSeats(saved.getShowtimeId(), saved.getHallId(), saved.getBasePrice());
        return saved;
    }

    @Transactional
    public void deleteShowtime(String showtimeId) {
        showSeatRepository.deleteByShowtimeId(showtimeId);
        showtimeRepository.deleteById(showtimeId);
    }

    @Transactional
    public void updateBasePrice(String showtimeId, double newPrice) {
        if (newPrice < 0) {
            throw new IllegalArgumentException("Giá không được âm.");
        }
        Showtime st = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại: " + showtimeId));
        st.setBasePrice(newPrice);
        showtimeRepository.save(st);
    }

    @Transactional
    public void updateStatus(String showtimeId, ShowtimeStatus status) {
        Showtime st = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Suất chiếu không tồn tại: " + showtimeId));
        st.setStatus(status);
        showtimeRepository.save(st);
    }

    @Transactional
    public Showtime updateInfo(Showtime st) {
        if (st.getStartTime().isAfter(st.getEndTime())) {
            throw new IllegalArgumentException("Thời gian bắt đầu không thể sau thời gian kết thúc.");
        }
        return showtimeRepository.save(st);
    }

    @Transactional
    public void autoUpdateStatuses(LocalDateTime now) {
        showtimeRepository.autoUpdateCompleted(now);
        showtimeRepository.autoUpdateOngoing(now);
        showtimeRepository.autoUpdateScheduled(now);
    }

    public String getNextShowtimeId() {
        String maxId = showtimeRepository.findMaxShowtimeId();
        if (maxId != null) {
            try {
                int next = Integer.parseInt(maxId.replaceAll("[^0-9]", "")) + 1;
                return String.format("SHW%03d", next);
            } catch (NumberFormatException ignored) {}
        }
        return "SHW001";
    }

    public boolean hasOverlap(String hallId, LocalDateTime newStart, LocalDateTime newEnd) {
        return showtimeRepository.countOverlappingShowtimes(hallId, newStart, newEnd) > 0;
    }

    @Transactional
    public void generateShowSeats(String showtimeId, String hallId, double basePrice) {
        List<Seat> seats = seatRepository.findByHallIdAndIsBrokenFalse(hallId);
        List<ShowSeat> showSeats = seats.stream().map(seat -> {
            String showSeatId = showtimeId + "_" + seat.getSeatId();
            double price = basePrice * seat.getSeatFactor();
            return new ShowSeat(showSeatId, showtimeId, seat.getSeatId(), price, ShowSeatStatus.AVAILABLE, null);
        }).collect(Collectors.toList());
        showSeatRepository.saveAll(showSeats);
    }

    public Map<String, ShowSeatStatus> getShowSeatStatuses(String showtimeId) {
        List<ShowSeat> showSeats = showSeatRepository.findByShowtimeId(showtimeId);
        LocalDateTime now = LocalDateTime.now();
        return showSeats.stream().collect(Collectors.toMap(
                ShowSeat::getSeatId,
                ss -> {
                    if (ss.getStatus() == ShowSeatStatus.HELD && ss.getHeldUntil() != null && ss.getHeldUntil().isBefore(now)) {
                        return ShowSeatStatus.AVAILABLE;
                    }
                    return ss.getStatus();
                }
        ));
    }

    public void exportToExcel(List<Showtime> showtimes, Map<String, Movie> movieCache, File file) throws Exception {
        ShowtimeExcelUtils.exportToExcel(showtimes, movieCache, file);
    }
}

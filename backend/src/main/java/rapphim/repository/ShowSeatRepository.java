package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rapphim.model.ShowSeat;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, String> {
    List<ShowSeat> findByShowtimeId(String showtimeId);
    Optional<ShowSeat> findByShowtimeIdAndSeatId(String showtimeId, String seatId);

    @org.springframework.transaction.annotation.Transactional
    void deleteByShowtimeId(String showtimeId);
}

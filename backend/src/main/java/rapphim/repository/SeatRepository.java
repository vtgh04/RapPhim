package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rapphim.model.Seat;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findByHallIdOrderByRowCharAscColNumberAsc(String hallId);
    List<Seat> findByHallIdAndIsBrokenFalse(String hallId);
    void deleteByHallId(String hallId);
}

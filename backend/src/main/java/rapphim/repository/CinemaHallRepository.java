package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rapphim.model.CinemaHall;

@Repository
public interface CinemaHallRepository extends JpaRepository<CinemaHall, String> {
}

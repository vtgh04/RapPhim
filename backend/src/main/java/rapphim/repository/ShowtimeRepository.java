package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rapphim.model.Showtime;
import rapphim.model.enums.ShowtimeStatus;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, String> {
    
    @Query("SELECT MAX(s.showtimeId) FROM Showtime s")
    String findMaxShowtimeId();

    List<Showtime> findByStartTimeBetweenOrderByMovieIdAscStartTimeAsc(LocalDateTime start, LocalDateTime end);

    List<Showtime> findByMovieId(String movieId);

    @Query("SELECT COUNT(s.showtimeId) FROM Showtime s WHERE s.hallId = :hallId AND s.status != rapphim.model.enums.ShowtimeStatus.CANCELLED AND s.startTime < :endTime AND s.endTime > :startTime")
    long countOverlappingShowtimes(@Param("hallId") String hallId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Modifying
    @Query("UPDATE Showtime s SET s.status = rapphim.model.enums.ShowtimeStatus.COMPLETED WHERE s.status NOT IN (rapphim.model.enums.ShowtimeStatus.COMPLETED, rapphim.model.enums.ShowtimeStatus.CANCELLED) AND s.endTime < :now")
    int autoUpdateCompleted(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Showtime s SET s.status = rapphim.model.enums.ShowtimeStatus.ONGOING WHERE s.status NOT IN (rapphim.model.enums.ShowtimeStatus.ONGOING, rapphim.model.enums.ShowtimeStatus.CANCELLED) AND s.startTime <= :now AND s.endTime >= :now")
    int autoUpdateOngoing(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Showtime s SET s.status = rapphim.model.enums.ShowtimeStatus.SCHEDULED WHERE s.status NOT IN (rapphim.model.enums.ShowtimeStatus.SCHEDULED, rapphim.model.enums.ShowtimeStatus.CANCELLED) AND s.startTime > :now")
    int autoUpdateScheduled(@Param("now") LocalDateTime now);

    long countByStatus(ShowtimeStatus status);

    long countByHallIdAndStatus(String hallId, ShowtimeStatus status);

    @Query("SELECT COUNT(DISTINCT s.hallId) FROM Showtime s WHERE s.status IN (rapphim.model.enums.ShowtimeStatus.SCHEDULED, rapphim.model.enums.ShowtimeStatus.ONGOING)")
    long countActiveHalls();

}

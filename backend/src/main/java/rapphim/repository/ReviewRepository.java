package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rapphim.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findByMovieIdOrderByCreatedAtDesc(String movieId);

    @Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.movieId = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") String movieId);

    boolean existsByMovieIdAndUserId(String movieId, String userId);
}

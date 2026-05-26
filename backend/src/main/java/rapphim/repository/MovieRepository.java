package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rapphim.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    @Query("SELECT MAX(m.movieId) FROM Movie m")
    String findMaxMovieId();
}

package rapphim.service;

import org.springframework.stereotype.Service;
import rapphim.model.Movie;
import rapphim.repository.MovieRepository;
import rapphim.util.MovieExcelUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final rapphim.repository.ShowtimeRepository showtimeRepository;
    private final ShowtimeService showtimeService;

    public MovieService(MovieRepository movieRepository, 
                        rapphim.repository.ShowtimeRepository showtimeRepository, 
                        ShowtimeService showtimeService) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
        this.showtimeService = showtimeService;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    public Movie addMovie(Movie movie) {
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phim không được để trống!");
        }
        if (movie.getMovieId() == null || movie.getMovieId().trim().isEmpty()) {
            movie.setMovieId(getNextMovieId());
        }
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Movie movie) {
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phim không được để trống!");
        }
        if (movie.getMovieId() == null || movie.getMovieId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phim không hợp lệ để cập nhật!");
        }
        return movieRepository.save(movie);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteMovie(String id) {
        List<rapphim.model.Showtime> showtimes = showtimeRepository.findByMovieId(id);
        for (rapphim.model.Showtime st : showtimes) {
            showtimeService.deleteShowtime(st.getShowtimeId());
        }
        movieRepository.deleteById(id);
    }

    public String getNextMovieId() {
        String maxId = movieRepository.findMaxMovieId();
        if (maxId != null && maxId.startsWith("MOV")) {
            try {
                int num = Integer.parseInt(maxId.substring(3));
                return String.format("MOV%03d", num + 1);
            } catch (NumberFormatException ignored) {}
        }
        return "MOV001";
    }

    public void exportMovies(List<Movie> movies, File file) throws Exception {
        MovieExcelUtils.exportToExcel(movies, file);
    }

    public List<Movie> importMovies(File file) throws Exception {
        return MovieExcelUtils.importFromExcel(file, this::addMovie, this::getNextMovieId);
    }
}

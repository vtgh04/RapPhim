package com.rapphim.service;

import com.rapphim.dao.MovieDAO;
import com.rapphim.model.Movie;
import com.rapphim.util.MovieExcelUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MovieService {

    private MovieDAO movieDAO;

    public MovieService() {
        this.movieDAO = new MovieDAO();
    }

    public List<Movie> getAllMovies() throws SQLException {
        return movieDAO.findAll();
    }

    public Optional<Movie> getMovieById(String id) throws SQLException {
        return movieDAO.findById(id);
    }

    public void addMovie(Movie movie) throws SQLException {
        // Có thể thêm logic kiểm tra hợp lệ tại đây
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phim không được để trống!");
        }
        movieDAO.insert(movie);
    }

    public void updateMovie(Movie movie) throws SQLException {
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phim không được để trống!");
        }
        movieDAO.update(movie);
    }

    public String getNextMovieId() throws SQLException {
        return movieDAO.getNextMovieId();
    }

    public void exportMovies(List<Movie> movies, File file) throws Exception {
        MovieExcelUtils.exportToExcel(movies, file);
    }

    public List<Movie> importMovies(File file) throws Exception {
        return MovieExcelUtils.importFromExcel(file, movieDAO);
    }
}

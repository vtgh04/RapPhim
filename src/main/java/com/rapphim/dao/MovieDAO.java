package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Movie;
import com.rapphim.model.enums.MovieStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieDAO {

    private static final String SQL_FIND_ALL = "SELECT movie_id, title, genre, duration_mins, format_movie, language, release_date, status, description, poster_url FROM movies ORDER BY movie_id";

    private static final String SQL_FIND_BY_ID = "SELECT movie_id, title, genre, duration_mins, format_movie, language, release_date, status, description, poster_url FROM movies WHERE movie_id = ?";

    private static final String SQL_INSERT = "INSERT INTO movies (movie_id, title, genre, duration_mins, format_movie, language, release_date, status, description, poster_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE movies SET title = ?, genre = ?, duration_mins = ?, format_movie = ?, language = ?, release_date = ?, status = ?, description = ?, poster_url = ? WHERE movie_id = ?";

    private static final String SQL_MAX_ID = "SELECT MAX(movie_id) AS max_id FROM movies";

    public List<Movie> findAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                movies.add(mapRow(rs));
            }
        }
        return movies;
    }

    public Optional<Movie> findById(String id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void insert(Movie movie) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, movie.getMovieId());
            ps.setNString(2, movie.getTitle());
            ps.setNString(3, movie.getGenre());
            ps.setInt(4, movie.getDurationMins());
            ps.setString(5, movie.getFormatMovie());
            ps.setNString(6, movie.getLanguage());
            if (movie.getReleaseDate() != null) {
                ps.setDate(7, java.sql.Date.valueOf(movie.getReleaseDate()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            ps.setString(8, movie.getStatus().getValue());
            ps.setNString(9, movie.getDescription());
            ps.setString(10, movie.getPosterUrl());
            ps.executeUpdate();
        }
    }

    public void update(Movie movie) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setNString(1, movie.getTitle());
            ps.setNString(2, movie.getGenre());
            ps.setInt(3, movie.getDurationMins());
            ps.setString(4, movie.getFormatMovie());
            ps.setNString(5, movie.getLanguage());
            if (movie.getReleaseDate() != null) {
                ps.setDate(6, java.sql.Date.valueOf(movie.getReleaseDate()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            ps.setString(7, movie.getStatus().getValue());
            ps.setNString(8, movie.getDescription());
            ps.setString(9, movie.getPosterUrl());
            ps.setString(10, movie.getMovieId());
            ps.executeUpdate();
        }
    }

    public String getNextMovieId() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_MAX_ID);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxId = rs.getString("max_id");
                if (maxId != null && maxId.startsWith("MOV")) {
                    int num = Integer.parseInt(maxId.substring(3));
                    return String.format("MOV%03d", num + 1);
                }
            }
        }
        return "MOV001";
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        java.sql.Date sqlDate = rs.getDate("release_date");
        java.time.LocalDate releaseDate = sqlDate != null ? sqlDate.toLocalDate() : null;

        return new Movie(
                rs.getString("movie_id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("duration_mins"),
                rs.getString("format_movie"),
                rs.getString("language"),
                releaseDate,
                MovieStatus.fromString(rs.getString("status")),
                rs.getString("description"),
                rs.getString("poster_url"));
    }
}

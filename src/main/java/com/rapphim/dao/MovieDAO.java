package com.rapphim.dao;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Movie;
import com.rapphim.model.enums.MovieStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieDAO {

    private static final String COLS =
            "movie_id, title, genre, duration_mins, format_movie, rating, language," +
            " release_date, status, description, poster_url";

    private static final String SQL_FIND_ALL =
            "SELECT " + COLS + " FROM movies ORDER BY movie_id";

    private static final String SQL_FIND_BY_ID =
            "SELECT " + COLS + " FROM movies WHERE movie_id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO movies (" + COLS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE movies SET title = ?, genre = ?, duration_mins = ?, format_movie = ?, rating = ?," +
            " language = ?, release_date = ?, status = ?, description = ?, poster_url = ?" +
            " WHERE movie_id = ?";

    private static final String SQL_MAX_ID =
            "SELECT MAX(movie_id) AS max_id FROM movies";

    // ═════════════════════════════════════════════════════════════════════════

    public List<Movie> findAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) movies.add(mapRow(rs));
        }
        return movies;
    }

    public Optional<Movie> findById(String id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public void insert(Movie movie) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, movie.getMovieId());
            ps.setNString(2, movie.getTitle());
            ps.setNString(3, movie.getGenre());
            ps.setInt(4, movie.getDurationMins());
            ps.setString(5, movie.getFormatMovie());
            ps.setString(6, movie.getRating());
            ps.setNString(7, movie.getLanguage());
            setDateOrNull(ps, 8, movie.getReleaseDate());
            ps.setString(9, movie.getStatus().getValue());
            ps.setNString(10, movie.getDescription());
            ps.setString(11, movie.getPosterUrl());
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
            ps.setString(5, movie.getRating());
            ps.setNString(6, movie.getLanguage());
            setDateOrNull(ps, 7, movie.getReleaseDate());
            ps.setString(8, movie.getStatus().getValue());
            ps.setNString(9, movie.getDescription());
            ps.setString(10, movie.getPosterUrl());
            ps.setString(11, movie.getMovieId());
            ps.executeUpdate();
        }
    }

    public String getNextMovieId() throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SQL_MAX_ID)) {
            if (rs.next()) {
                String maxId = rs.getString("max_id");
                if (maxId != null && maxId.startsWith("MOV")) {
                    return String.format("MOV%03d", Integer.parseInt(maxId.substring(3)) + 1);
                }
            }
        }
        return "MOV001";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Sets a DATE parameter, or SQL NULL if the value is null. */
    private static void setDateOrNull(PreparedStatement ps, int idx, LocalDate date) throws SQLException {
        if (date != null) ps.setDate(idx, Date.valueOf(date));
        else ps.setNull(idx, Types.DATE);
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("release_date");
        return new Movie(
                rs.getString("movie_id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("duration_mins"),
                rs.getString("format_movie"),
                rs.getString("rating"),
                rs.getString("language"),
                sqlDate != null ? sqlDate.toLocalDate() : null,
                MovieStatus.fromString(rs.getString("status")),
                rs.getString("description"),
                rs.getString("poster_url"));
    }
}

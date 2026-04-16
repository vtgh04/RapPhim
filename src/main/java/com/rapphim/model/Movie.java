package com.rapphim.model;

import com.rapphim.model.enums.MovieStatus;
import java.io.Serializable;
import java.time.LocalDate;

public class Movie implements Serializable {
    private String movieId;
    private String title;
    private String genre;
    private int durationMins;
    private String formatMovie;
    private String language;
    private LocalDate releaseDate;
    private MovieStatus status;
    private String description;
    private String posterUrl;

    public Movie() {}

    public Movie(String movieId, String title, String genre, int durationMins, String formatMovie,
                 String language, LocalDate releaseDate, MovieStatus status, 
                 String description, String posterUrl) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.durationMins = durationMins;
        this.formatMovie = formatMovie;
        this.language = language;
        this.releaseDate = releaseDate;
        this.status = status;
        this.description = description;
        this.posterUrl = posterUrl;
    }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDurationMins() { return durationMins; }
    public void setDurationMins(int durationMins) { this.durationMins = durationMins; }

    public String getFormatMovie() { return formatMovie; }
    public void setFormatMovie(String formatMovie) { this.formatMovie = formatMovie; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public MovieStatus getStatus() { return status; }
    public void setStatus(MovieStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId='" + movieId + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", durationMins=" + durationMins +
                ", formatMovie='" + formatMovie + '\'' +
                ", status=" + status +
                '}';
    }
}

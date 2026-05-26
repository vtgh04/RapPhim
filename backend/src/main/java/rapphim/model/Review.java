package rapphim.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * User review for a movie.
 * Table: dbo.reviews (created by DatabaseInitializer if not exists)
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @Column(name = "review_id")
    private String reviewId;

    @Column(name = "movie_id", nullable = false)
    @NotBlank
    private String movieId;

    @Column(name = "user_id", nullable = false)
    @NotBlank
    private String userId;

    @Column(name = "rating", nullable = false)
    @Min(1) @Max(5)
    private int rating;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Review() {}

    public Review(String reviewId, String movieId, String userId, int rating, String comment) {
        this.reviewId = reviewId;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

package rapphim.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rapphim.model.Review;
import rapphim.repository.ReviewRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST endpoints for movie reviews and ratings.
 * GET  /api/reviews/{movieId}        — public: list reviews + avg rating
 * POST /api/reviews                  — authenticated: submit a review
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /** Returns all reviews for a movie plus the computed average rating. */
    @GetMapping("/{movieId}")
    public ResponseEntity<Map<String, Object>> getReviewsForMovie(@PathVariable String movieId) {
        List<Review> reviews = reviewRepository.findByMovieIdOrderByCreatedAtDesc(movieId);
        Double avg = reviewRepository.findAverageRatingByMovieId(movieId);

        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviews);
        result.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : null);
        result.put("totalReviews", reviews.size());
        return ResponseEntity.ok(result);
    }

    /** Submit a new review. One review per user per movie enforced. */
    @PostMapping
    public ResponseEntity<?> submitReview(@Valid @RequestBody Review review, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Bạn cần đăng nhập để đánh giá phim."));
        }

        String userId = auth.getName();

        if (reviewRepository.existsByMovieIdAndUserId(review.getMovieId(), userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Bạn đã đánh giá bộ phim này rồi."));
        }

        review.setReviewId(UUID.randomUUID().toString());
        review.setUserId(userId);
        Review saved = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}

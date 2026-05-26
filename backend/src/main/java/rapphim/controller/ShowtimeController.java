package rapphim.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rapphim.model.Showtime;
import rapphim.model.enums.ShowSeatStatus;
import rapphim.model.enums.ShowtimeStatus;
import rapphim.service.ShowtimeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping
    public ResponseEntity<List<Showtime>> getAllShowtimes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        if (from != null && to != null) {
            return ResponseEntity.ok(showtimeService.getShowtimesByDateRange(from, to));
        }
        return ResponseEntity.ok(showtimeService.getAllShowtimes());
    }

    @GetMapping("/today")
    public ResponseEntity<List<Showtime>> getTodayShowtimes() {
        return ResponseEntity.ok(showtimeService.getTodayShowtimes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable String id) {
        Showtime st = showtimeService.getShowtimeById(id);
        if (st == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(st);
    }

    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@Valid @RequestBody Showtime showtime) {
        return ResponseEntity.status(HttpStatus.CREATED).body(showtimeService.addShowtime(showtime));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestParam ShowtimeStatus status) {
        showtimeService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/base-price")
    public ResponseEntity<?> updateBasePrice(@PathVariable String id, @RequestParam double price) {
        showtimeService.updateBasePrice(id, price);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<Map<String, ShowSeatStatus>> getShowSeatStatuses(@PathVariable String id) {
        return ResponseEntity.ok(showtimeService.getShowSeatStatuses(id));
    }

    @PostMapping("/sync-status")
    public ResponseEntity<?> syncShowtimeStatuses() {
        showtimeService.autoUpdateStatuses(LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShowtime(@PathVariable String id) {
        Showtime st = showtimeService.getShowtimeById(id);
        if (st == null) {
            return ResponseEntity.notFound().build();
        }
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable String id, @Valid @RequestBody Showtime showtime) {
        Showtime st = showtimeService.getShowtimeById(id);
        if (st == null) {
            return ResponseEntity.notFound().build();
        }
        showtime.setShowtimeId(id);
        // If status or price is updated, we can apply business rules if needed
        return ResponseEntity.ok(showtimeService.updateInfo(showtime));
    }
}

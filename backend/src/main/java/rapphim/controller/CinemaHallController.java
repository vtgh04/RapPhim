package rapphim.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rapphim.model.CinemaHall;
import rapphim.model.Seat;
import rapphim.model.enums.CinemaHallStatus;
import rapphim.service.HallService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cinema-halls")
public class CinemaHallController {

    private final HallService hallService;

    public CinemaHallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public ResponseEntity<List<CinemaHall>> getAllHalls() {
        return ResponseEntity.ok(hallService.getAllHalls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaHall> getHallById(@PathVariable String id) {
        CinemaHall hall = hallService.getHallById(id);
        if (hall == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hall);
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<Seat>> getSeatsByHall(@PathVariable String id) {
        return ResponseEntity.ok(hallService.getSeatsByHall(id));
    }

    @PostMapping
    public ResponseEntity<?> createHall(@Valid @RequestBody CinemaHall hall) {
        try {
            CinemaHall created = hallService.createHall(hall);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateHallStatus(@PathVariable String id, @RequestParam CinemaHallStatus status) {
        hallService.updateHallStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/info")
    public ResponseEntity<?> updateHallInfo(@PathVariable String id,
                                            @RequestParam String name,
                                            @RequestParam String hallType) {
        try {
            hallService.updateHallInfo(id, name, hallType);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHall(@PathVariable String id) {
        try {
            hallService.deleteHall(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}

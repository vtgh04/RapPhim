package rapphim.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rapphim.model.CinemaHall;
import rapphim.model.Seat;
import rapphim.model.enums.CinemaHallStatus;
import rapphim.service.HallService;

import java.util.List;

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

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateHallStatus(@PathVariable String id, @RequestParam CinemaHallStatus status) {
        hallService.updateHallStatus(id, status);
        return ResponseEntity.ok().build();
    }
}

package rapphim.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rapphim.service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/revenue-by-day")
    public ResponseEntity<List<Object[]>> getRevenueByDay() {
        return ResponseEntity.ok(dashboardService.getRevenueByDay());
    }

    @GetMapping("/top-movies")
    public ResponseEntity<List<Object[]>> getTopMovies() {
        return ResponseEntity.ok(dashboardService.getTopMovies());
    }
}

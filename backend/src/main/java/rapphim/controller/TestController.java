package rapphim.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rapphim.repository.EmployeeRepository;
import rapphim.repository.MovieRepository;
import rapphim.repository.ShowtimeRepository;
import rapphim.model.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Public test endpoint to verify REST API and Database connectivity/seeding.
 */
@RestController
@RequestMapping("/api/public")
public class TestController {

    private final EmployeeRepository employeeRepository;
    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public TestController(EmployeeRepository employeeRepository, 
                          MovieRepository movieRepository, 
                          ShowtimeRepository showtimeRepository) {
        this.employeeRepository = employeeRepository;
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Chào mừng đến với API hệ thống RapPhim!");
        response.put("version", "1.0.0-Spring-Boot");
        return response;
    }

    @GetMapping("/headers")
    public Map<String, String> getHeaders(jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> headersMap = new HashMap<>();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = request.getHeader(key);
                headersMap.put(key, value);
            }
        }
        return headersMap;
    }

    @GetMapping("/test-db")
    public Map<String, Object> testDb() {
        Map<String, Object> response = new HashMap<>();
        try {
            long employeeCount = employeeRepository.count();
            long movieCount = movieRepository.count();
            long showtimeCount = showtimeRepository.count();
            
            response.put("status", "connected");
            response.put("employeeCount", employeeCount);
            response.put("movieCount", movieCount);
            response.put("showtimeCount", showtimeCount);
            
            List<String> usernames = employeeRepository.findAll().stream()
                    .map(Employee::getUsername)
                    .collect(Collectors.toList());
            response.put("usernames", usernames);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }
}

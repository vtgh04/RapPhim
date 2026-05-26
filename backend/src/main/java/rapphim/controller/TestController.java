package rapphim.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Public test endpoint to verify REST API is running.
 */
@RestController
@RequestMapping("/api/public")
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Chào mừng đến với API hệ thống RapPhim!");
        response.put("version", "1.0.0-Spring-Boot");
        return response;
    }
}

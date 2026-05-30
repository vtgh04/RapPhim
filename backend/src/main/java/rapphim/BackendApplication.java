package rapphim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot REST API Entry Point for RapPhim Project.
 * This runs the server on port 5001 as specified in application.properties.
 */
@SpringBootApplication
@EnableScheduling
public class BackendApplication {

    public static void main(String[] args) {
        // Set headless property to false if Swing/AWT integrations are used,
        // but for pure REST backend we run in headless mode.
        System.setProperty("java.awt.headless", "true");
        SpringApplication.run(BackendApplication.class, args);
    }
}

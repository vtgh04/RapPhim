package rapphim.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Runs on application startup to ensure the 'reviews' table exists.
 * Uses a safe IF OBJECT_ID check so it never fails on re-starts.
 */
@Component
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void initTables() {
        String createReviews = """
            IF OBJECT_ID('dbo.reviews', 'U') IS NULL
            CREATE TABLE dbo.reviews (
                review_id    NVARCHAR(50)   NOT NULL PRIMARY KEY,
                movie_id     NVARCHAR(50)   NOT NULL,
                user_id      NVARCHAR(100)  NOT NULL,
                rating       INT            NOT NULL CHECK (rating BETWEEN 1 AND 5),
                comment      NVARCHAR(1000) NULL,
                created_at   DATETIME2      NOT NULL DEFAULT SYSDATETIME()
            )
            """;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createReviews);
            log.info("DatabaseInitializer: 'reviews' table ensured.");
        } catch (Exception e) {
            log.error("DatabaseInitializer: Failed to create tables", e);
        }
    }
}

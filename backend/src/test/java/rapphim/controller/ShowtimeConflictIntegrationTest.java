package rapphim.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import rapphim.model.Showtime;
import rapphim.model.enums.ShowtimeStatus;
import rapphim.repository.SeatRepository;
import rapphim.repository.ShowSeatRepository;
import rapphim.repository.ShowtimeRepository;
import rapphim.service.ShowtimeService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test cho ShowtimeService.
 * Sử dụng @DataJpaTest để load chỉ lớp JPA/Repository (không load SecurityConfig, WebConfig...)
 * và H2 in-memory database tự động tạo schema từ các Entity classes.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(ShowtimeService.class)
public class ShowtimeConflictIntegrationTest {

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @BeforeEach
    public void setup() {
        showSeatRepository.deleteAll();
        showtimeRepository.deleteAll();
    }

    @Test
    public void testAddShowtime_Success() {
        // Arrange
        Showtime showtime = new Showtime();
        showtime.setShowtimeId("SHW101");
        showtime.setMovieId("MOV001");
        showtime.setHallId("HALL01");
        showtime.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
        showtime.setEndTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0));
        showtime.setBasePrice(80000.0);
        showtime.setStatus(ShowtimeStatus.SCHEDULED);

        // Act - gọi service trực tiếp (không qua HTTP)
        Showtime saved = showtimeService.addShowtime(showtime);

        // Assert
        assertNotNull(saved);
        assertEquals("SHW101", saved.getShowtimeId());
        assertTrue(showtimeRepository.existsById("SHW101"));
    }

    @Test
    public void testAddShowtime_Conflict_Overlap_ThrowsException() {
        // Arrange: Lưu suất chiếu đầu tiên trực tiếp vào DB (14:00 - 16:00)
        LocalDateTime baseTime = LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);

        Showtime showtime1 = new Showtime("SHW201", "MOV001", "HALL01",
                baseTime, baseTime.plusHours(2), 80000.0, ShowtimeStatus.SCHEDULED);
        showtimeRepository.save(showtime1);

        // Tạo suất chiếu thứ 2 chồng chéo thời gian (15:00 - 17:00)
        Showtime showtime2 = new Showtime("SHW202", "MOV002", "HALL01",
                baseTime.plusHours(1), baseTime.plusHours(3), 90000.0, ShowtimeStatus.SCHEDULED);

        // Act & Assert - phải ném ngoại lệ vì overlap
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            showtimeService.addShowtime(showtime2);
        });

        assertTrue(ex.getMessage().contains("Phòng chiếu đã có suất chiếu trong khung giờ này."));
        // Chắc chắn suất chiếu xung đột KHÔNG được lưu vào DB
        assertFalse(showtimeRepository.existsById("SHW202"));
    }

    @Test
    public void testHasOverlap_NoConflict_DifferentHall() {
        // Arrange: Lưu suất chiếu ở HALL01
        LocalDateTime baseTime = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Showtime showtime1 = new Showtime("SHW301", "MOV001", "HALL01",
                baseTime, baseTime.plusHours(2), 80000.0, ShowtimeStatus.SCHEDULED);
        showtimeRepository.save(showtime1);

        // Act: Kiểm tra overlap ở HALL02 (phòng khác)
        boolean hasOverlap = showtimeService.hasOverlap("HALL02", baseTime, baseTime.plusHours(2));

        // Assert: Không conflict vì khác phòng
        assertFalse(hasOverlap);
    }
}

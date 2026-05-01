-- ============================================================
--  Script : 12_create_show_seats.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo bảng show_seats (trạng thái ghế trong từng suất chiếu)
--
--  Nghiệp vụ:
--    - Khi tạo Showtime mới, ứng dụng tự động sinh bản ghi show_seat
--      cho toàn bộ seats thuộc hall đó (xử lý ở tầng Service/Java).
--    - held_until: hạn giữ chỗ — cần job giải phóng ghế hết hạn
-- ============================================================

USE RapPhim;
GO

IF OBJECT_ID(N'dbo.show_seats', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.show_seats;
    PRINT N'[OK] Bảng dbo.show_seats cũ đã bị xoá.';
END
GO

CREATE TABLE dbo.show_seats (
    show_seat_id  VARCHAR(20)   NOT NULL,
    showtime_id   VARCHAR(20)   NOT NULL,
    seat_id       VARCHAR(20)   NOT NULL,
    status        VARCHAR(20)   NOT NULL CONSTRAINT df_show_seats_status DEFAULT 'AVAILABLE',
    held_until    DATETIME      NULL,

    CONSTRAINT pk_show_seats            PRIMARY KEY (show_seat_id),
    CONSTRAINT fk_show_seats_showtime   FOREIGN KEY (showtime_id) REFERENCES dbo.showtimes(showtime_id),
    CONSTRAINT fk_show_seats_seat       FOREIGN KEY (seat_id)     REFERENCES dbo.seats(seat_id),
    CONSTRAINT uq_show_seats_pair       UNIQUE (showtime_id, seat_id),
    CONSTRAINT chk_show_seats_status    CHECK (status IN ('AVAILABLE', 'HELD', 'BOOKED'))
);
GO

CREATE INDEX idx_show_seats_showtime ON dbo.show_seats (showtime_id);
CREATE INDEX idx_show_seats_status   ON dbo.show_seats (status);
CREATE INDEX idx_show_seats_held     ON dbo.show_seats (held_until) WHERE held_until IS NOT NULL;
GO

PRINT N'[OK] Bảng dbo.show_seats đã được tạo thành công.';
GO

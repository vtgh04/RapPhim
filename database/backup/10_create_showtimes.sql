
--  Script : 10_create_showtimes.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo bảng showtimes (suất chiếu)
-- ============================================================

USE RapPhim;
GO

IF OBJECT_ID(N'dbo.showtimes', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.showtimes;
    PRINT N'[OK] Bảng dbo.showtimes cũ đã bị xoá.';
END
GO

CREATE TABLE dbo.showtimes (
    showtime_id  VARCHAR(20)    NOT NULL,
    movie_id     VARCHAR(20)    NOT NULL,
    hall_id      VARCHAR(20)    NOT NULL,
    start_time   DATETIME       NOT NULL,
    end_time     DATETIME       NOT NULL,
    base_price   DECIMAL(12,2)  NOT NULL,
    status       VARCHAR(20)    NOT NULL CONSTRAINT df_showtimes_status DEFAULT 'SCHEDULED',

    CONSTRAINT pk_showtimes           PRIMARY KEY (showtime_id),
    CONSTRAINT fk_showtimes_movie     FOREIGN KEY (movie_id) REFERENCES dbo.movies(movie_id),
    CONSTRAINT fk_showtimes_hall      FOREIGN KEY (hall_id)  REFERENCES dbo.cinema_halls(hall_id),
    CONSTRAINT chk_showtimes_endtime  CHECK (end_time > start_time),
    CONSTRAINT chk_showtimes_price    CHECK (base_price >= 0),
    CONSTRAINT chk_showtimes_status   CHECK (status IN ('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED'))
);
GO

CREATE INDEX idx_showtimes_movie_id  ON dbo.showtimes (movie_id);
CREATE INDEX idx_showtimes_hall_id   ON dbo.showtimes (hall_id);
CREATE INDEX idx_showtimes_start     ON dbo.showtimes (start_time);
CREATE INDEX idx_showtimes_status    ON dbo.showtimes (status);
GO

PRINT N'[OK] Bảng dbo.showtimes đã được tạo thành công.';
GO
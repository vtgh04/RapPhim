USE RapPhim;
GO

IF OBJECT_ID(N'dbo.seats', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.seats;
END
GO

CREATE TABLE seats (
    seat_id VARCHAR(20) PRIMARY KEY,
    hall_id VARCHAR(20) NOT NULL,
    row_char CHAR(1) NOT NULL,
    col_number INT NOT NULL,
    seat_type VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    seat_price FLOAT NOT NULL,
    is_broken BIT DEFAULT 0,
    CONSTRAINT fk_seat_hall FOREIGN KEY (hall_id) REFERENCES cinema_halls(hall_id),
    CONSTRAINT uq_seat_hall_row_col UNIQUE (hall_id, row_char, col_number)
);
GO

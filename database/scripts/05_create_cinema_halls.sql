USE RapPhim;
GO

CREATE TABLE cinema_halls (
    hall_id VARCHAR(10) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE,
    hall_type VARCHAR(20),
    total_rows INT NOT NULL CHECK (total_rows > 0),
    total_cols INT NOT NULL CHECK (total_cols > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

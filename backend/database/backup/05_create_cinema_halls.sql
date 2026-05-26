-- ============================================================
--  Script : 05_create_cinema_halls.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo bảng cinema_halls với schema chuẩn
-- ============================================================

USE RapPhim;
GO

IF OBJECT_ID(N'dbo.cinema_halls', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.cinema_halls;
    PRINT N'[OK] Bảng dbo.cinema_halls cũ đã bị xoá.';
END
GO

CREATE TABLE dbo.cinema_halls (
    hall_id    VARCHAR(20)   NOT NULL,
    name       NVARCHAR(100) NOT NULL,
    hall_type  VARCHAR(20)   NULL,
    total_rows INT           NOT NULL,
    total_cols INT           NOT NULL,
    status     VARCHAR(20)   NOT NULL CONSTRAINT df_cinema_halls_status DEFAULT 'ACTIVE',

    CONSTRAINT pk_cinema_halls        PRIMARY KEY (hall_id),
    CONSTRAINT uq_cinema_halls_name   UNIQUE (name),
    CONSTRAINT chk_cinema_halls_rows  CHECK (total_rows > 0),
    CONSTRAINT chk_cinema_halls_cols  CHECK (total_cols > 0),
    CONSTRAINT chk_cinema_halls_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
GO

CREATE INDEX idx_cinema_halls_status ON dbo.cinema_halls (status);
GO

PRINT N'[OK] Bảng dbo.cinema_halls đã được tạo thành công.';
GO
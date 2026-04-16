-- ============================================================
--  Script : 03_create_movies.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo bảng movies cho hệ thống
-- ============================================================

USE RapPhim;
GO

IF OBJECT_ID(N'dbo.movies', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.movies;
    PRINT N'[OK] Bảng dbo.movies cũ đã bị xoá.';
END
GO

CREATE TABLE dbo.movies (
    movie_id      VARCHAR(20)    NOT NULL,
    title         NVARCHAR(200)  NOT NULL,
    genre         NVARCHAR(100)  NOT NULL,
    duration_mins INT            NOT NULL,
    format_movie  VARCHAR(20)    NOT NULL,
    language      NVARCHAR(50)   NULL,
    release_date  DATE           NULL,
    status        VARCHAR(20)    NOT NULL CONSTRAINT df_movies_status DEFAULT 'ACTIVE',
    description   NVARCHAR(MAX)  NULL,
    poster_url    VARCHAR(255)   NULL,

    CONSTRAINT pk_movies PRIMARY KEY (movie_id),
    CONSTRAINT chk_movies_duration CHECK (duration_mins > 0),
    CONSTRAINT chk_movies_status   CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
GO

CREATE INDEX idx_movies_title ON dbo.movies (title);
CREATE INDEX idx_movies_status ON dbo.movies (status);
GO

PRINT N'[OK] Bảng dbo.movies đã được tạo thành công.';
GO

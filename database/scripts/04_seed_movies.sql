-- ============================================================
--  Script : 04_seed_movies.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Thêm dữ liệu mẫu vào bảng movies (Cập nhật Format type)
-- ============================================================

USE RapPhim;
GO

-- Xoá dữ liệu cũ nếu muốn chạy lại seed
DELETE FROM dbo.movies;
GO

-- Seed dữ liệu
-- Phim nước ngoài: 2D/3D/IMAX + Lồng tiếng / Phụ đề
-- Phim Việt Nam: 2D/3D + Lồng tiếng / Phụ đề
INSERT INTO dbo.movies (movie_id, title, genre, duration_mins, format_movie, language, release_date, status, description, poster_url) VALUES 
('MOV001', N'Mai', N'Drama, Romance', 131, '2D', N'2D Lồng tiếng', '2024-02-10', 'ACTIVE', N'Một bộ phim về cuộc đời của Mai...', NULL),
('MOV002', N'Đào, Phở và Piano', N'Action, History', 120, '2D', N'2D Phụ đề', '2024-02-10', 'ACTIVE', N'Phim lịch sử phản ánh cuộc chiến oai hùng...', NULL),
('MOV003', N'Dune: Part Two', N'Sci-Fi, Adventure', 166, 'IMAX', N'IMAX Phụ đề', '2024-03-01', 'ACTIVE', N'Paul Atreides unites with...', NULL),
('MOV004', N'Kung Fu Panda 4', N'Animation, Comedy', 94, '3D', N'3D Lồng tiếng', '2024-03-08', 'ACTIVE', N'Po is gearing up to become...', NULL),
('MOV005', N'Godzilla x Kong: The New Empire', N'Action, Sci-Fi', 115, 'IMAX', N'IMAX Lồng tiếng', '2024-03-29', 'ACTIVE', N'Hai titan khổng lồ đụng độ...', NULL),
('MOV006', N'Lật Mặt 7', N'Family, Drama', 125, '3D', N'3D Phụ đề', '2024-04-26', 'ACTIVE', N'Phim gia đình kịch tính của VN...', NULL),
('MOV007', N'Oppenheimer', N'Biography, Drama', 180, 'IMAX', N'IMAX Phụ đề', '2023-08-11', 'INACTIVE', N'Câu chuyện về cha đẻ bom nguyên tử...', NULL);
GO

PRINT N'[OK] Đã chèn dữ liệu mẫu cho bảng dbo.movies thành công.';
GO

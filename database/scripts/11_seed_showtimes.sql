--  Script : 11_seed_showtimes.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Dữ liệu mẫu cho bảng showtimes
--           endTime = startTime + duration + 15 phút dọn phòng
-- ============================================================

USE RapPhim;
GO

DELETE FROM dbo.showtimes;
GO

-- Phim MAI (131 phút) → end = start + 131 + 15 = +146 phút
-- Phim Inception (148 phút) → +163 phút
-- Phim Interstellar (169 phút) → +184 phút
-- Phim The Dark Knight (152 phút) → +167 phút
-- Phim Avatar (192 phút) → +207 phút
-- Phim Titanic (194 phút) → +209 phút

INSERT INTO dbo.showtimes (showtime_id, movie_id, hall_id, start_time, end_time, base_price, status) VALUES

-- HAL001 (Phòng 1 - 2D): Mai
('SHW001', 'MOV001', 'HAL001', '2026-05-01 09:00:00', '2026-05-01 11:26:00', 100000, 'SCHEDULED'),
('SHW002', 'MOV001', 'HAL001', '2026-05-01 13:00:00', '2026-05-01 15:26:00', 100000, 'SCHEDULED'),
('SHW003', 'MOV001', 'HAL001', '2026-05-01 18:00:00', '2026-05-01 20:26:00', 100000, 'SCHEDULED'),

-- HAL002 (Phòng 2 - 2D): Harry Potter
('SHW004', 'MOV005', 'HAL002', '2026-05-01 09:30:00', '2026-05-01 12:17:00', 90000, 'SCHEDULED'),
('SHW005', 'MOV005', 'HAL002', '2026-05-01 14:00:00', '2026-05-01 16:47:00', 90000, 'SCHEDULED'),
('SHW006', 'MOV005', 'HAL002', '2026-05-01 19:00:00', '2026-05-01 21:47:00', 90000, 'SCHEDULED'),

-- HAL003 (Phòng 3 - 3D): Jurassic Park
('SHW007', 'MOV007', 'HAL003', '2026-05-01 10:00:00', '2026-05-01 12:22:00', 110000, 'SCHEDULED'),
('SHW008', 'MOV007', 'HAL003', '2026-05-01 15:00:00', '2026-05-01 17:22:00', 110000, 'SCHEDULED'),
('SHW009', 'MOV007', 'HAL003', '2026-05-01 20:00:00', '2026-05-01 22:22:00', 110000, 'SCHEDULED'),

-- HAL004 (Phòng 4 - 3D): Titanic
('SHW010', 'MOV010', 'HAL004', '2026-05-01 09:00:00', '2026-05-01 12:29:00', 110000, 'SCHEDULED'),
('SHW011', 'MOV010', 'HAL004', '2026-05-01 14:00:00', '2026-05-01 17:29:00', 110000, 'SCHEDULED'),
('SHW012', 'MOV010', 'HAL004', '2026-05-01 19:30:00', '2026-05-01 22:59:00', 110000, 'SCHEDULED'),

GO
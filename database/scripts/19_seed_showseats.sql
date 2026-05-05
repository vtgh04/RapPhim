-- ============================================================
--  Script : 19_seed_showseats.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Dữ liệu mẫu cho bảng show_seats để có thể tạo vé 
--           trong 17_seed_invoices_tickets.sql
-- ============================================================

USE RapPhim;
GO

DELETE FROM dbo.show_seats;
GO

INSERT INTO dbo.show_seats (show_seat_id, showtime_id, seat_id, price, status, held_until) VALUES
-- Dành cho Invoice 1 (suất chiếu SHW001, vé 100k)
('SHW001_HAL001_A1', 'SHW001', 'HAL001_A1', 100000, 'BOOKED', NULL),
('SHW001_HAL001_A2', 'SHW001', 'HAL001_A2', 100000, 'BOOKED', NULL),

-- Dành cho Invoice 2 (suất chiếu SHW010, Phòng 4 3D, base_price 110k -> VIP 165k, REGULAR 110k)
('SHW010_HAL004_D4', 'SHW010', 'HAL004_D4', 165000, 'BOOKED', NULL),
('SHW010_HAL004_D5', 'SHW010', 'HAL004_D5', 165000, 'BOOKED', NULL),
('SHW010_HAL004_A1', 'SHW010', 'HAL004_A1', 110000, 'BOOKED', NULL),
('SHW010_HAL004_A2', 'SHW010', 'HAL004_A2', 110000, 'BOOKED', NULL);
GO

PRINT N'[OK] Đã seed 6 show_seats mẫu cho việc mua vé.';
GO

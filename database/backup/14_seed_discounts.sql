-- ============================================================
--  Script : 14_seed_discounts.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Dữ liệu mẫu cho bảng discounts
-- ============================================================

USE RapPhim;
GO

DELETE FROM dbo.discounts;
GO

INSERT INTO dbo.discounts (discount_id, discount_name, discount_type, discount_rate, valid_from, valid_to, min_ticket_quantity, is_active, description) VALUES
-- HOLIDAY: Ngày lễ 30/4 - 1/5
('DIS001', N'Giảm giá 30/4 - 1/5', 'HOLIDAY', 0.15,
 '2026-04-30', '2026-05-01', 1, 1,
 N'Giảm 15% nhân dịp lễ Giải phóng miền Nam và Quốc tế Lao động'),

-- HOLIDAY: Tết Nguyên Đán
('DIS002', N'Khuyến mãi Tết 2026', 'HOLIDAY', 0.20,
 '2026-01-25', '2026-02-05', 1, 0,
 N'Giảm 20% trong dịp Tết Nguyên Đán Bính Ngọ 2026'),

-- GROUP: Mua nhóm từ 4 vé
('DIS003', N'Vé nhóm (4 người)', 'GROUP', 0.10,
 '2026-01-01', '2026-12-31', 4, 1,
 N'Giảm 10% khi mua từ 4 vé trở lên trong cùng một hóa đơn'),

-- GROUP: Mua nhóm từ 10 vé
('DIS004', N'Vé nhóm lớn (10 người)', 'GROUP', 0.20,
 '2026-01-01', '2026-12-31', 10, 1,
 N'Giảm 20% khi mua từ 10 vé trở lên — dành cho đặt vé đoàn'),

-- SPECIAL: Sinh nhật rạp
('DIS005', N'Sinh nhật rạp phim', 'SPECIAL', 0.25,
 '2026-06-01', '2026-06-07', 1, 1,
 N'Giảm 25% kỷ niệm ngày thành lập rạp — áp dụng toàn bộ suất chiếu trong tuần'),

-- SPECIAL: Thứ Hai vui vẻ
('DIS006', N'Monday Happy Hour', 'SPECIAL', 0.10,
 '2026-01-01', '2026-12-31', 1, 1,
 N'Giảm 10% tất cả suất chiếu vào thứ Hai hàng tuần');
GO

PRINT N'[OK] Đã seed 6 chương trình giảm giá mẫu.';
GO

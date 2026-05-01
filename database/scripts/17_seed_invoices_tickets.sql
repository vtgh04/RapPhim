-- ============================================================
--  Script : 17_seed_invoices_tickets.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Dữ liệu mẫu cho invoices và tickets
--
--  Yêu cầu: Phải chạy sau 12_create_show_seats.sql và các seed
--           show_seats đã được sinh (bởi ứng dụng hoặc script riêng).
--
--  Script này seed 3 hóa đơn mẫu với dữ liệu show_seat giả định
--  (sử dụng show_seat_id từ SHW001 - HAL001 đã được seed ghế).
-- ============================================================

USE RapPhim;
GO

-- Xoá dữ liệu cũ theo thứ tự phụ thuộc FK
DELETE FROM dbo.tickets;
DELETE FROM dbo.invoices;
GO

-- ============================================================
-- Invoice 1: CONFIRMED — nhân viên EMP002, thanh toán CASH
-- Mua 2 vé suất SHW001 (MAI - Phòng 1)
-- ============================================================
INSERT INTO dbo.invoices (invoice_id, employee_id, created_at, total_amount, payment_method, status, note)
VALUES ('INV001', 'EMP002', '2026-04-27 10:00:00', 200000, 'CASH', 'CONFIRMED', NULL);

-- Ticket 1: ghế A1 (REGULAR, 100k, không giảm giá)
INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode, original_price, discount_amount, final_price, issued_at, status)
VALUES ('TKT001', 'INV001', 'SHW001_HAL001_A1', NULL, 'BC-TKT001-2026', 100000, 0, 100000, '2026-04-27 10:00:00', 'VALID');

-- Ticket 2: ghế A2 (REGULAR, 100k, không giảm giá)
INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode, original_price, discount_amount, final_price, issued_at, status)
VALUES ('TKT002', 'INV001', 'SHW001_HAL001_A2', NULL, 'BC-TKT002-2026', 100000, 0, 100000, '2026-04-27 10:00:00', 'VALID');

-- ============================================================
-- Invoice 2: CONFIRMED — nhân viên EMP002, thanh toán CARD
-- Mua 4 vé suất SHW013 (Inception - IMAX), áp dụng DIS003 (GROUP 10%)
-- ============================================================
INSERT INTO dbo.invoices (invoice_id, employee_id, created_at, total_amount, payment_method, status, note)
VALUES ('INV002', 'EMP002', '2026-04-27 14:30:00', 540000, 'CARD', 'CONFIRMED', N'Vé nhóm 4 người');

-- Ticket 3: VIP 150k×1.5=225k, giảm 10% → discount_amount=22500, final=202500
INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode, original_price, discount_amount, final_price, issued_at, status)
VALUES ('TKT003', 'INV002', 'SHW013_HAL005_D4', 'DIS003', 'BC-TKT003-2026', 225000, 22500, 202500, '2026-04-27 14:30:00', 'VALID');

-- Ticket 4: VIP 225k, giảm 10% → final=202500
INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode, original_price, discount_amount, final_price, issued_at, status)
VALUES ('TKT004', 'INV002', 'SHW013_HAL005_D5', 'DIS003', 'BC-TKT004-2026', 225000, 22500, 202500, '2026-04-27 14:30:00', 'VALID');

-- Ticket 5: REGULAR 150k, giảm 10% → final=135000
INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode, original_price, discount_amount, final_price, issued_at, status)
VALUES ('TKT005', 'INV002', 'SHW013_HAL005_A1', 'DIS003', 'BC-TKT005-2026', 150000, 15000, 135000, '2026-04-27 14:30:00', 'VALID');

-- Ticket 6: REGULAR 150k, giảm 10% → final=135000  (tổng INV002 = 202500×2 + 135000×2 = 675000 — ghi chú: cập nhật total)
INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode, original_price, discount_amount, final_price, issued_at, status)
VALUES ('TKT006', 'INV002', 'SHW013_HAL005_A2', 'DIS003', 'BC-TKT006-2026', 150000, 15000, 135000, '2026-04-27 14:30:00', 'VALID');

-- Cập nhật lại total_amount cho INV002
UPDATE dbo.invoices
SET total_amount = (SELECT SUM(final_price) FROM dbo.tickets WHERE invoice_id = 'INV002' AND status != 'CANCELLED')
WHERE invoice_id = 'INV002';

-- ============================================================
-- Invoice 3: PENDING — nhân viên EMP004, chưa thanh toán
-- ============================================================
INSERT INTO dbo.invoices (invoice_id, employee_id, created_at, total_amount, payment_method, status, note)
VALUES ('INV003', 'EMP004', '2026-04-27 16:00:00', 0, 'CASH', 'PENDING', NULL);
GO

PRINT N'[OK] Đã seed 3 hóa đơn và 6 vé mẫu.';
GO

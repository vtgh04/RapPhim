-- ============================================================
--  Script : 20_seed_shw017_inv004.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Thêm suất chiếu SHW017, hóa đơn INV005 với 4 vé
--
--  Chi tiết:
--    - Phim   : Avatar: The Way of Water (MOV003, 192 phút)
--    - Phòng  : HAL002 (Phòng 2 - 2D)
--    - Suất   : SHW017 — 17:30 → 21:57 ngày 04/05/2026
--    - Invoice: INV005 — mua lúc 15:00 ngày 04/05/2026
--    - Ghế    : A1, A2, B1, B2 (REGULAR - 120k/ghế)
--    - Vé     : TKT007 → TKT010
-- ============================================================

USE RapPhim;
GO

-- ── Dọn dữ liệu cũ (chạy an toàn nhiều lần) ─────────────────
DELETE FROM dbo.tickets    WHERE ticket_id  IN ('TKT007','TKT008','TKT009','TKT010');
DELETE FROM dbo.invoices   WHERE invoice_id  = 'INV005';
DELETE FROM dbo.show_seats WHERE show_seat_id IN (
    'SHW017_HAL002_A1','SHW017_HAL002_A2',
    'SHW017_HAL002_B1','SHW017_HAL002_B2');
DELETE FROM dbo.showtimes  WHERE showtime_id = 'SHW017';
GO

-- ── 1. Suất chiếu SHW017 ─────────────────────────────────────
INSERT INTO dbo.showtimes (showtime_id, movie_id, hall_id, start_time, end_time, base_price, status)
VALUES ('SHW017', 'MOV003', 'HAL002',
        '2026-05-04 17:30:00', '2026-05-04 21:57:00',
        120000, 'SCHEDULED');
PRINT N'[OK] Đã thêm SHW017.';
GO

-- ── 2. Show_seats (4 ghế REGULAR 120k) ───────────────────────
INSERT INTO dbo.show_seats (show_seat_id, showtime_id, seat_id, price, status, held_until) VALUES
('SHW017_HAL002_A1', 'SHW017', 'HAL002_A1', 120000, 'BOOKED', NULL),
('SHW017_HAL002_A2', 'SHW017', 'HAL002_A2', 120000, 'BOOKED', NULL),
('SHW017_HAL002_B1', 'SHW017', 'HAL002_B1', 120000, 'BOOKED', NULL),
('SHW017_HAL002_B2', 'SHW017', 'HAL002_B2', 120000, 'BOOKED', NULL);
PRINT N'[OK] Đã thêm 4 show_seats cho SHW017.';
GO

-- ── 3. Hóa đơn INV005 ────────────────────────────────────────
INSERT INTO dbo.invoices (invoice_id, employee_id, created_at, total_amount, total_tickets, payment_method, status, note)
VALUES ('INV005', 'EMP002', '2026-05-04 15:00:00',
        480000, 4, 'CASH', 'CONFIRMED',
        N'Mua 4 vé Avatar: The Way of Water — SHW017 — Phòng 2');
PRINT N'[OK] Đã thêm INV005.';
GO

-- ── 4. Vé TKT007 → TKT010 ────────────────────────────────────
INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode,
                         original_price, discount_amount, final_price, issued_at, status) VALUES
('TKT007', 'INV005', 'SHW017_HAL002_A1', NULL, 'BC-TKT007-20260504', 120000, 0, 120000, '2026-05-04 15:00:00', 'VALID'),
('TKT008', 'INV005', 'SHW017_HAL002_A2', NULL, 'BC-TKT008-20260504', 120000, 0, 120000, '2026-05-04 15:00:00', 'VALID'),
('TKT009', 'INV005', 'SHW017_HAL002_B1', NULL, 'BC-TKT009-20260504', 120000, 0, 120000, '2026-05-04 15:00:00', 'VALID'),
('TKT010', 'INV005', 'SHW017_HAL002_B2', NULL, 'BC-TKT010-20260504', 120000, 0, 120000, '2026-05-04 15:00:00', 'VALID');
PRINT N'[OK] Đã thêm 4 vé TKT007 → TKT010.';
GO

-- ── Kiểm tra kết quả ─────────────────────────────────────────
SELECT
    t.ticket_id,
    t.invoice_id,
    ss.showtime_id,
    ss.seat_id,
    ss.status    AS seat_status,
    t.final_price,
    t.issued_at,
    t.status     AS ticket_status
FROM dbo.tickets t
JOIN dbo.show_seats ss ON ss.show_seat_id = t.show_seat_id
WHERE t.invoice_id = 'INV005'
ORDER BY t.ticket_id;
GO

PRINT N'[DONE] Script 20 hoàn tất.';
GO

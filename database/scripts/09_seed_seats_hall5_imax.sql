USE RapPhim;
GO

-- Xoá dữ liệu cũ nếu muốn chạy lại seed
DELETE FROM seats WHERE hall_id = 'HAL005';
GO

-- Seed dữ liệu ghế tự động bằng T-SQL cho HAL005 (Phòng IMAX 1, 9 hàng x 9 cột)
-- Khối VIP: Từ hàng D(4) đến F(6), cột từ 3 đến 7
DECLARE @hall_id VARCHAR(10) = 'HAL005';
DECLARE @total_rows INT = 9;
DECLARE @total_cols INT = 9;
DECLARE @r INT = 1;
DECLARE @c INT = 1;
DECLARE @row_char CHAR(1);
DECLARE @seat_type VARCHAR(20);
DECLARE @seat_price FLOAT;

WHILE @r <= @total_rows
BEGIN
    SET @row_char = CHAR(64 + @r);
    SET @c = 1;

    WHILE @c <= @total_cols
    BEGIN
        -- VIP = Hàng D(4) tới F(6) và Cột 3 tới 7
        IF @r >= 4 AND @r <= 6 AND @c >= 3 AND @c <= 7
        BEGIN
            SET @seat_type = 'VIP';
            SET @seat_price = 110000;
        END
        ELSE
        BEGIN
            SET @seat_type = 'REGULAR';
            SET @seat_price = 100000;
        END

        INSERT INTO seats (seat_id, hall_id, row_char, col_number, seat_type, seat_price)
        VALUES (@hall_id + '_' + @row_char + CAST(@c AS VARCHAR), @hall_id, @row_char, @c, @seat_type, @seat_price);

        SET @c = @c + 1;
    END
    SET @r = @r + 1;
END
GO

PRINT N'[OK] Đã seed ghế cho HAL005 (Phòng IMAX 1) thành công.';
GO

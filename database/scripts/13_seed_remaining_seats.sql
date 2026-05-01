USE RapPhim;
GO

-- Xoá dữ liệu cũ nếu muốn chạy lại seed
DELETE FROM seats WHERE hall_id IN ('HAL002', 'HAL003', 'HAL004', 'HAL006', 'HAL007', 'HAL008');
GO

-- ============================================================================
-- Seed dữ liệu ghế tự động cho các phòng 12x8 (HAL002, HAL003, HAL004, HAL007, HAL008)
-- ============================================================================
DECLARE @hall_id VARCHAR(20);
DECLARE @total_rows INT = 12;
DECLARE @total_cols INT = 8;
DECLARE @r INT;
DECLARE @c INT;
DECLARE @row_char CHAR(1);
DECLARE @seat_type VARCHAR(20);
DECLARE @seat_price FLOAT;

DECLARE hall_cursor CURSOR FOR 
SELECT hall_id FROM cinema_halls WHERE hall_id IN ('HAL002', 'HAL003', 'HAL004', 'HAL007', 'HAL008');

OPEN hall_cursor;
FETCH NEXT FROM hall_cursor INTO @hall_id;

WHILE @@FETCH_STATUS = 0
BEGIN
    SET @r = 1;
    WHILE @r <= @total_rows
    BEGIN
        SET @row_char = CHAR(64 + @r); 
        SET @c = 1;
        
        WHILE @c <= @total_cols
        BEGIN
            -- VIP = Hàng D(4) tới H(8) và Cột 3 tới 6
            IF @r >= 4 AND @r <= 8 AND @c >= 3 AND @c <= 6
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

    FETCH NEXT FROM hall_cursor INTO @hall_id;
END

CLOSE hall_cursor;
DEALLOCATE hall_cursor;
GO

-- ============================================================================
-- Seed dữ liệu ghế tự động cho HAL006 (Phòng IMAX 2, 9 hàng x 9 cột)
-- ============================================================================
DECLARE @hall_id VARCHAR(20) = 'HAL006';
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

PRINT N'[OK] Đã seed ghế cho các phòng còn lại (HAL002, HAL003, HAL004, HAL006, HAL007, HAL008) thành công.';
GO

    USE RapPhim;
    GO

    -- Xoá dữ liệu cũ nếu muốn chạy lại seed
    DELETE FROM seats WHERE hall_id = 'HAL001';
    GO

    -- Seed dữ liệu ghế tự động bằng T-SQL cho HAL001 (12 hàng x 8 cột)
    -- Khối VIP: Từ hàng D đến G (hàng 4 đến 7), cột từ 3 đến 7.
    DECLARE @hall_id VARCHAR(10) = 'HAL001';
    DECLARE @total_rows INT = 12;
    DECLARE @total_cols INT = 8;
    DECLARE @r INT = 1;
    DECLARE @c INT = 1;
    DECLARE @row_char CHAR(1);
    DECLARE @seat_type VARCHAR(20);
    DECLARE @seat_factor DECIMAL(4,2);

    WHILE @r <= @total_rows
    BEGIN
        -- Tính ký tự hàng: 1 -> A, 2 -> B... (Mã ASCII 64 + 1 = 65 là 'A')
        SET @row_char = CHAR(64 + @r); 
        SET @c = 1;
        
        WHILE @c <= @total_cols
        BEGIN
            -- VIP = Hàng D(4) tới H(8) và Cột 3 tới 6
            IF @r >= 4 AND @r <= 8 AND @c >= 3 AND @c <= 6
            BEGIN
                SET @seat_type = 'VIP';
                SET @seat_factor = 1.5;
            END
            ELSE
            BEGIN
                SET @seat_type = 'REGULAR';
                SET @seat_factor = 1.0;
            END

            INSERT INTO seats (seat_id, hall_id, row_char, col_number, seat_type, seat_factor)
            VALUES (@hall_id + '_' + @row_char + CAST(@c AS VARCHAR), @hall_id, @row_char, @c, @seat_type, @seat_factor);
            
            SET @c = @c + 1;
        END
        SET @r = @r + 1;
    END
    GO

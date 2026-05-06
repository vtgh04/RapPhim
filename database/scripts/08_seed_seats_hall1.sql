
DECLARE @r INT, @c INT, @row_char CHAR(1);
DECLARE @hall_type VARCHAR(10), @rows INT, @cols INT;
DECLARE @seat_type VARCHAR(20), @seat_factor DECIMAL(4,2);

DECLARE @Halls TABLE (hall_id VARCHAR(20), rows INT, cols INT, hall_type VARCHAR(10));
INSERT INTO @Halls VALUES 
('HAL001', 12, 8, '2D'), ('HAL002', 12, 8, '2D'), 
('HAL003', 12, 8, '3D'), ('HAL004', 12, 8, '3D'),
('HAL005', 9, 9, 'IMAX'), ('HAL006', 9, 9, 'IMAX'), 
('HAL007', 12, 8, '2D'), ('HAL008', 12, 8, '3D');

DECLARE @current_hall VARCHAR(20) = (SELECT MIN(hall_id) FROM @Halls);

WHILE @current_hall IS NOT NULL
BEGIN
    SELECT @rows = rows, @cols = cols, @hall_type = hall_type 
    FROM @Halls WHERE hall_id = @current_hall;
    
    SET @r = 1;
    WHILE @r <= @rows
    BEGIN
        SET @row_char = CHAR(64 + @r);
        SET @c = 1;
        WHILE @c <= @cols
        BEGIN
            -- Quy tắc VIP
            IF @hall_type = 'IMAX' AND @r BETWEEN 4 AND 6 AND @c BETWEEN 3 AND 7
                SELECT @seat_type = 'VIP', @seat_factor = 1.5;
            ELSE IF @hall_type IN ('2D', '3D') AND @r BETWEEN 4 AND 8 AND @c BETWEEN 3 AND 6
                SELECT @seat_type = 'VIP', @seat_factor = 1.5;
            ELSE
                SELECT @seat_type = 'REGULAR', @seat_factor = 1.0;

            INSERT INTO dbo.seats (seat_id, hall_id, row_char, col_number, seat_type, seat_factor)
            VALUES (@current_hall + '_' + @row_char + CAST(@c AS VARCHAR), @current_hall, @row_char, @c, @seat_type, @seat_factor);

            SET @c = @c + 1;
        END
        SET @r = @r + 1;
    END

    SET @current_hall = (SELECT MIN(hall_id) FROM @Halls WHERE hall_id > @current_hall);
END
GO

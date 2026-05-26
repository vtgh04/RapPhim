    USE RapPhim;
    GO
    -- Xoá toàn bộ dữ liệu cũ theo thứ tự FK (an toàn khi chạy lại)
    DELETE FROM dbo.tickets;
    DELETE FROM dbo.invoices;
    DELETE FROM dbo.show_seats;
    DELETE FROM dbo.showtimes;
    DELETE FROM dbo.seats;
    DELETE FROM dbo.cinema_halls;
    DELETE FROM dbo.discounts;
    DELETE FROM dbo.movies;
    DELETE FROM dbo.employees;
    GO
    INSERT INTO dbo.employees (
        employee_id, full_name, username, password, role, status, phone, email
    )
    SELECT *
    FROM (VALUES
    ('EMP001', N'Nguyen Van An', 'manager01', '123', 'MANAGER', 'ACTIVE', '0901234567', 'manager01@rapphim.com'),
    ('EMP002', N'Tran Thi Binh', 'staff01', '123', 'STAFF', 'ACTIVE', '0912345678', 'staff01@rapphim.com'),
    ('EMP003', N'Le Van Cuong', 'staff_off', 'Staff@123', 'STAFF', 'RETIRED', '0923456789', 'staff_off@rapphim.com'),

    ('EMP004', N'Pham Minh Duc', 'staff02', '123', 'STAFF', 'ACTIVE', '0931111111', 'staff02@rapphim.com'),
    ('EMP005', N'Hoang Thi Lan', 'staff03', '123', 'STAFF', 'ACTIVE', '0932222222', 'staff03@rapphim.com'),
    ('EMP006', N'Nguyen Quang Huy', 'staff04', '123', 'STAFF', 'ACTIVE', '0933333333', 'staff04@rapphim.com'),
    ('EMP007', N'Le Thi Mai', 'staff05', '123', 'STAFF', 'ACTIVE', '0934444444', 'staff05@rapphim.com'),
    ('EMP008', N'Tran Van Nam', 'staff06', '123', 'STAFF', 'ACTIVE', '0935555555', 'staff06@rapphim.com'),
    ('EMP009', N'Vo Thi Hoa', 'staff07', '123', 'STAFF', 'ACTIVE', '0936666666', 'staff07@rapphim.com'),
    ('EMP010', N'Bui Van Tai', 'staff08', '123', 'STAFF', 'ACTIVE', '0937777777', 'staff08@rapphim.com'),

    ('EMP011', N'Do Thi Hang', 'staff09', '123', 'STAFF', 'ACTIVE', '0941111111', 'staff09@rapphim.com'),
    ('EMP012', N'Nguyen Van Phuc', 'staff10', '123', 'STAFF', 'ACTIVE', '0942222222', 'staff10@rapphim.com'),
    ('EMP013', N'Tran Thi Yen', 'staff11', '123', 'STAFF', 'ACTIVE', '0943333333', 'staff11@rapphim.com'),
    ('EMP014', N'Pham Van Khoa', 'staff12', '123', 'STAFF', 'ACTIVE', '0944444444', 'staff12@rapphim.com'),
    ('EMP015', N'Hoang Minh Chau', 'staff13', '123', 'STAFF', 'ACTIVE', '0945555555', 'staff13@rapphim.com'),
    ('EMP016', N'Le Van Dat', 'staff14', '123', 'STAFF', 'ACTIVE', '0946666666', 'staff14@rapphim.com'),
    ('EMP017', N'Nguyen Thi Uyen', 'staff15', '123', 'STAFF', 'ACTIVE', '0947777777', 'staff15@rapphim.com'),
    ('EMP018', N'Tran Quoc Bao', 'staff16', '123', 'STAFF', 'ACTIVE', '0951111111', 'staff16@rapphim.com'),
    ('EMP019', N'Vo Thanh Tung', 'staff17', '123', 'STAFF', 'ACTIVE', '0952222222', 'staff17@rapphim.com'),
    ('EMP020', N'Bui Thi Ngan', 'staff18', '123', 'STAFF', 'ACTIVE', '0953333333', 'staff18@rapphim.com'),

    ('EMP021', N'Do Van Hiep', 'staff19', '123', 'STAFF', 'ACTIVE', '0954444444', 'staff19@rapphim.com'),
    ('EMP022', N'Nguyen Thanh Long', 'staff20', '123', 'STAFF', 'ACTIVE', '0955555555', 'staff20@rapphim.com'),
    ('EMP023', N'Tran Thi Nga', 'staff21', '123', 'STAFF', 'ACTIVE', '0956666666', 'staff21@rapphim.com'),
    ('EMP024', N'Pham Thi Huong', 'staff22', '123', 'STAFF', 'ACTIVE', '0957777777', 'staff22@rapphim.com'),
    ('EMP025', N'Le Van Son', 'staff23', '123', 'STAFF', 'ACTIVE', '0961111111', 'staff23@rapphim.com'),
    ('EMP026', N'Hoang Van Duc', 'staff24', '123', 'STAFF', 'ACTIVE', '0962222222', 'staff24@rapphim.com'),
    ('EMP027', N'Nguyen Thi Thao', 'staff25', '123', 'STAFF', 'ACTIVE', '0963333333', 'staff25@rapphim.com'),
    ('EMP028', N'Tran Van Hung', 'staff26', '123', 'STAFF', 'ACTIVE', '0964444444', 'staff26@rapphim.com'),
    ('EMP029', N'Vo Thi Linh', 'staff27', '123', 'STAFF', 'ACTIVE', '0965555555', 'staff27@rapphim.com'),
    ('EMP030', N'Bui Quang Vinh', 'staff28', '123', 'STAFF', 'ACTIVE', '0966666666', 'staff28@rapphim.com')
    ) AS source (employee_id, full_name, username, password, role, status, phone, email)
    WHERE NOT EXISTS (
        SELECT 1 FROM dbo.employees e
        WHERE e.employee_id = source.employee_id
    );


    -- Movies
    GO
    INSERT INTO dbo.movies (movie_id, title, genre, duration_mins, format_movie, rating, language, release_date, status, description, poster_url) VALUES
    ('MOV001', N'Mai', N'Drama, Romance', 131, '2D', 'T16',
    N'2D Lồng tiếng', '2024-02-10', 'ACTIVE',
    N'Câu chuyện cảm động về người phụ nữ tên Mai, vượt qua nghịch cảnh để tìm lại chính mình và hạnh phúc đích thực.',
    'images/movies/Mai.jpg'),
    -- Inception.jpg
    ('MOV002', N'Inception', N'Action, Sci-Fi, Thriller', 148, 'IMAX', 'T13',
    N'IMAX Phụ đề', '2010-07-16', 'ACTIVE',
    N'Dom Cobb là tên trộm lành nghề nhất trong nghệ thuật chiết xuất: đánh cắp bí mật giá trị nhất từ sâu trong tiềm thức ngay khi con người đang mơ.',
    'images/movies/Inception.jpg'),
    -- avatar_poster.jpg
    ('MOV003', N'Avatar: The Way of Water', N'Action, Adventure, Sci-Fi', 192, 'IMAX', 'T13',
    N'IMAX Phụ đề', '2022-12-16', 'ACTIVE',
    N'Jake Sully cùng gia đình Na''vi phải đối mặt với hiểm nguy từ lực lượng xâm lược trở lại, buộc họ trốn chạy và khám phá đại dương huyền bí của Pandora.',
    'images/movies/avatar_poster.jpg'),
    -- bzvn-poster-thoat-khoi-tan-the-...2026.jpg
    ('MOV004', N'Project Hail Mary', N'Sci-Fi, Adventure', 140, 'IMAX', 'P',
    N'IMAX Phụ đề', '2026-03-20', 'ACTIVE',
    N'Một nhà khoa học tỉnh dậy một mình trên phi thuyền giữa vũ trụ, mất đi ký ức, nhưng anh ta chính là hy vọng duy nhất để cứu Trái Đất.',
    'images/movies/bzvn-poster-thoat-khoi-tan-the-project-hail-mary-2026.jpg'),
    -- harry_potter_poster.jpg
    ('MOV005', N'Harry Potter and the Sorcerer''s Stone', N'Fantasy, Adventure, Family', 152, '2D', 'P',
    N'2D Lồng tiếng', '2001-11-16', 'ACTIVE',
    N'Cậu bé 11 tuổi Harry Potter phát hiện ra mình là một phù thủy và được nhận vào trường Hogwarts, nơi anh bắt đầu hành trình huyền diệu đầu tiên.',
    'images/movies/harry_potter_poster.jpg'),
    -- interstellar-movie-poster-2014-1010771210.jpg
    ('MOV006', N'Interstellar', N'Sci-Fi, Adventure, Drama', 169, 'IMAX', 'P',
    N'IMAX Phụ đề', '2014-11-07', 'ACTIVE',
    N'Trong tương lai, Trái Đất sắp không còn thích hợp để sống. Một nhóm phi hành gia lên đường qua lỗ sâu đục để tìm kiếm hành tinh mới cho nhân loại.',
    'images/movies/interstellar-movie-poster-2014-1010771210.jpg'),
    -- jurassic_park_poster.jpg
    ('MOV007', N'Jurassic Park', N'Adventure, Sci-Fi, Thriller', 127, '3D', 'T13',
    N'3D Lồng tiếng', '1993-06-11', 'ACTIVE',
    N'Một nhà khoa học tỷ phú mở công viên chủ đề với những con khủng long được hồi sinh từ ADN cổ đại, nhưng mọi thứ nhanh chóng vượt ngoài tầm kiểm soát.',
    'images/movies/jurassic_park_poster.jpg'),
    -- the_matrix_poster.jpg
    ('MOV008', N'The Matrix', N'Action, Sci-Fi', 136, 'IMAX', 'T16',
    N'IMAX Phụ đề', '1999-03-31', 'ACTIVE',
    N'Neo, một hacker máy tính, khám phá ra rằng thực tại mà anh đang sống chỉ là một thế giới ảo được kiểm soát bởi những cỗ máy thông minh.',
    'images/movies/the_matrix_poster.jpg'),
    -- thedarknight.jpg
    ('MOV009', N'The Dark Knight', N'Action, Crime, Drama', 152, 'IMAX', 'T16',
    N'IMAX Phụ đề', '2008-07-18', 'ACTIVE',
    N'Batman đối mặt với kẻ thù hỗn loạn nhất từng thấy: The Joker, tên tội phạm tâm lý muốn xem thế giới bùng cháy và thách thức giới hạn đạo đức của người hùng.',
    'images/movies/thedarknight.jpg'),
    -- titanic_poster.jpg
    ('MOV010', N'Titanic', N'Romance, Drama, History', 194, '3D', 'T13',
    N'3D Lồng tiếng', '1997-12-19', 'ACTIVE',
    N'Câu chuyện tình yêu bi thảm giữa Jack và Rose trên con tàu Titanic định mệnh, biểu tượng của tình yêu vượt qua ranh giới giai cấp.',
    'images/movies/titanic_poster.jpg'),
    -- 7DUjpg.jpg  (Avengers: Endgame placeholder)
    ('MOV011', N'Avengers: Endgame', N'Action, Adventure, Sci-Fi', 181, 'IMAX', 'T13',
    N'IMAX Lồng tiếng', '2019-04-26', 'INACTIVE',
    N'Sau thảm kịch của Infinity War, các Avengers sống sót tập hợp lần cuối để đảo ngược hành động của Thanos và khôi phục lại vũ trụ.',
    'images/movies/7DUjpg.jpg');
    GO

    INSERT INTO dbo.cinema_halls (hall_id, name, hall_type, total_rows, total_cols, status) VALUES
    ('HAL001', N'Phòng 1', '2D', 12, 8, 'ACTIVE'),
    ('HAL002', N'Phòng 2', '2D', 12, 8, 'ACTIVE'),
    ('HAL003', N'Phòng 3', '3D', 12, 8, 'ACTIVE'),
    ('HAL004', N'Phòng 4', '3D', 12, 8, 'ACTIVE'),
    ('HAL005', N'Phòng IMAX 1', 'IMAX', 9, 9, 'ACTIVE'),
    ('HAL006', N'Phòng IMAX 2', 'IMAX', 9, 9, 'ACTIVE'),
    ('HAL007', N'Phòng 5', '2D', 12, 8, 'INACTIVE'),
    ('HAL008', N'Phòng 6', '3D', 12, 8, 'INACTIVE');
    GO

    INSERT INTO dbo.showtimes (showtime_id, movie_id, hall_id, start_time, end_time, base_price, status) VALUES

    ('SHW001', 'MOV001', 'HAL001', '2026-05-08 09:00:00', '2026-05-08 11:26:00', 120000, 'SCHEDULED'),
    ('SHW002', 'MOV001', 'HAL001', '2026-05-08 13:00:00', '2026-05-08 15:26:00', 120000, 'SCHEDULED'),
    ('SHW003', 'MOV001', 'HAL001', '2026-05-08 18:00:00', '2026-05-08 20:26:00', 120000, 'SCHEDULED'),

    ('SHW004', 'MOV005', 'HAL002', '2026-05-08 09:30:00', '2026-05-08 12:17:00', 120000, 'SCHEDULED'),
    ('SHW005', 'MOV005', 'HAL002', '2026-05-08 14:00:00', '2026-05-08 16:47:00', 120000, 'SCHEDULED'),
    ('SHW006', 'MOV005', 'HAL002', '2026-05-08 19:00:00', '2026-05-08 21:47:00', 120000, 'SCHEDULED'),

    ('SHW007', 'MOV007', 'HAL003', '2026-05-08 10:00:00', '2026-05-08 12:22:00', 120000, 'SCHEDULED'),
    ('SHW008', 'MOV007', 'HAL003', '2026-05-08 15:00:00', '2026-05-08 17:22:00', 120000, 'SCHEDULED'),
    ('SHW009', 'MOV007', 'HAL003', '2026-05-08 20:00:00', '2026-05-08 22:22:00', 120000, 'SCHEDULED'),

    ('SHW010', 'MOV010', 'HAL004', '2026-05-08 09:00:00', '2026-05-08 12:29:00', 120000, 'SCHEDULED'),
    ('SHW011', 'MOV010', 'HAL004', '2026-05-08 14:00:00', '2026-05-08 17:29:00', 120000, 'SCHEDULED'),
    ('SHW012', 'MOV010', 'HAL004', '2026-05-08 19:30:00', '2026-05-08 22:59:00', 120000, 'SCHEDULED');

    GO


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


  
    INSERT INTO dbo.show_seats (show_seat_id, showtime_id, seat_id, price, status)
    SELECT 
        st.showtime_id + '_' + s.seat_id,
        st.showtime_id,
        s.seat_id,
        st.base_price * s.seat_factor,
        'AVAILABLE'
    FROM dbo.showtimes st
    JOIN dbo.seats s ON st.hall_id = s.hall_id;
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

    INSERT INTO dbo.invoices (invoice_id, employee_id, created_at, total_amount, total_tickets, payment_method, status, note) VALUES
    ('INV001', 'EMP002', '2026-05-03 09:15:00',  360000, 3, 'CASH',     'CONFIRMED', NULL),
    ('INV002', 'EMP004', '2026-05-03 09:42:00',  240000, 2, 'CARD',     'CONFIRMED', NULL),
    ('INV003', 'EMP005', '2026-05-03 10:05:00',  360000, 3, 'TRANSFER', 'CONFIRMED', NULL),
    ('INV004', 'EMP006', '2026-05-03 10:30:00',  240000, 2, 'CASH',     'CONFIRMED', NULL),
    ('INV005', 'EMP002', '2026-05-03 11:15:00',  120000, 1, 'CARD',     'CONFIRMED', NULL),
    ('INV006', 'EMP007', '2026-05-03 12:00:00',  360000, 3, 'CASH',     'CONFIRMED', NULL),
    ('INV007', 'EMP008', '2026-05-03 12:45:00',  240000, 2, 'TRANSFER', 'CONFIRMED', NULL),
    ('INV008', 'EMP004', '2026-05-03 13:20:00',  480000, 4, 'CARD',     'CONFIRMED', NULL),
    ('INV009', 'EMP009', '2026-05-03 14:00:00',  360000, 3, 'CASH',     'CONFIRMED', NULL),
    ('INV010', 'EMP002', '2026-05-03 14:35:00',  240000, 2, 'TRANSFER', 'CONFIRMED', NULL),
    ('INV011', 'EMP010', '2026-05-03 15:10:00',  240000, 2, 'CASH',     'CONFIRMED', NULL),
    ('INV012', 'EMP005', '2026-05-03 15:50:00',  360000, 3, 'CARD',     'CONFIRMED', NULL),
    ('INV013', 'EMP011', '2026-05-03 16:20:00',  120000, 1, 'TRANSFER', 'CONFIRMED', NULL),
    ('INV014', 'EMP006', '2026-05-03 17:05:00',  240000, 2, 'CASH',     'CONFIRMED', NULL),
    ('INV015', 'EMP002', '2026-05-03 17:40:00',  360000, 3, 'CARD',     'CONFIRMED', NULL),
    ('INV016', 'EMP012', '2026-05-03 18:15:00',  480000, 4, 'TRANSFER', 'CONFIRMED', NULL),
    ('INV017', 'EMP007', '2026-05-03 18:50:00',  240000, 2, 'CASH',     'CONFIRMED', NULL),
    ('INV018', 'EMP004', '2026-05-03 19:30:00',  360000, 3, 'CARD',     'CONFIRMED', NULL),
    ('INV019', 'EMP013', '2026-05-03 20:05:00',  240000, 2, 'CASH',     'CONFIRMED', NULL),
    ('INV020', 'EMP008', '2026-05-03 20:40:00',  360000, 3, 'TRANSFER', 'CONFIRMED', NULL),
    ('INV021', 'EMP004', '2026-05-03 19:30:00',  360000, 3, 'CARD',     'CONFIRMED', NULL),
    ('INV022', 'EMP013', '2026-05-03 20:05:00',  240000, 2, 'CASH',     'CONFIRMED', NULL),
    ('INV023', 'EMP008', '2026-05-03 20:40:00',  360000, 3, 'TRANSFER', 'CONFIRMED', NULL);
    GO


    INSERT INTO dbo.tickets (ticket_id, invoice_id, show_seat_id, discount_id, barcode, original_price, discount_amount, final_price, issued_at, status) VALUES
    ('TKT001', 'INV001', 'SHW001_HAL001_A3', NULL, 'BC-TKT007-2026', 120000, 0, 120000, '2026-05-03 09:15:00', 'VALID'),
    ('TKT002', 'INV001', 'SHW001_HAL001_B5', NULL, 'BC-TKT008-2026', 120000, 0, 120000, '2026-05-03 09:15:00', 'VALID'),
    ('TKT003', 'INV001', 'SHW001_HAL001_C7', NULL, 'BC-TKT009-2026', 120000, 0, 120000, '2026-05-03 09:15:00', 'VALID'),
    ('TKT004', 'INV002', 'SHW002_HAL001_D4', NULL, 'BC-TKT010-2026', 120000, 0, 120000, '2026-05-03 09:42:00', 'VALID'),
    ('TKT005', 'INV002', 'SHW002_HAL001_E6', NULL, 'BC-TKT011-2026', 120000, 0, 120000, '2026-05-03 09:42:00', 'VALID'),
    ('TKT006', 'INV003', 'SHW003_HAL001_B2', NULL, 'BC-TKT012-2026', 120000, 0, 120000, '2026-05-03 10:05:00', 'VALID'),
    ('TKT007', 'INV003', 'SHW003_HAL001_C4', NULL, 'BC-TKT013-2026', 120000, 0, 120000, '2026-05-03 10:05:00', 'VALID'),
    ('TKT008', 'INV003', 'SHW003_HAL001_D6', NULL, 'BC-TKT014-2026', 120000, 0, 120000, '2026-05-03 10:05:00', 'VALID'),
    ('TKT009', 'INV004', 'SHW004_HAL002_A1', NULL, 'BC-TKT015-2026', 120000, 0, 120000, '2026-05-03 10:30:00', 'VALID'),
    ('TKT010', 'INV004', 'SHW004_HAL002_B3', NULL, 'BC-TKT016-2026', 120000, 0, 120000, '2026-05-03 10:30:00', 'VALID'),
    ('TKT011', 'INV005', 'SHW005_HAL002_C3', NULL, 'BC-TKT017-2026', 120000, 0, 120000, '2026-05-03 11:15:00', 'VALID'),
    ('TKT012', 'INV006', 'SHW006_HAL002_D2', NULL, 'BC-TKT018-2026', 120000, 0, 120000, '2026-05-03 12:00:00', 'VALID'),
    ('TKT013', 'INV007', 'SHW006_HAL002_E5', NULL, 'BC-TKT019-2026', 120000, 0, 120000, '2026-05-03 12:00:00', 'VALID'),
    ('TKT014', 'INV008', 'SHW006_HAL002_F7', NULL, 'BC-TKT020-2026', 120000, 0, 120000, '2026-05-03 12:00:00', 'VALID'),
    ('TKT015', 'INV009', 'SHW007_HAL003_G4', NULL, 'BC-TKT021-2026', 120000, 0, 120000, '2026-05-03 12:45:00', 'VALID'),
    ('TKT016', 'INV010', 'SHW007_HAL003_H6', NULL, 'BC-TKT022-2026', 120000, 0, 120000, '2026-05-03 12:45:00', 'VALID'),
    ('TKT017', 'INV011', 'SHW008_HAL003_A2', NULL, 'BC-TKT023-2026', 120000, 0, 120000, '2026-05-03 13:20:00', 'VALID'),
    ('TKT018', 'INV012', 'SHW008_HAL003_B4', NULL, 'BC-TKT024-2026', 120000, 0, 120000, '2026-05-03 13:20:00', 'VALID'),
    ('TKT019', 'INV011', 'SHW008_HAL003_C6', NULL, 'BC-TKT025-2026', 120000, 0, 120000, '2026-05-03 13:20:00', 'VALID'),
    ('TKT020', 'INV011', 'SHW008_HAL003_D1', NULL, 'BC-TKT026-2026', 120000, 0, 120000, '2026-05-03 13:20:00', 'VALID'),
    ('TKT021', 'INV012', 'SHW009_HAL003_E3', NULL, 'BC-TKT027-2026', 120000, 0, 120000, '2026-05-03 14:00:00', 'VALID'),
    ('TKT022', 'INV012', 'SHW009_HAL003_F5', NULL, 'BC-TKT028-2026', 120000, 0, 120000, '2026-05-03 14:00:00', 'VALID'),
    ('TKT023', 'INV012', 'SHW009_HAL003_G7', NULL, 'BC-TKT029-2026', 120000, 0, 120000, '2026-05-03 14:00:00', 'VALID'),
    ('TKT024', 'INV013', 'SHW010_HAL004_H2', NULL, 'BC-TKT030-2026', 120000, 0, 120000, '2026-05-03 14:35:00', 'VALID'),
    ('TKT025', 'INV013', 'SHW010_HAL004_I4', NULL, 'BC-TKT031-2026', 120000, 0, 120000, '2026-05-03 14:35:00', 'VALID'),
    ('TKT026', 'INV014', 'SHW011_HAL004_J6', NULL, 'BC-TKT032-2026', 120000, 0, 120000, '2026-05-03 15:10:00', 'VALID'),
    ('TKT027', 'INV014', 'SHW011_HAL004_K8', NULL, 'BC-TKT033-2026', 120000, 0, 120000, '2026-05-03 15:10:00', 'VALID'),
    ('TKT028', 'INV015', 'SHW012_HAL004_A4', NULL, 'BC-TKT034-2026', 120000, 0, 120000, '2026-05-03 15:50:00', 'VALID'),
    ('TKT029', 'INV015', 'SHW012_HAL004_B6', NULL, 'BC-TKT035-2026', 120000, 0, 120000, '2026-05-03 15:50:00', 'VALID'),
    ('TKT030', 'INV015', 'SHW012_HAL004_C8', NULL, 'BC-TKT036-2026', 120000, 0, 120000, '2026-05-03 15:50:00', 'VALID'),
    ('TKT031', 'INV016', 'SHW001_HAL001_F2', NULL, 'BC-TKT037-2026', 120000, 0, 120000, '2026-05-03 16:20:00', 'VALID'),
    ('TKT032', 'INV017', 'SHW002_HAL001_G1', NULL, 'BC-TKT038-2026', 120000, 0, 120000, '2026-05-03 17:05:00', 'VALID'),
    ('TKT033', 'INV017', 'SHW002_HAL001_H3', NULL, 'BC-TKT039-2026', 120000, 0, 120000, '2026-05-03 17:05:00', 'VALID'),
    ('TKT034', 'INV018', 'SHW003_HAL001_A5', NULL, 'BC-TKT040-2026', 120000, 0, 120000, '2026-05-03 17:40:00', 'VALID'),
    ('TKT035', 'INV018', 'SHW003_HAL001_K1', NULL, 'BC-TKT041-2026', 120000, 0, 120000, '2026-05-03 17:40:00', 'VALID'),
    ('TKT036', 'INV018', 'SHW003_HAL001_L4', NULL, 'BC-TKT042-2026', 120000, 0, 120000, '2026-05-03 17:40:00', 'VALID'),
    ('TKT037', 'INV019', 'SHW004_HAL002_E4', NULL, 'BC-TKT043-2026', 120000, 0, 120000, '2026-05-03 18:15:00', 'VALID'),
    ('TKT038', 'INV019', 'SHW004_HAL002_F6', NULL, 'BC-TKT044-2026', 120000, 0, 120000, '2026-05-03 18:15:00', 'VALID'),
    ('TKT039', 'INV019', 'SHW004_HAL002_G2', NULL, 'BC-TKT045-2026', 120000, 0, 120000, '2026-05-03 18:15:00', 'VALID'),
    ('TKT040', 'INV019', 'SHW004_HAL002_H1', NULL, 'BC-TKT046-2026', 120000, 0, 120000, '2026-05-03 18:15:00', 'VALID'),
    ('TKT041', 'INV020', 'SHW005_HAL002_K5', NULL, 'BC-TKT047-2026', 120000, 0, 120000, '2026-05-03 18:50:00', 'VALID'),
    ('TKT042', 'INV020', 'SHW005_HAL002_L2', NULL, 'BC-TKT048-2026', 120000, 0, 120000, '2026-05-03 18:50:00', 'VALID'),
    ('TKT043', 'INV021', 'SHW006_HAL002_A3', NULL, 'BC-TKT049-2026', 120000, 0, 120000, '2026-05-03 19:30:00', 'VALID'),
    ('TKT044', 'INV021', 'SHW006_HAL002_B6', NULL, 'BC-TKT050-2026', 120000, 0, 120000, '2026-05-03 19:30:00', 'VALID'),
    ('TKT045', 'INV021', 'SHW006_HAL002_C8', NULL, 'BC-TKT051-2026', 120000, 0, 120000, '2026-05-03 19:30:00', 'VALID'),
    ('TKT046', 'INV022', 'SHW007_HAL003_I2', NULL, 'BC-TKT052-2026', 120000, 0, 120000, '2026-05-03 20:05:00', 'VALID'),
    ('TKT047', 'INV022', 'SHW007_HAL003_J4', NULL, 'BC-TKT053-2026', 120000, 0, 120000, '2026-05-03 20:05:00', 'VALID'),
    ('TKT048', 'INV023', 'SHW008_HAL003_K3', NULL, 'BC-TKT054-2026', 120000, 0, 120000, '2026-05-03 20:40:00', 'VALID'),
    ('TKT049', 'INV023', 'SHW008_HAL003_L5', NULL, 'BC-TKT055-2026', 120000, 0, 120000, '2026-05-03 20:40:00', 'VALID'),
    ('TKT050', 'INV023', 'SHW008_HAL003_K7', NULL, 'BC-TKT056-2026', 120000, 0, 120000, '2026-05-03 20:40:00', 'VALID');
    GO

    
     Cập nhật trạng thái ghế thành 'BOOKED' cho những vé đã bán
    UPDATE dbo.show_seats
    SET status = 'BOOKED'
    WHERE show_seat_id IN (SELECT show_seat_id FROM dbo.tickets);
    GO

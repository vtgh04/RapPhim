-- ============================================================
--  Script : 04_seed_movies.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Thêm dữ liệu mẫu vào bảng movies (Phù hợp với ảnh poster thực tế)
--           Rating: P=Phổ biến, K=Kids, T13=13+, T16=16+, T18=18+
-- ============================================================
USE RapPhim;
GO
-- Xoá dữ liệu cũ nếu muốn chạy lại seed
DELETE FROM dbo.movies;
GO
-- Seed dữ liệu — khớp với ảnh trong src/main/resources/images/movies/
INSERT INTO dbo.movies (movie_id, title, genre, duration_mins, format_movie, rating, language, release_date, status, description, poster_url) VALUES
-- Mai.jpg / phim-mai-...02e-7077008.jpg
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
PRINT N'[OK] Đã chèn 11 bộ phim mẫu phù hợp với ảnh poster thực tế.';
GO

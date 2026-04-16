-- ============================================================
--  Script : 04_seed_movies.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Chèn dữ liệu mẫu cho bảng movies
-- ============================================================

USE RapPhim;
GO

INSERT INTO dbo.movies (movie_id, title, genre, duration_mins, language, release_date, status, description, poster_url) VALUES 
('MOV001', N'Inception', N'Sci-Fi, Thriller', 148, N'English', '2010-07-16', 'ACTIVE', N'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'images/movies/Inception.jpg'),
('MOV002', N'The Dark Knight', N'Action, Crime', 152, N'English', '2008-07-18', 'ACTIVE', N'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 'images/movies/thedarknight.jpg'),
('MOV003', N'Interstellar', N'Adventure, Sci-Fi', 169, N'English', '2014-11-07', 'ACTIVE', N'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 'images/movies/interstellar-movie-poster-2014-1010771210.jpg'),
('MOV004', N'Lật Mặt 7: Một Điều Ước', N'Drama, Family', 115, N'Vietnamese', '2024-04-26', 'INACTIVE', N'Câu chuyện cảm động về tình mẹ con của bà Hai và 5 người con trưởng thành với những bộn bề cuộc sống.', 'images/movies/7DUjpg.jpg'),
('MOV005', N'Mai', N'Romance, Drama', 131, N'Vietnamese', '2024-02-10', 'ACTIVE', N'Mai xoay quanh câu chuyện về cuộc đời của người phụ nữ mang tên Mai với những góc khuất trong gia đình và tình yêu.', 'images/movies/Mai.jpg'),
('MOV006', N'Avatar', N'Action, Sci-Fi', 162, N'English', '2009-12-18', 'INACTIVE', N'A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.', 'images/movies/avatar_poster.jpg'),
('MOV007', N'Titanic', N'Drama, Romance', 194, N'English', '1997-12-19', 'INACTIVE', N'A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic.', 'images/movies/titanic_poster.jpg'),
('MOV008', N'The Matrix', N'Action, Sci-Fi', 136, N'English', '1999-03-31', 'ACTIVE', N'When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.', 'images/movies/the_matrix_poster.jpg'),
('MOV009', N'Jurassic Park', N'Action, Adventure', 127, N'English', '1993-06-11', 'INACTIVE', N'A pragmatic paleontologist touring an almost complete theme park on an island in Central America is tasked with protecting a couple of kids after a power failure causes the park''s cloned dinosaurs to run loose.', 'images/movies/jurassic_park_poster.jpg'),
('MOV010', N'Harry Potter and the Sorcerer''s Stone', N'Adventure, Family', 152, N'English', '2001-11-16', 'INACTIVE', N'An orphaned boy enrolls in a school of wizardry, where he learns the truth about himself, his family and the terrible evil that haunts the magical world.', 'images/movies/harry_potter_poster.jpg');
GO
    
PRINT N'[OK] Dữ liệu mẫu bảng movies đã được khởi tạo.';
GO

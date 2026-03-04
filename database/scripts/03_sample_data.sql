-- ============================================================
-- RapPhim - Hệ Thống Quản Lý Rạp Chiếu Phim
-- Script 03: Dữ Liệu Mẫu
-- ============================================================

USE rapphim_db;

-- ===== DỮ LIỆU PHIM =====
INSERT INTO phim (ten_phim, the_loai, thoi_luong, ngon_ngu, quoc_gia, nam_san_xuat, dao_dien, dien_vien, mo_ta, gia_ve, trang_thai, gioi_han_tuoi) VALUES
('Avengers: Endgame', 'Hành động, Phiêu lưu', 181, 'Tiếng Anh', 'Mỹ', 2019, 'Anthony Russo, Joe Russo', 'Robert Downey Jr., Chris Evans', 'Sau sự kiện bi thảm của Infinity War, vũ trụ đang trong đống đổ nát.', 90000, 'Đang chiếu', 13),
('Interstellar', 'Khoa học viễn tưởng', 169, 'Tiếng Anh', 'Mỹ', 2014, 'Christopher Nolan', 'Matthew McConaughey, Anne Hathaway', 'Một nhóm nhà thám hiểm đi qua lỗ sâu trong không gian để đảm bảo sự tồn tồn của nhân loại.', 85000, 'Đang chiếu', 0),
('Parasite', 'Tâm lý, Kinh dị', 132, 'Tiếng Hàn', 'Hàn Quốc', 2019, 'Bong Joon-ho', 'Song Kang-ho, Lee Sun-kyun', 'Toàn bộ gia đình Ki-taek thất nghiệp và rất quan tâm đến giới thượng lưu.', 80000, 'Sắp chiếu', 16),
('Cô Gái Đến Từ Hôm Qua', 'Tình cảm, Hài', 100, 'Tiếng Việt', 'Việt Nam', 2017, 'Phan Gia Nhật Linh', 'Miu Lê, Ngô Kiến Huy', 'Câu chuyện tình yêu đặc biệt giữa một chàng trai và cô gái đến từ quá khứ.', 75000, 'Đang chiếu', 0),
('The Dark Knight', 'Hành động, Tội phạm', 152, 'Tiếng Anh', 'Mỹ', 2008, 'Christopher Nolan', 'Christian Bale, Heath Ledger', 'Batman đối đầu với tên tội phạm điên loạn Joker.', 85000, 'Ngừng chiếu', 16);

-- ===== DỮ LIỆU PHÒNG CHIẾU =====
INSERT INTO phong_chieu (ten_phong, loai_phong, suc_chua, trang_thai) VALUES
('Phòng 1 - 2D Standard', '2D', 100, 'Hoạt động'),
('Phòng 2 - 3D Premium', '3D', 80, 'Hoạt động'),
('Phòng 3 - 4DX', '4DX', 60, 'Hoạt động'),
('Phòng 4 - IMAX', 'IMAX', 150, 'Hoạt động'),
('Phòng 5 - VIP', '2D', 40, 'Bảo trì');

-- ===== DỮ LIỆU NHÂN VIÊN =====
INSERT INTO nhan_vien (ho_ten, so_dien_thoai, email, vai_tro, ten_dang_nhap, mat_khau, trang_thai, luong, ngay_vao_lam) VALUES
('Nguyễn Văn Admin', '0901234567', 'admin@rapphim.vn', 'Admin', 'admin', '$2a$10$xG5L3...hashedpassword', 'Hoạt động', 20000000, '2020-01-01'),
('Trần Thị Quản Lý', '0902345678', 'quanly@rapphim.vn', 'Quản lý', 'quanly01', '$2a$10$xG5L3...hashedpassword', 'Hoạt động', 15000000, '2021-03-15'),
('Lê Văn Thu Ngân', '0903456789', 'thungan01@rapphim.vn', 'Thu ngân', 'thungan01', '$2a$10$xG5L3...hashedpassword', 'Hoạt động', 10000000, '2022-06-01'),
('Phạm Thị Thu Ngân', '0904567890', 'thungan02@rapphim.vn', 'Thu ngân', 'thungan02', '$2a$10$xG5L3...hashedpassword', 'Hoạt động', 10000000, '2022-07-15');

-- ===== DỮ LIỆU KHÁCH HÀNG =====
INSERT INTO khach_hang (ho_ten, so_dien_thoai, email, gioi_tinh, ngay_sinh, loai_thanh_vien, diem_tich_luy) VALUES
('Nguyễn Thị Lan', '0911111111', 'lan.nguyen@gmail.com', 'Nữ', '1995-05-15', 'Vàng', 1200),
('Trần Văn Minh', '0922222222', 'minh.tran@gmail.com', 'Nam', '1990-08-20', 'Bạc', 650),
('Lê Thị Hoa', '0933333333', 'hoa.le@gmail.com', 'Nữ', '2000-12-01', 'Thường', 150),
('Phạm Đức Anh', '0944444444', 'duc.anh@gmail.com', 'Nam', '1988-03-10', 'Kim Cương', 3500);

-- ===== DỮ LIỆU DỊCH VỤ ĂN UỐNG =====
INSERT INTO dich_vu (ten_dich_vu, mo_ta, gia, so_luong_con, loai) VALUES
('Bắp rang bơ nhỏ', 'Bắp rang bơ size nhỏ 100g', 35000, 200, 'Bắp rang'),
('Bắp rang bơ lớn', 'Bắp rang bơ size lớn 200g', 55000, 150, 'Bắp rang'),
('Coca-Cola lon', 'Coca-Cola 330ml', 25000, 500, 'Nước uống'),
('Pepsi lon', 'Pepsi 330ml', 25000, 300, 'Nước uống'),
('Nước suối', 'Nước suối Aquafina 500ml', 15000, 400, 'Nước uống'),
('Combo 1 người', 'Bắp rang nhỏ + Nước uống', 50000, 100, 'Combo'),
('Combo 2 người', 'Bắp rang lớn + 2 Nước uống', 90000, 100, 'Combo');

SELECT 'Dữ liệu mẫu đã được thêm thành công!' AS thong_bao;

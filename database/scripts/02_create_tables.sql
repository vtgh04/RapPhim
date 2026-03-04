-- ============================================================
-- RapPhim - Hệ Thống Quản Lý Rạp Chiếu Phim
-- Script 02: Tạo Bảng (Schema)
-- ============================================================

USE rapphim_db;

-- ===== BẢNG PHIM =====
CREATE TABLE IF NOT EXISTS phim (
    ma_phim         INT AUTO_INCREMENT PRIMARY KEY,
    ten_phim        VARCHAR(255) NOT NULL,
    the_loai        VARCHAR(100),
    thoi_luong      INT,                         -- Phút
    ngon_ngu        VARCHAR(50),
    quoc_gia        VARCHAR(100),
    nam_san_xuat    YEAR,
    dao_dien        VARCHAR(255),
    dien_vien       TEXT,
    mo_ta           TEXT,
    gia_ve          DECIMAL(10, 0),
    hinh_anh        VARCHAR(500),
    trailer         VARCHAR(500),
    trang_thai      ENUM('Đang chiếu', 'Sắp chiếu', 'Ngừng chiếu') DEFAULT 'Sắp chiếu',
    gioi_han_tuoi   INT DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG PHÒNG CHIẾU =====
CREATE TABLE IF NOT EXISTS phong_chieu (
    ma_phong        INT AUTO_INCREMENT PRIMARY KEY,
    ten_phong       VARCHAR(100) NOT NULL,
    loai_phong      ENUM('2D', '3D', '4DX', 'IMAX', 'Dolby') DEFAULT '2D',
    suc_chua        INT NOT NULL,
    trang_thai      ENUM('Hoạt động', 'Bảo trì', 'Ngừng hoạt động') DEFAULT 'Hoạt động',
    mo_ta           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG GHẾ NGỒI =====
CREATE TABLE IF NOT EXISTS ghe (
    ma_ghe          INT AUTO_INCREMENT PRIMARY KEY,
    ma_phong        INT NOT NULL,
    so_hang         CHAR(2) NOT NULL,           -- A, B, C...
    so_ghe          INT NOT NULL,               -- 1, 2, 3...
    loai_ghe        ENUM('Thường', 'VIP', 'Cặp đôi') DEFAULT 'Thường',
    trang_thai      ENUM('Sẵn sàng', 'Hỏng', 'Bảo trì') DEFAULT 'Sẵn sàng',
    FOREIGN KEY (ma_phong) REFERENCES phong_chieu(ma_phong) ON DELETE CASCADE,
    UNIQUE KEY unique_seat (ma_phong, so_hang, so_ghe)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG NHÂN VIÊN =====
CREATE TABLE IF NOT EXISTS nhan_vien (
    ma_nhan_vien    INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten          VARCHAR(100) NOT NULL,
    so_dien_thoai   VARCHAR(15),
    email           VARCHAR(255) UNIQUE,
    vai_tro         ENUM('Admin', 'Quản lý', 'Thu ngân', 'Bảo vệ') DEFAULT 'Thu ngân',
    ten_dang_nhap   VARCHAR(50) UNIQUE NOT NULL,
    mat_khau        VARCHAR(255) NOT NULL,       -- Lưu dạng hash
    trang_thai      ENUM('Hoạt động', 'Nghỉ việc') DEFAULT 'Hoạt động',
    luong           DECIMAL(12, 0),
    ngay_vao_lam    DATE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG KHÁCH HÀNG =====
CREATE TABLE IF NOT EXISTS khach_hang (
    ma_khach_hang   INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten          VARCHAR(100) NOT NULL,
    so_dien_thoai   VARCHAR(15) UNIQUE,
    email           VARCHAR(255),
    dia_chi         TEXT,
    gioi_tinh       ENUM('Nam', 'Nữ', 'Khác') DEFAULT 'Nam',
    ngay_sinh       DATE,
    loai_thanh_vien ENUM('Thường', 'Bạc', 'Vàng', 'Kim Cương') DEFAULT 'Thường',
    diem_tich_luy   INT DEFAULT 0,
    trang_thai      ENUM('Hoạt động', 'Vô hiệu hóa') DEFAULT 'Hoạt động',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG LỊCH CHIẾU =====
CREATE TABLE IF NOT EXISTS lich_chieu (
    ma_lich_chieu   INT AUTO_INCREMENT PRIMARY KEY,
    ma_phim         INT NOT NULL,
    ma_phong        INT NOT NULL,
    ngay_chieu      DATE NOT NULL,
    gio_chieu       TIME NOT NULL,
    gio_ket_thuc    TIME,
    loai_chieu      ENUM('2D', '3D', '4DX', 'IMAX') DEFAULT '2D',
    gia_ve          DECIMAL(10, 0),
    trang_thai      ENUM('Đang mở', 'Đã hủy', 'Đã chiếu') DEFAULT 'Đang mở',
    FOREIGN KEY (ma_phim) REFERENCES phim(ma_phim) ON DELETE CASCADE,
    FOREIGN KEY (ma_phong) REFERENCES phong_chieu(ma_phong) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG VÉ =====
CREATE TABLE IF NOT EXISTS ve (
    ma_ve           INT AUTO_INCREMENT PRIMARY KEY,
    ma_lich_chieu   INT NOT NULL,
    ma_ghe          INT NOT NULL,
    ma_khach_hang   INT,
    ma_nhan_vien    INT NOT NULL,
    loai_ve         ENUM('Thường', 'VIP', 'Cặp đôi', 'Học sinh/SV') DEFAULT 'Thường',
    gia_ve          DECIMAL(10, 0) NOT NULL,
    thoi_gian_dat   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    trang_thai      ENUM('Đã đặt', 'Đã thanh toán', 'Đã hủy') DEFAULT 'Đã đặt',
    ma_giao_dich    VARCHAR(100),
    FOREIGN KEY (ma_lich_chieu) REFERENCES lich_chieu(ma_lich_chieu),
    FOREIGN KEY (ma_ghe) REFERENCES ghe(ma_ghe),
    FOREIGN KEY (ma_khach_hang) REFERENCES khach_hang(ma_khach_hang),
    FOREIGN KEY (ma_nhan_vien) REFERENCES nhan_vien(ma_nhan_vien),
    UNIQUE KEY unique_seat_showtime (ma_lich_chieu, ma_ghe)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG DỊCH VỤ ĂN UỐNG =====
CREATE TABLE IF NOT EXISTS dich_vu (
    ma_dich_vu      INT AUTO_INCREMENT PRIMARY KEY,
    ten_dich_vu     VARCHAR(100) NOT NULL,
    mo_ta           TEXT,
    gia             DECIMAL(10, 0) NOT NULL,
    so_luong_con    INT DEFAULT 0,
    loai            ENUM('Bắp rang', 'Nước uống', 'Snack', 'Combo') DEFAULT 'Bắp rang',
    hinh_anh        VARCHAR(500),
    trang_thai      ENUM('Còn hàng', 'Hết hàng') DEFAULT 'Còn hàng'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===== BẢNG HOÁ ĐƠN DỊCH VỤ =====
CREATE TABLE IF NOT EXISTS hoa_don_dich_vu (
    ma_hoa_don      INT AUTO_INCREMENT PRIMARY KEY,
    ma_ve           INT NOT NULL,
    ma_dich_vu      INT NOT NULL,
    so_luong        INT DEFAULT 1,
    gia             DECIMAL(10, 0) NOT NULL,
    FOREIGN KEY (ma_ve) REFERENCES ve(ma_ve),
    FOREIGN KEY (ma_dich_vu) REFERENCES dich_vu(ma_dich_vu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SELECT 'Tất cả bảng đã được tạo thành công!' AS thong_bao;

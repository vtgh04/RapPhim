-- ============================================================
--  Script: 01_create_employees.sql
--  Database: MySQL 8.x
--  Mô tả: Tạo bảng employees cho hệ thống rạp chiếu phim
--  Nghiệp vụ:
--    - Chỉ tài khoản có status = 'ACTIVE' được đăng nhập
--    - role = 'MANAGER' -> điều hướng tới Admin Page
--    - role = 'STAFF'   -> điều hướng tới Staff Page
-- ============================================================

CREATE DATABASE IF NOT EXISTS rapphim
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE rapphim;

-- ------------------------------------------------------------
-- Bảng: employees
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    employee_id   INT             NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(100)    NOT NULL                    COMMENT 'Họ và tên đầy đủ',
    username      VARCHAR(50)     NOT NULL                    COMMENT 'Tên đăng nhập — duy nhất',
    password_hash VARCHAR(255)    NOT NULL                    COMMENT 'Mật khẩu đã băm bằng BCrypt',
    role          ENUM('MANAGER','STAFF') NOT NULL            COMMENT 'MANAGER: quyền quản lý; STAFF: nhân viên',
    status        ENUM('ACTIVE','INACTIVE') NOT NULL
                  DEFAULT 'ACTIVE'                            COMMENT 'Chỉ ACTIVE mới được đăng nhập',
    phone         VARCHAR(15)                                 COMMENT 'Số điện thoại',
    email         VARCHAR(255)                                COMMENT 'Email công ty (nên duy nhất)',
    created_at    DATETIME        NOT NULL DEFAULT NOW()      COMMENT 'Thời điểm tạo tài khoản',

    PRIMARY KEY (employee_id),
    UNIQUE KEY uq_username (username),
    UNIQUE KEY uq_email    (email)
)   ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = 'Bảng nhân viên — quản lý đăng nhập và phân quyền';

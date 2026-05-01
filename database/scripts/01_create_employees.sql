-- ============================================================
--  Script : 01_create_employees.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo database RapPhim và bảng employees
--
--  ⚠ CHẠY SCRIPT NÀY SẼ XOÁ VÀ TẠO LẠI BẢNG employees
--     (DROP TABLE nếu tồn tại → CREATE lại)
--
--  Nghiệp vụ:
--    - employee_id: định dạng EMP001, EMP002… (sinh từ tầng ứng dụng)
--    - Chỉ tài khoản status = 'ACTIVE' được đăng nhập
--    - role = 'MANAGER' → Admin Page
--    - role = 'STAFF'   → Staff Page
--    - password: lưu plain-text (không hash)
-- ============================================================

-- ------------------------------------------------------------
-- 1. Tạo database (bỏ qua nếu đã có)
-- ------------------------------------------------------------
IF NOT EXISTS (
    SELECT name FROM sys.databases WHERE name = N'RapPhim'
)
BEGIN
    CREATE DATABASE RapPhim
        COLLATE Vietnamese_CI_AS;
    PRINT N'[OK] Database RapPhim đã được tạo.';
END
ELSE
BEGIN
    PRINT N'[SKIP] Database RapPhim đã tồn tại.';
END
GO

USE RapPhim;
GO

-- ------------------------------------------------------------
-- 2. Xoá bảng cũ nếu tồn tại (reset sạch schema)
-- ------------------------------------------------------------
IF OBJECT_ID(N'dbo.employees', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.employees;
    PRINT N'[OK] Bảng dbo.employees cũ đã bị xoá.';
END
GO

-- ------------------------------------------------------------
-- 3. Tạo bảng employees với schema chuẩn
-- ------------------------------------------------------------
CREATE TABLE dbo.employees (

    -- ── Khoá chính ──────────────────────────────────────────────────
    -- Định dạng: EMP001, EMP002, ... — sinh từ tầng ứng dụng
    employee_id   VARCHAR(20)    NOT NULL,

    -- ── Thông tin cá nhân ───────────────────────────────────────────
    full_name     NVARCHAR(100)  NOT NULL,          -- Họ tên đầy đủ (hỗ trợ tiếng Việt)
    username      VARCHAR(50)    NOT NULL,           -- Tên đăng nhập, duy nhất
    password      VARCHAR(255)   NOT NULL,           -- Mật khẩu plain-text

    -- ── Phân quyền & trạng thái ─────────────────────────────────────
    role          VARCHAR(20)    NOT NULL,           -- 'MANAGER' | 'STAFF'
    status        VARCHAR(20)    NOT NULL
                  CONSTRAINT df_employees_status DEFAULT 'ACTIVE',   -- 'ACTIVE' | 'INACTIVE'

    -- ── Liên hệ ─────────────────────────────────────────────────────
    phone         VARCHAR(20)    NULL,               -- Số điện thoại (nullable)
    email         VARCHAR(100)   NULL,               -- Email (nullable)

    -- ── Khoá chính ──────────────────────────────────────────────────
    CONSTRAINT pk_employees          PRIMARY KEY (employee_id),

    -- ── Ràng buộc duy nhất ──────────────────────────────────────────
    CONSTRAINT uq_employees_username UNIQUE (username),
    CONSTRAINT uq_employees_email    UNIQUE (email),

    -- ── Ràng buộc miền giá trị ──────────────────────────────────────
    CONSTRAINT chk_employees_role    CHECK (role   IN ('MANAGER', 'STAFF')),
    CONSTRAINT chk_employees_status  CHECK (status IN ('ACTIVE',  'INACTIVE'))
);
GO

-- ── Index hỗ trợ tìm kiếm nhanh ─────────────────────────────────────────
CREATE INDEX idx_employees_username ON dbo.employees (username);
CREATE INDEX idx_employees_status   ON dbo.employees (status);
GO

PRINT N'[OK] Bảng dbo.employees đã được tạo thành công.';
GO

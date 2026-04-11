-- ============================================================
--  Script : 02_seed_employees.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Dữ liệu mẫu cho bảng employees
--
--  ⚠ Chạy script này SAU khi đã chạy 01_create_employees.sql
--
--  Tài khoản mẫu:
--    username=manager01   password=123  role=MANAGER  status=ACTIVE
--    username=staff01     password=123   role=STAFF    status=ACTIVE
--    username=staff_off   password=Staff@123   role=STAFF    status=INACTIVE (test khoá)
-- ============================================================

USE RapPhim;
GO

-- ------------------------------------------------------------
-- Dùng MERGE để tránh lỗi khi chạy lại (idempotent)
-- ------------------------------------------------------------
MERGE INTO dbo.employees AS target
USING (VALUES
    -- (employee_id, full_name, username, password, role, status, phone, email)
    (
        'EMP001',
        N'Nguyen Van An',
        'manager01',
        '123',
        'MANAGER',
        'ACTIVE',
        '0901234567',
        'manager01@rapphim.com'
    ),
    (
        'EMP002',
        N'Tran Thi Binh',
        'staff01',
        '123',
        'STAFF',
        'ACTIVE',
        '0912345678',
        'staff01@rapphim.com'
    ),
    (
        'EMP003',
        N'Le Van Cuong',
        'staff_off',
        'Staff@123',
        'STAFF',
        'INACTIVE',                          -- tài khoản bị khoá → test không cho đăng nhập
        '0923456789',
        'staff_off@rapphim.com'
    )
) AS source (employee_id, full_name, username, password, role, status, phone, email)
ON target.employee_id = source.employee_id

-- Nếu đã tồn tại: cập nhật mọi cột trừ employee_id
WHEN MATCHED THEN
    UPDATE SET
        full_name = source.full_name,
        username  = source.username,
        password  = source.password,
        role      = source.role,
        status    = source.status,
        phone     = source.phone,
        email     = source.email

-- Nếu chưa tồn tại: chèn mới
WHEN NOT MATCHED THEN
    INSERT (employee_id, full_name, username, password, role, status, phone, email)
    VALUES (source.employee_id, source.full_name, source.username, source.password,
            source.role, source.status, source.phone, source.email);
GO

-- ------------------------------------------------------------
-- Kiểm tra kết quả
-- ------------------------------------------------------------
SELECT
    employee_id,
    full_name,
    username,
    password,
    role,
    status,
    phone,
    email
FROM dbo.employees
ORDER BY employee_id;
GO

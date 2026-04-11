-- ============================================================
--  Script: 02_seed_employees.sql
--  Mô tả: Dữ liệu mẫu cho bảng employees
--  Lưu ý: password_hash được tạo bằng BCrypt với password:
--    admin123  -> hash tương ứng bên dưới
--    staff123  -> hash tương ứng bên dưới
--  Dùng PasswordUtils.hashPassword() để tạo hash mới.
-- ============================================================

USE rapphim;

INSERT INTO employees (full_name, username, password_hash, role, status, phone, email)
VALUES
    -- manager: password = 'admin123'
    ('Nguyen Van An',
     'manager01',
     '$2a$12$6v9VQPi5o5e5K5.7KlvF5OzMoGqvk3Fy4HZiE7nYH1G3vGqXSDWYe',
     'MANAGER',
     'ACTIVE',
     '0901234567',
     'manager01@rapphim.com'),

    -- staff: password = 'staff123'
    ('Tran Thi Binh',
     'staff01',
     '$2a$12$YwQnV3KxVkz2FkTVqXDlne1R1Z2WqnSjPwfHLqHLoIWqxGiJVQxHu',
     'STAFF',
     'ACTIVE',
     '0912345678',
     'staff01@rapphim.com'),

    -- inactive account (should NOT be able to login)
    ('Le Van Cuong',
     'staff_inactive',
     '$2a$12$YwQnV3KxVkz2FkTVqXDlne1R1Z2WqnSjPwfHLqHLoIWqxGiJVQxHu',
     'STAFF',
     'INACTIVE',
     '0923456789',
     'staff_inactive@rapphim.com');

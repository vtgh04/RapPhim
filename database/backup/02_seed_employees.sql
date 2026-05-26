MERGE INTO dbo.employees AS target
USING (VALUES
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
ON target.employee_id = source.employee_id

WHEN MATCHED THEN
    UPDATE SET
        full_name = source.full_name,
        username  = source.username,
        password  = source.password,
        role      = source.role,
        status    = source.status,
        phone     = source.phone,
        email     = source.email

WHEN NOT MATCHED THEN
    INSERT (employee_id, full_name, username, password, role, status, phone, email)
    VALUES (source.employee_id, source.full_name, source.username, source.password,
            source.role, source.status, source.phone, source.email);
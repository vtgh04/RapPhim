USE RapPhim;
GO

INSERT INTO cinema_halls (hall_id, name, hall_type, total_rows, total_cols, status) VALUES
('HAL001', N'Phòng 1', '2D', 12, 8, 'ACTIVE'),
('HAL002', N'Phòng 2', '2D', 12, 8, 'ACTIVE'),
('HAL003', N'Phòng 3', '3D', 12, 8, 'ACTIVE'),
('HAL004', N'Phòng 4', '3D', 12, 8, 'ACTIVE'),
('HAL005', N'Phòng IMAX 1', 'IMAX', 9, 9, 'ACTIVE'),
('HAL006', N'Phòng IMAX 2', 'IMAX', 9, 9, 'ACTIVE'),
('HAL007', N'Phòng 5', '2D', 12, 8, 'INACTIVE'),
('HAL008', N'Phòng 6', '3D', 12, 8, 'INACTIVE');

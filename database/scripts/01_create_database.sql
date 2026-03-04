-- ============================================================
-- RapPhim - Hệ Thống Quản Lý Rạp Chiếu Phim
-- Script 01: Tạo Database
-- ============================================================

DROP DATABASE IF EXISTS rapphim_db;
CREATE DATABASE rapphim_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE rapphim_db;

SELECT 'Database rapphim_db đã được tạo thành công!' AS thong_bao;

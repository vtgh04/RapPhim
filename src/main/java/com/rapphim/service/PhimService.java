package com.rapphim.service;

import java.util.List;

import com.rapphim.dao.PhimDAO;
import com.rapphim.model.Phim;

/**
 * Service class xử lý business logic cho module Phim.
 * Tầng trung gian giữa Controller và DAO.
 */
public class PhimService {

    private final PhimDAO phimDAO;

    public PhimService() {
        this.phimDAO = new PhimDAO();
    }

    /**
     * Thêm phim mới
     */
    public boolean themPhim(Phim phim) {
        // Validate dữ liệu trước khi lưu
        if (phim.getTenPhim() == null || phim.getTenPhim().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phim không được để trống!");
        }
        if (phim.getThoiLuong() <= 0) {
            throw new IllegalArgumentException("Thời lượng phim phải lớn hơn 0!");
        }
        return phimDAO.insert(phim);
    }

    /**
     * Cập nhật thông tin phim
     */
    public boolean capNhatPhim(Phim phim) {
        if (phim.getMaPhim() <= 0) {
            throw new IllegalArgumentException("Mã phim không hợp lệ!");
        }
        return phimDAO.update(phim);
    }

    /**
     * Xóa phim theo ID
     */
    public boolean xoaPhim(int maPhim) {
        // Có thể kiểm tra xem phim còn lịch chiếu không trước khi xóa
        return phimDAO.delete(maPhim);
    }

    /**
     * Tìm phim theo ID
     */
    public Phim timPhimTheoId(int maPhim) {
        return phimDAO.findById(maPhim);
    }

    /**
     * Lấy tất cả phim
     */
    public List<Phim> layTatCaPhim() {
        return phimDAO.findAll();
    }

    /**
     * Lấy phim đang chiếu
     */
    public List<Phim> layPhimDangChieu() {
        return phimDAO.findByTrangThai("Đang chiếu");
    }

    /**
     * Lấy phim sắp chiếu
     */
    public List<Phim> layPhimSapChieu() {
        return phimDAO.findByTrangThai("Sắp chiếu");
    }

    /**
     * Tìm kiếm phim theo tên
     */
    public List<Phim> timKiemPhim(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layTatCaPhim();
        }
        return phimDAO.searchByName(keyword.trim());
    }
}

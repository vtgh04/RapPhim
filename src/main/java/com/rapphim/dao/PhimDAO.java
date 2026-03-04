package com.rapphim.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rapphim.config.DatabaseConnection;
import com.rapphim.model.Phim;

/**
 * DAO class xử lý các thao tác CRUD với bảng PHIM trong database.
 */
public class PhimDAO implements GenericDAO<Phim, Integer> {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean insert(Phim phim) {
        String sql = "INSERT INTO phim (ten_phim, the_loai, thoi_luong, ngon_ngu, " +
                     "quoc_gia, nam_san_xuat, dao_dien, dien_vien, mo_ta, " +
                     "gia_ve, hinh_anh, trailer, trang_thai, gioi_han_tuoi) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, phim.getTenPhim());
            ps.setString(2, phim.getTheLoai());
            ps.setInt(3, phim.getThoiLuong());
            ps.setString(4, phim.getNgonNgu());
            ps.setString(5, phim.getQuocGia());
            ps.setInt(6, phim.getNamSanXuat());
            ps.setString(7, phim.getDaoDien());
            ps.setString(8, phim.getDienVien());
            ps.setString(9, phim.getMoTa());
            ps.setBigDecimal(10, phim.getGiaVe());
            ps.setString(11, phim.getHinhAnh());
            ps.setString(12, phim.getTrailer());
            ps.setString(13, phim.getTrangThai());
            ps.setInt(14, phim.getGioiHanTuoi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm phim: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Phim phim) {
        String sql = "UPDATE phim SET ten_phim=?, the_loai=?, thoi_luong=?, " +
                     "ngon_ngu=?, quoc_gia=?, nam_san_xuat=?, dao_dien=?, " +
                     "dien_vien=?, mo_ta=?, gia_ve=?, hinh_anh=?, trailer=?, " +
                     "trang_thai=?, gioi_han_tuoi=? WHERE ma_phim=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, phim.getTenPhim());
            ps.setString(2, phim.getTheLoai());
            ps.setInt(3, phim.getThoiLuong());
            ps.setString(4, phim.getNgonNgu());
            ps.setString(5, phim.getQuocGia());
            ps.setInt(6, phim.getNamSanXuat());
            ps.setString(7, phim.getDaoDien());
            ps.setString(8, phim.getDienVien());
            ps.setString(9, phim.getMoTa());
            ps.setBigDecimal(10, phim.getGiaVe());
            ps.setString(11, phim.getHinhAnh());
            ps.setString(12, phim.getTrailer());
            ps.setString(13, phim.getTrangThai());
            ps.setInt(14, phim.getGioiHanTuoi());
            ps.setInt(15, phim.getMaPhim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật phim: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM phim WHERE ma_phim = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa phim: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Phim findById(Integer id) {
        String sql = "SELECT * FROM phim WHERE ma_phim = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm phim: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Phim> findAll() {
        List<Phim> list = new ArrayList<>();
        String sql = "SELECT * FROM phim ORDER BY ma_phim DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách phim: " + e.getMessage());
        }
        return list;
    }

    /**
     * Tìm phim theo trạng thái
     */
    public List<Phim> findByTrangThai(String trangThai) {
        List<Phim> list = new ArrayList<>();
        String sql = "SELECT * FROM phim WHERE trang_thai = ? ORDER BY ten_phim";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm phim theo trạng thái: " + e.getMessage());
        }
        return list;
    }

    /**
     * Tìm kiếm phim theo tên
     */
    public List<Phim> searchByName(String keyword) {
        List<Phim> list = new ArrayList<>();
        String sql = "SELECT * FROM phim WHERE ten_phim LIKE ? ORDER BY ten_phim";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm phim: " + e.getMessage());
        }
        return list;
    }

    /**
     * Map ResultSet sang đối tượng Phim
     */
    private Phim mapRow(ResultSet rs) throws SQLException {
        Phim phim = new Phim();
        phim.setMaPhim(rs.getInt("ma_phim"));
        phim.setTenPhim(rs.getString("ten_phim"));
        phim.setTheLoai(rs.getString("the_loai"));
        phim.setThoiLuong(rs.getInt("thoi_luong"));
        phim.setNgonNgu(rs.getString("ngon_ngu"));
        phim.setQuocGia(rs.getString("quoc_gia"));
        phim.setNamSanXuat(rs.getInt("nam_san_xuat"));
        phim.setDaoDien(rs.getString("dao_dien"));
        phim.setDienVien(rs.getString("dien_vien"));
        phim.setMoTa(rs.getString("mo_ta"));
        phim.setGiaVe(rs.getBigDecimal("gia_ve"));
        phim.setHinhAnh(rs.getString("hinh_anh"));
        phim.setTrailer(rs.getString("trailer"));
        phim.setTrangThai(rs.getString("trang_thai"));
        phim.setGioiHanTuoi(rs.getInt("gioi_han_tuoi"));
        return phim;
    }
}

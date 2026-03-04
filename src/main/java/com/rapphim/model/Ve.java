package com.rapphim.model;

import java.time.LocalDateTime;

/**
 * Entity class đại diện cho Vé Xem Phim.
 */
public class Ve {
    private int maVe;
    private int maLichChieu;
    private int maGhe;
    private int maKhachHang;
    private int maNhanVien;          // Nhân viên bán vé
    private String loaiVe;           // "Thường", "VIP", "Cặp đôi"
    private double giaVe;
    private LocalDateTime thoiGianDat;
    private String trangThai;        // "Đã đặt", "Đã thanh toán", "Đã hủy"
    private String maGiaoDich;       // Mã giao dịch thanh toán

    // ===== CONSTRUCTORS =====
    public Ve() {}

    public Ve(int maVe, int maLichChieu, int maGhe, int maKhachHang,
              int maNhanVien, String loaiVe, double giaVe,
              LocalDateTime thoiGianDat, String trangThai, String maGiaoDich) {
        this.maVe = maVe;
        this.maLichChieu = maLichChieu;
        this.maGhe = maGhe;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.loaiVe = loaiVe;
        this.giaVe = giaVe;
        this.thoiGianDat = thoiGianDat;
        this.trangThai = trangThai;
        this.maGiaoDich = maGiaoDich;
    }

    // ===== GETTERS & SETTERS =====
    public int getMaVe() { return maVe; }
    public void setMaVe(int maVe) { this.maVe = maVe; }

    public int getMaLichChieu() { return maLichChieu; }
    public void setMaLichChieu(int maLichChieu) { this.maLichChieu = maLichChieu; }

    public int getMaGhe() { return maGhe; }
    public void setMaGhe(int maGhe) { this.maGhe = maGhe; }

    public int getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(int maKhachHang) { this.maKhachHang = maKhachHang; }

    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getLoaiVe() { return loaiVe; }
    public void setLoaiVe(String loaiVe) { this.loaiVe = loaiVe; }

    public double getGiaVe() { return giaVe; }
    public void setGiaVe(double giaVe) { this.giaVe = giaVe; }

    public LocalDateTime getThoiGianDat() { return thoiGianDat; }
    public void setThoiGianDat(LocalDateTime thoiGianDat) { this.thoiGianDat = thoiGianDat; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

    @Override
    public String toString() {
        return "Ve{maVe=" + maVe + ", maLichChieu=" + maLichChieu +
               ", maGhe=" + maGhe + ", trangThai='" + trangThai + "'}";
    }
}

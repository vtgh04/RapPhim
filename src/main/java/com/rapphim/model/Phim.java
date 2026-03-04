package com.rapphim.model;

import java.math.BigDecimal;

/**
 * Entity class đại diện cho một bộ Phim.
 */
public class Phim {
    private int maPhim;
    private String tenPhim;
    private String theLoai;
    private int thoiLuong;          // Đơn vị: phút
    private String ngonNgu;
    private String quocGia;
    private int namSanXuat;
    private String daoDien;
    private String dienVien;
    private String moTa;
    private BigDecimal giaVe;        // Giá vé cơ bản
    private String hinhAnh;          // Đường dẫn ảnh poster
    private String trailer;          // URL trailer
    private String trangThai;        // "Đang chiếu", "Sắp chiếu", "Ngừng chiếu"
    private int gioiHanTuoi;         // 0 = mọi lứa tuổi, 13, 16, 18

    // ===== CONSTRUCTORS =====
    public Phim() {}

    public Phim(int maPhim, String tenPhim, String theLoai, int thoiLuong,
                String ngonNgu, String quocGia, int namSanXuat,
                String daoDien, String dienVien, String moTa,
                BigDecimal giaVe, String hinhAnh, String trailer,
                String trangThai, int gioiHanTuoi) {
        this.maPhim = maPhim;
        this.tenPhim = tenPhim;
        this.theLoai = theLoai;
        this.thoiLuong = thoiLuong;
        this.ngonNgu = ngonNgu;
        this.quocGia = quocGia;
        this.namSanXuat = namSanXuat;
        this.daoDien = daoDien;
        this.dienVien = dienVien;
        this.moTa = moTa;
        this.giaVe = giaVe;
        this.hinhAnh = hinhAnh;
        this.trailer = trailer;
        this.trangThai = trangThai;
        this.gioiHanTuoi = gioiHanTuoi;
    }

    // ===== GETTERS & SETTERS =====
    public int getMaPhim() { return maPhim; }
    public void setMaPhim(int maPhim) { this.maPhim = maPhim; }

    public String getTenPhim() { return tenPhim; }
    public void setTenPhim(String tenPhim) { this.tenPhim = tenPhim; }

    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }

    public int getThoiLuong() { return thoiLuong; }
    public void setThoiLuong(int thoiLuong) { this.thoiLuong = thoiLuong; }

    public String getNgonNgu() { return ngonNgu; }
    public void setNgonNgu(String ngonNgu) { this.ngonNgu = ngonNgu; }

    public String getQuocGia() { return quocGia; }
    public void setQuocGia(String quocGia) { this.quocGia = quocGia; }

    public int getNamSanXuat() { return namSanXuat; }
    public void setNamSanXuat(int namSanXuat) { this.namSanXuat = namSanXuat; }

    public String getDaoDien() { return daoDien; }
    public void setDaoDien(String daoDien) { this.daoDien = daoDien; }

    public String getDienVien() { return dienVien; }
    public void setDienVien(String dienVien) { this.dienVien = dienVien; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getTrailer() { return trailer; }
    public void setTrailer(String trailer) { this.trailer = trailer; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public int getGioiHanTuoi() { return gioiHanTuoi; }
    public void setGioiHanTuoi(int gioiHanTuoi) { this.gioiHanTuoi = gioiHanTuoi; }

    @Override
    public String toString() {
        return "Phim{" +
                "maPhim=" + maPhim +
                ", tenPhim='" + tenPhim + '\'' +
                ", theLoai='" + theLoai + '\'' +
                ", thoiLuong=" + thoiLuong + " phút" +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}

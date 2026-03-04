package com.rapphim.model;

/**
 * Entity class đại diện cho Nhân Viên.
 */
public class NhanVien {
    private int maNhanVien;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String vaiTro;           // "Admin", "Quản lý", "Thu ngân", "Bảo vệ"
    private String tenDangNhap;
    private String matKhau;          // Lưu dạng hash (bcrypt)
    private String trangThai;        // "Hoạt động", "Nghỉ việc"
    private double luong;
    private String ngayVaoLam;

    // ===== CONSTRUCTORS =====
    public NhanVien() {}

    // ===== GETTERS & SETTERS =====
    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public double getLuong() { return luong; }
    public void setLuong(double luong) { this.luong = luong; }

    public String getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(String ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
}

package com.rapphim.model;

/**
 * Entity class đại diện cho Khách Hàng.
 */
public class KhachHang {
    private int maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String gioiTinh;         // "Nam", "Nữ", "Khác"
    private String ngaySinh;
    private String loaiThanhVien;    // "Thường", "Bạc", "Vàng", "Kim Cương"
    private int diemTichLuy;
    private String trangThai;        // "Hoạt động", "Vô hiệu hóa"

    // ===== CONSTRUCTORS =====
    public KhachHang() {}

    // ===== GETTERS & SETTERS =====
    public int getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(int maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getLoaiThanhVien() { return loaiThanhVien; }
    public void setLoaiThanhVien(String loaiThanhVien) { this.loaiThanhVien = loaiThanhVien; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}

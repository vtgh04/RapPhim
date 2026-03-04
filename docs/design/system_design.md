# 📐 Tài Liệu Thiết Kế Hệ Thống

## Quản Lý Rạp Chiếu Phim

---

## 1. Sơ Đồ Kiến Trúc MVC

```
┌─────────────────────────────────────────┐
│              VIEW LAYER                 │
│  (Java Swing: JFrame, JPanel, JDialog)  │
└──────────────┬──────────────────────────┘
               │ Event / Action
               ▼
┌─────────────────────────────────────────┐
│           CONTROLLER LAYER              │
│  (Xử lý sự kiện, validate, điều phối)  │
└──────────────┬──────────────────────────┘
               │ gọi
               ▼
┌─────────────────────────────────────────┐
│            SERVICE LAYER                │
│  (Business Logic, business rules)       │
└──────────────┬──────────────────────────┘
               │ gọi
               ▼
┌─────────────────────────────────────────┐
│              DAO LAYER                  │
│  (Data Access Object, JDBC queries)     │
└──────────────┬──────────────────────────┘
               │ JDBC
               ▼
┌─────────────────────────────────────────┐
│           DATABASE LAYER                │
│           (MySQL 8.0)                   │
└─────────────────────────────────────────┘
```

---

## 2. Sơ Đồ Cơ Sở Dữ Liệu (ERD Tóm Tắt)

```
PHIM (1) ──────────── (N) LICH_CHIEU
PHONG_CHIEU (1) ───── (N) LICH_CHIEU
PHONG_CHIEU (1) ───── (N) GHE
LICH_CHIEU (1) ─────── (N) VE
GHE (1) ──────────── (N) VE
KHACH_HANG (1) ─────── (N) VE
NHAN_VIEN (1) ──────── (N) VE
VE (1) ─────────────── (N) HOA_DON_DICH_VU
DICH_VU (1) ───────── (N) HOA_DON_DICH_VU
```

---

## 3. Danh Sách Màn Hình (Screens)

| Màn Hình | Mô Tả | Quyền Truy Cập |
|----------|--------|----------------|
| `LoginForm` | Đăng nhập hệ thống | Tất cả |
| `MainFrame` | Khung chính, menu điều hướng | Tất cả |
| `PhimPanel` | Quản lý danh sách phim | Quản lý, Admin |
| `PhimForm` | Thêm/sửa thông tin phim | Quản lý, Admin |
| `LichChieuPanel` | Quản lý lịch chiếu | Quản lý, Admin |
| `DatVePanel` | Bán vé, chọn ghế | Thu ngân |
| `GhePanel` | Sơ đồ ghế ngồi phòng chiếu | Thu ngân |
| `KhachHangPanel` | Quản lý khách hàng | Thu ngân, Admin |
| `NhanVienPanel` | Quản lý nhân viên | Admin |
| `ThongKePanel` | Báo cáo doanh thu | Quản lý, Admin |
| `DichVuPanel` | Quản lý dịch vụ ăn uống | Quản lý, Admin |

---

## 4. Phân Quyền Hệ Thống

| Chức Năng | Admin | Quản Lý | Thu Ngân | Bảo Vệ |
|-----------|:-----:|:-------:|:--------:|:------:|
| Quản lý nhân viên | ✅ | ❌ | ❌ | ❌ |
| Quản lý phim | ✅ | ✅ | ❌ | ❌ |
| Quản lý lịch chiếu | ✅ | ✅ | ❌ | ❌ |
| Bán vé | ✅ | ✅ | ✅ | ❌ |
| Xem báo cáo | ✅ | ✅ | ❌ | ❌ |
| Quản lý khách hàng | ✅ | ✅ | ✅ | ❌ |

---

## 5. Công Nghệ Sử Dụng

| Thành Phần | Thư Viện | Mục Đích |
|-----------|----------|----------|
| UI Framework | Java Swing | Giao diện desktop |
| Look & Feel | FlatLaf 3.x | UI hiện đại |
| Database | MySQL 8.0 + JDBC | Lưu trữ dữ liệu |
| Build | Apache Maven | Quản lý dependencies |
| Export Excel | Apache POI 5.x | Xuất báo cáo Excel |
| Export PDF | iText 5.x | Xuất vé, báo cáo PDF |
| Chart | JFreeChart | Biểu đồ thống kê |
| Testing | JUnit 5 + Mockito | Unit testing |

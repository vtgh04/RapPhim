# RapPhim - Hệ Thống Quản Lý Rạp Chiếu Phim

> **Dự án môn học** | **Java Swing** | **MVC Architecture**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.java.com)
[![Swing](https://img.shields.io/badge/UI-Java%20Swing-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)

[![Maven](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Giới Thiệu

**RapPhim** là ứng dụng desktop quản lý rạp chiếu phim được xây dựng bằng **Java Swing**

### Chức Năng Chính

| Module                  | Mô Tả                                       |
| ----------------------- | --------------------------------------------- |
| Quản lý Phim          | Thêm, sửa, xóa, tìm kiếm thông tin phim |
| Quản lý Phòng Chiếu | Quản lý phòng và sơ đồ ghế ngồi      |
| Lịch Chiếu            | Tạo và quản lý lịch chiếu phim          |
| Bán Vé                | Đặt vé, in vé, quản lý giao dịch       |
| Quản lý Khách Hàng  | Thông tin khách hàng, thẻ thành viên    |
| Quản lý Nhân Viên   | Ca làm việc, phân quyền                   |
| Dịch Vụ Ăn Uống     | Quản lý bắp rang, nước uống             |
| Báo Cáo & Thống Kê  | Doanh thu, xuất Excel/PDF                    |
| Phân Quyền            | Admin, Thu ngân, Quản lý                   |

---

## Kiến Trúc Dự Án

```
RapPhim/
├── src/
│   ├── main/
│   │   ├── java/com/rapphim/
│   │   │   ├── model/          # Các lớp thực thể (Entity)
│   │   │   ├── view/           # Giao diện Java Swing
│   │   │   │   ├── panels/     # Các JPanel chức năng
│   │   │   │   ├── dialogs/    # Hộp thoại popup
│   │   │   │   └── forms/      # Form nhập liệu
│   │   │   ├── controller/     # Xử lý sự kiện, kết nối View & Service
│   │   │   ├── dao/            # Data Access Object (truy xuất CSDL)
│   │   │   ├── service/        # Business Logic
│   │   │   ├── util/           # Tiện ích (DateUtil, FormatUtil...)
│   │   │   └── config/         # Cấu hình DB, hằng số
│   │   └── resources/
│   │       ├── images/         # Hình ảnh, banner phim
│   │       ├── fonts/          # Font chữ tùy chỉnh
│   │       └── config/         # File cấu hình (db.properties)
│   └── test/
│       └── java/com/rapphim/   # Unit Tests
├── lib/                        # Thư viện JAR (JDBC, iText, Apache POI)
├── database/                   # Script SQL (tạo bảng, dữ liệu mẫu)
├── docs/                       # Tài liệu thiết kế, sơ đồ
└── reports/                    # Báo cáo đồ án
```

---

---

## Hướng Dẫn Cài Đặt

### Yêu Cầu Hệ Thống

- Java JDK 17 trở lên
- MySQL Server 8.0 trở lên
- Apache Maven 3.8+
- IDE: IntelliJ IDEA / Eclipse / NetBeans

### Các Bước Cài Đặt

```bash
# 1. Clone repository
git clone https://github.com/YOUR_USERNAME/RapPhim.git
cd RapPhim

# 2. Import database
mysql -u root -p < database/scripts/01_create_database.sql
mysql -u root -p rapphim_db < database/scripts/02_create_tables.sql
mysql -u root -p rapphim_db < database/scripts/03_sample_data.sql

# 3. Cấu hình kết nối database
# Chỉnh sửa file: src/main/resources/config/database.properties

# 4. Build project
mvn clean install

# 5. Chạy ứng dụng
mvn exec:java -Dexec.mainClass="com.rapphim.Main"
```

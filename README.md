# RapPhim - Hệ Thống Quản Lý Rạp Chiếu Phim

> **Dự án môn học** | **Java Swing** | **MVC Architecture** | **SQL Server**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.java.com)
[![Swing](https://img.shields.io/badge/UI-Java%20Swing-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Maven](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/DB-SQL%20Server-red.svg)](https://www.microsoft.com/en-us/sql-server)

## Giới Thiệu

**RapPhim** là ứng dụng desktop quản lý rạp chiếu phim chuyên nghiệp được xây dựng bằng **Java Swing** và cơ sở dữ liệu **Microsoft SQL Server**. Ứng dụng cung cấp các công cụ đầy đủ cho cả nhân viên (Staff) và người quản lý (Manager), từ khâu bán vé POS, quản lý sơ đồ ghế linh hoạt đến thống kê doanh thu.

### Chức Năng Nổi Bật

| Module                  | Mô Tả                                       |
| ----------------------- | --------------------------------------------- |
| **Phân Quyền (Roles)**  | Phân tách luồng giao diện giữa Manager và Staff cực kì trực quan. |
| **Quản lý Phim**        | Thêm, sửa, xóa, tìm kiếm thông tin phim với giao diện Card hiện đại. |
| **Quản lý Phòng Chiếu** | Quản lý sơ đồ ghế ngồi động theo loại phòng (2D, 3D, IMAX) với thuật toán tạo ghế tự động. |
| **Lịch Chiếu**          | Tạo và quản lý lịch chiếu phim, hỗ trợ tự động fill dữ liệu `show_seats`. |
| **Bán Vé (POS)**        | Chọn ghế trực quan, hỗ trợ tính giá ghế VIP/Regular linh hoạt và thanh toán đa dạng. |
| **Giao Dịch (Invoices)**| Quản lý hóa đơn chuyên sâu, tự động update trạng thái ghế sang "BOOKED". |
| **Quản lý Nhân Viên**    | Quản lý tài khoản, thông tin cơ bản và import/export Excel nhanh chóng. |
| **Báo Cáo & Thống Kê**  | Dashboard đồ thị trực quan báo cáo doanh thu, cho phép xuất dữ liệu Excel, vé PDF (iText). |

---

## Kiến Trúc Dự Án

```text
RapPhim/
├── src/
│   ├── main/
│   │   ├── java/com/rapphim/
│   │   │   ├── model/          # Các lớp thực thể (Entity)
│   │   │   ├── view/           # Giao diện Java Swing hiện đại (RoundedPanel, Custom Tokens)
│   │   │   │   ├── panels/     # Các JPanel chức năng phân quyền (GeneralAdmin, GeneralStaff)
│   │   │   │   └── dialogs/    # Hộp thoại popup
│   │   │   ├── controller/     # Xử lý sự kiện, kết nối View & Service
│   │   │   ├── dao/            # Data Access Object (truy xuất SQL Server)
│   │   │   └── config/         # DatabaseConnection (JDBC)
│   │   └── resources/
│   │       └── images/         # Hình ảnh, banner phim, icons
├── lib/                        # Thư viện ngoài (nếu không dùng Maven)
├── database/scripts/           # Script T-SQL: seed.sql (Idempotent schema teardown & creation)
└── pom.xml                     # Quản lý dependency (iText, POI, mssql-jdbc, jbcrypt)
```

---

## Hướng Dẫn Cài Đặt

### Yêu Cầu Hệ Thống

- **Java JDK 17** trở lên
- **Microsoft SQL Server 2019/2022** (hỗ trợ SQL Authentication)
- **Apache Maven 3.8+**
- IDE: IntelliJ IDEA, Eclipse, hoặc VS Code

### Các Bước Cài Đặt

1. **Clone repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/RapPhim.git
   cd RapPhim
   ```

2. **Cài Đặt Database (SQL Server)**
   - Mở SQL Server Management Studio (SSMS) (hoặc Azure Data Studio).
   - Mở file `database/scripts/seed.sql` và chạy toàn bộ (Execute).


3. **Cấu hình kết nối CSDL**
   - Mở file: `src/main/java/com/rapphim/config/DatabaseConnection.java`
   - Điều chỉnh cấu hình `URL`, `USERNAME` và `PASSWORD` cho phù hợp với SQL Server của bạn.

4. **Build & Chạy ứng dụng**
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="com.rapphim.Main"
   ```

5. **Tài khoản đăng nhập mẫu**
   - **Quản lý (Manager):** `manager01` / `123`
   - **Nhân viên (Staff):** `staff01` / `123`

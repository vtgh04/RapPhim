# 📖 Hướng Dẫn Sử Dụng & Phát Triển Dự Án

## Hệ Thống Quản Lý Rạp Chiếu Phim — Java Swing / JavaFX + MySQL + MVC

---

## 📋 MỤC LỤC

1. [Yêu cầu hệ thống](#1-yêu-cầu-hệ-thống)
2. [Cài đặt môi trường](#2-cài-đặt-môi-trường)
3. [Cấu trúc thư mục &amp; vai trò từng layer](#3-cấu-trúc-thư-mục--vai-trò-từng-layer)
4. [Cài đặt và chạy dự án](#4-cài-đặt-và-chạy-dự-án)
5. [Mô hình phân quyền &amp; tài khoản demo](#5-mô-hình-phân-quyền--tài-khoản-demo)
6. [Hướng dẫn bổ sung tính năng mới (MVC + DAO)](#6-hướng-dẫn-bổ-sung-tính-năng-mới-mvc--dao)
7. [Tiêu chuẩn code cho dự án](#7-tiêu-chuẩn-code-cho-dự-án)
8. [Git Workflow cho nhóm](#8-git-workflow-cho-nhóm)
9. [Câu hỏi thường gặp](#9-câu-hỏi-thường-gặp)

---

## 1. Yêu Cầu Hệ Thống

| Thành phần           | Phiên bản | Ghi chú                                           |
| ---------------------- | ----------- | -------------------------------------------------- |
| **Java JDK**     | 17+         | Tải tại[adoptium.net](https://adoptium.net)         |
| **Apache Maven** | 3.8+        | Tải tại[maven.apache.org](https://maven.apache.org) |
| **MySQL Server** | 8.0+        | Tải tại[mysql.com](https://mysql.com)               |
| **IDE**          | Bất kỳ    | IntelliJ IDEA (khuyến nghị) / Eclipse / NetBeans |
| **RAM**          | 4GB+        | 8GB để chạy thoải mái                         |

---

## 2. Cài Đặt Môi Trường

### 2.1 Kiểm tra Java

```bash
java -version
# Yêu cầu: java 17 hoặc cao hơn
```

> Nếu chưa cài: tải JDK 17 tại https://adoptium.net, sau đó đặt biến `JAVA_HOME`.

### 2.2 Kiểm tra Maven

```bash
mvn -version
# Yêu cầu: Apache Maven 3.8+
```

> Nếu chưa cài: tải Maven, giải nén và thêm thư mục `/bin` vào biến `PATH`.

### 2.3 Cài MySQL

Tải MySQL Community Server 8.0: https://dev.mysql.com/downloads/mysql/

---

## 3. Cấu Trúc Thư Mục & Vai Trò Từng Layer

```
RapPhim/
│
├── 📄 pom.xml                         ← Maven: quản lý thư viện
├── 📄 README.md                       ← Tổng quan dự án
│
├── src/main/java/com/rapphim/
│   │
│   ├── 📄 Main.java                   ← ENTRY POINT — chạy ứng dụng
│   │
│   ├── 📁 model/     [LAYER 1 — Entity]
│   │   ├── Movie.java / Phim.java     ← Thực thể phim (movieId, title, genre, durationMins...)
│   │   ├── Showtime.java              ← Suất chiếu (showId, startTime, endTime, basePrice)
│   │   ├── Seat.java / Ghe.java       ← Ghế ngồi (seatId, rowNumber, seatType)
│   │   ├── ShowSeat.java              ← Trạng thái ghế trong suất chiếu (AVAILABLE/PENDING/BOOKED)
│   │   ├── Invoice.java               ← Hóa đơn (invoiceId, totalAmount, paymentStatus)
│   │   ├── Ticket.java / Ve.java      ← Vé (ticketId, barcode, finalPrice)
│   │   ├── Employee.java / NhanVien.java  ← Nhân viên (accountId, role, passwordHash)
│   │   └── KhachHang.java        SSS     ← Khách hàng
│   │
│   ├── 📁 dao/       [LAYER 2 — Data Access]
│   │   ├── GenericDAO.java            ← Interface CRUD chung (insert/update/delete/findById/findAll)
│   │   ├── PhimDAO.java               ← Truy xuất bảng phim (JDBC + SQL)
│   │   ├── LichChieuDAO.java          ← (cần tạo) Truy xuất bảng showtimes
│   │   └── VeDAO.java                 ← (cần tạo) Truy xuất bảng tickets/invoices
│   │
│   ├── 📁 service/   [LAYER 3 — Business Logic]
│   │   ├── PhimService.java           ← Validate + gọi DAO
│   │   ├── BanVeService.java          ← (cần tạo) Logic bán vé, kiểm soát đồng thời (HOLD → BOOKED)
│   │   └── BaoCaoService.java         ← (cần tạo) Logic thống kê, SQL aggregation
│   │
│   ├── 📁 controller/ [LAYER 4 — Điều phối]
│   │   ├── PhimController.java        ← Kết nối PhimPanel ↔ PhimService
│   │   └── POSController.java         ← (cần tạo) Kết nối POSPanel ↔ BanVeService
│   │
│   ├── 📁 view/      [LAYER 5 — Giao Diện Swing]
│   │   ├── 📁 panels/
│   │   │   ├── MainFrame.java         ← Cửa sổ chính (JFrame + JTabbedPane/CardLayout)
│   │   │   ├── MoviePanel.java        ← Quản lý phim (JTable + JToolBar + search)
│   │   │   ├── POSPanel.java          ← Bán vé (Seat Map Grid — JToggleButton)
│   │   │   └── RevenuePanel.java      ← Thống kê (JFreeChart biểu đồ doanh thu)
│   │   ├── 📁 dialogs/                ← JDialog popup (thêm/sửa, xác nhận)
│   │   └── 📁 forms/                  ← JPanel form nhập liệu
│   │
│   ├── 📁 config/
│   │   └── DatabaseConnection.java    ← Singleton JDBC — kết nối MySQL
│   │
│   └── 📁 util/
│       └── Utils.java                 ← Format tiền (VND), ngày, validate email/SĐT
│
├── src/main/resources/
│   └── config/
│       └── database.properties        ← ⚠️ Không push Git! Tạo thủ công từ .example
│
├── src/test/java/                     ← JUnit 5 Unit Tests
│
├── database/scripts/
│   ├── 01_create_database.sql         ← Tạo database rapphim_db
│   ├── 02_create_tables.sql           ← Tạo 8 bảng + ràng buộc + cascade
│   └── 03_sample_data.sql             ← Dữ liệu mẫu (phim, nhân viên, khách hàng)
│
├── docs/
│   ├── design/system_design.md        ← Báo cáo phân tích thiết kế (5 phần)
│   └── user-guide/huong_dan_su_dung.md ← 📍 File này
│
└── reports/                           ← Nộp báo cáo Word/PDF + slides + screenshots
```

---

## 4. Cài Đặt và Chạy Dự Án

### Bước 1 — Clone code về máy

```bash
git clone https://github.com/vtgh04/RapPhim.git
cd RapPhim
```

### Bước 2 — Import database

```bash
# Mở MySQL CLI hoặc MySQL Workbench, chạy lần lượt:
mysql -u root -p < database/scripts/01_create_database.sql
mysql -u root -p rapphim_db < database/scripts/02_create_tables.sql
mysql -u root -p rapphim_db < database/scripts/03_sample_data.sql
```

### Bước 3 — Tạo file cấu hình database

```bash
# Windows
copy src\main\resources\config\database.properties.example ^
     src\main\resources\config\database.properties
```

Mở `database.properties` và sửa:

```properties
db.url=jdbc:mysql://localhost:3306/rapphim_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
db.username=root
db.password=matkhau_mysql_cua_ban
```

> ⚠️ File `database.properties` đã được `.gitignore` — **KHÔNG push lên GitHub!**

### Bước 4 — Build bằng Maven

```bash
mvn clean install -DskipTests
# Kết quả: BUILD SUCCESS ✅
```

### Bước 5 — Chạy ứng dụng

```bash
# Cách 1: Maven
mvn exec:java -Dexec.mainClass="com.rapphim.Main"

# Cách 2: Chạy JAR
java -jar target/RapPhim-1.0.0.jar
```

---

## 5. Mô Hình Phân Quyền & Tài Khoản Demo

| Vai Trò            | Đăng Nhập  | Mật Khẩu      | Quyền Hạn                                                               |
| ------------------- | ------------- | --------------- | ------------------------------------------------------------------------- |
| **Admin**     | `admin`     | `Admin@123`   | Toàn quyền: quản lý nhân viên, cấu hình hệ thống, xem báo cáo |
| **Quản lý** | `quanly01`  | `Quanly@123`  | Quản lý phim, phòng, lịch chiếu, xem báo cáo                       |
| **Thu ngân** | `thungan01` | `Thungan@123` | Bán vé (POS), in hóa đơn, tra cứu khách hàng                      |

**Phân quyền hệ thống:**

| Chức Năng            | Admin | Quản Lý | Thu Ngân |
| ---------------------- | :---: | :-------: | :-------: |
| Quản lý nhân viên  |  ✅  |    ❌    |    ❌    |
| Quản lý phim/phòng  |  ✅  |    ✅    |    ❌    |
| Lịch chiếu           |  ✅  |    ✅    |    ❌    |
| Bán vé (POS)         |  ✅  |    ✅    |    ✅    |
| Hủy vé / Hoàn tiền |  ✅  |    ✅    |    ❌    |
| Báo cáo doanh thu    |  ✅  |    ✅    |    ❌    |

---

## 6. Hướng Dẫn Bổ Sung Tính Năng Mới (MVC + DAO)

**Ví dụ: Thêm module "Quản lý Phòng Chiếu"**

### Bước 1 — Tạo Model

```java
// src/main/java/com/rapphim/model/PhongChieu.java
public class PhongChieu {
    private String hallId;
    private String name;
    private int totalRows, totalCols;
    private boolean isActive;
    // getters / setters...
}
```

### Bước 2 — Tạo DAO

```java
// src/main/java/com/rapphim/dao/PhongChieuDAO.java
public class PhongChieuDAO implements GenericDAO<PhongChieu, String> {
    // Implement: insert, update, delete, findById, findAll
    // Dùng DatabaseConnection.getInstance().getConnection()
}
```

### Bước 3 — Tạo Service

```java
// src/main/java/com/rapphim/service/PhongChieuService.java
public class PhongChieuService {
    private PhongChieuDAO dao = new PhongChieuDAO();

    public boolean themPhong(PhongChieu phong) {
        if (phong.getName() == null || phong.getName().isBlank())
            throw new IllegalArgumentException("Tên phòng không được để trống!");
        return dao.insert(phong);
    }
}
```

### Bước 4 — Tạo Controller

```java
// Kết nối View ↔ Service, đăng ký listener cho các button
public class PhongChieuController {
    private PhongChieuPanel view;
    private PhongChieuService service = new PhongChieuService();

    public void init() {
        view.getBtnThem().addActionListener(e -> handleThem());
    }
}
```

### Bước 5 — Tạo View (Swing Panel)

```java
// src/main/java/com/rapphim/view/panels/PhongChieuPanel.java
public class PhongChieuPanel extends JPanel {
    private JTable table;
    private JButton btnThem, btnSua, btnXoa;
    // Phím tắt: F2 = Thêm, Delete = Xóa, Ctrl+E = Sửa
}
```

---

## 7. Tiêu Chuẩn Code Cho Dự Án

Tuân thủ **Java Coding Conventions** (Oracle) + **Google Java Style Guide**:

| Đối tượng | Chuẩn                 | Ví dụ đúng          | Ví dụ sai         |
| ------------- | ---------------------- | ----------------------- | ------------------- |
| Package       | lowercase              | `com.rapphim.dao`     | `com.RapPhim.DAO` |
| Class         | PascalCase             | `PhongChieuDAO`       | `phongchieuDAO`   |
| Method        | camelCase (động từ) | `getAvailableSeats()` | `Ghetrong()`      |
| Variable      | camelCase (danh từ)   | `totalPrice`          | `tp`, `x`       |
| Constant      | UPPER_SNAKE_CASE       | `MAX_SEAT_COUNT`      | `MaxSeat`         |

**Javadoc bắt buộc cho mọi public method:**

```java
/**
 * Tìm tất cả ghế còn trống trong một suất chiếu.
 *
 * @param showId  Mã suất chiếu cần tra cứu
 * @return        Danh sách ghế có status = AVAILABLE
 * @throws        ShowNotFoundException nếu showId không tồn tại
 */
public List<ShowSeat> getAvailableSeats(String showId) { ... }
```

**Ràng buộc format:**

- Thụt lề: **4 spaces** (không dùng Tab)
- Dấu `{` cuối dòng khai báo (K&R style)
- Import đích danh, không dùng `import java.util.*;`

---

## 8. Git Workflow Cho Nhóm

### Quy tắc nhánh

```
main           ← Nhánh chính, chỉ merge code hoàn chỉnh
feature/ten    ← Tính năng mới: feature/ban-ve-pos
bugfix/ten     ← Sửa lỗi: bugfix/loi-cascade-delete
```

### Quy trình hàng ngày

```bash
# 1. Lấy code mới nhất
git pull origin main

# 2. Tạo nhánh riêng
git checkout -b feature/quan-ly-phong-chieu

# 3. Code xong → commit
git add .
git commit -m "feat: Thêm module Quản lý Phòng Chiếu (PhongChieuDAO + Panel)"

# 4. Push nhánh lên GitHub
git push origin feature/quan-ly-phong-chieu

# 5. Tạo Pull Request trên GitHub → merge vào main
```

### Chuẩn commit message

```
feat:     Tính năng mới
fix:      Sửa lỗi
refactor: Cải thiện code (không đổi chức năng)
docs:     Cập nhật tài liệu
test:     Thêm/sửa unit test
sql:      Thay đổi database schema/script
```

### Gợi ý phân chia theo nhóm

| Thành Viên | Phụ Trách                                         |
| ------------ | --------------------------------------------------- |
| TV1          | Model, DAO, DatabaseConnection, SQL scripts         |
| TV2          | Service, Controller, Business Logic                 |
| TV3          | View — Swing UI (JFrame, JPanel, JTable, Seat Map) |
| TV4          | Báo cáo PDF/Excel, Thống kê, Tài liệu, Slides |

---

## 9. Câu Hỏi Thường Gặp

### ❓ Lỗi "Communications link failure" khi kết nối DB

**Nguyên nhân:** MySQL chưa chạy hoặc sai thông tin trong `database.properties`.**Giải pháp:**

1. Vào `Services → MySQL80 → Start`
2. Kiểm tra `db.username`, `db.password`, `db.url`

### ❓ Lỗi "Table doesn't exist"

**Giải pháp:** Chạy lại script SQL:

```bash
mysql -u root -p rapphim_db < database/scripts/02_create_tables.sql
```

### ❓ Hai nhân viên bán cùng một ghế

**Không xảy ra** — Ràng buộc `UNIQUE(show_id, seat_id)` trong bảng TICKETS và cơ chế `HOLD` ở tầng Service ngăn chặn race condition.

### ❓ Git báo "rejected" khi push

```bash
git pull origin main --rebase
git push origin main
```

### ❓ Sơ đồ ghế bị vỡ layout khi resize

Dùng `GridBagLayout` hoặc `BorderLayout` thay vì `AbsoluteLayout (null)`.

---

## 🔗 Liên Kết Nhanh

| Tài Nguyên          | Đường Dẫn                                                                     |
| --------------------- | --------------------------------------------------------------------------------- |
| GitHub Repository     | [github.com/vtgh04/RapPhim](https://github.com/vtgh04/RapPhim)                       |
| Thiết kế hệ thống | [docs/design/system_design.md](../design/system_design.md)                           |
| Schema DB             | [database/scripts/02_create_tables.sql](../../database/scripts/02_create_tables.sql) |
| Dữ liệu mẫu        | [database/scripts/03_sample_data.sql](../../database/scripts/03_sample_data.sql)     |

---

*Cập nhật lần cuối: 04/03/2026*

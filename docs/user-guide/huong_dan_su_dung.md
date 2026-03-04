# 📖 Hướng Dẫn Sử Dụng Dự Án - RapPhim

> **Hệ thống Quản lý Rạp Chiếu Phim** | Java Swing + MySQL + MVC | Đồ án sinh viên

---

## 📋 MỤC LỤC

1. [Yêu cầu hệ thống](#1-yêu-cầu-hệ-thống)
2. [Cài đặt môi trường](#2-cài-đặt-môi-trường)
3. [Cấu trúc thư mục](#3-cấu-trúc-thư-mục)
4. [Cài đặt và chạy dự án](#4-cài-đặt-và-chạy-dự-án)
5. [Cấu hình Database](#5-cấu-hình-database)
6. [Hướng dẫn code theo từng layer](#6-hướng-dẫn-code-theo-từng-layer)
7. [Workflow làm việc nhóm với Git](#7-workflow-làm-việc-nhóm-với-git)
8. [Câu hỏi thường gặp (FAQ)](#8-câu-hỏi-thường-gặp-faq)

---

## 1. Yêu Cầu Hệ Thống

| Thành phần | Phiên bản tối thiểu | Ghi chú |
|-----------|---------------------|---------|
| **Java JDK** | 17+ | Tải tại [adoptium.net](https://adoptium.net) |
| **Apache Maven** | 3.8+ | Tải tại [maven.apache.org](https://maven.apache.org) |
| **MySQL Server** | 8.0+ | Tải tại [mysql.com](https://mysql.com) |
| **IDE** | Bất kỳ | IntelliJ IDEA (khuyến nghị), Eclipse, NetBeans |
| **RAM** | 4GB+ | 8GB để chạy thoải mái |
| **OS** | Windows 10/11, macOS, Linux | |

---

## 2. Cài Đặt Môi Trường

### 2.1 Cài Java JDK 17

```bash
# Kiểm tra Java đã cài chưa
java -version

# Nếu chưa cài: tải JDK 17 tại
# https://adoptium.net/temurin/releases/?version=17
```

> ⚠️ Sau khi cài, đặt biến môi trường `JAVA_HOME` trỏ đến thư mục JDK.

### 2.2 Cài Apache Maven

```bash
# Kiểm tra Maven đã cài chưa
mvn -version

# Nếu chưa cài: tải Maven tại
# https://maven.apache.org/download.cgi
# Giải nén → thêm thư mục /bin vào biến PATH
```

### 2.3 Cài MySQL Server

```bash
# Tải MySQL Community Server 8.0 tại
# https://dev.mysql.com/downloads/mysql/

# Sau khi cài, mở MySQL Workbench hoặc dùng CLI:
mysql -u root -p
```

---

## 3. Cấu Trúc Thư Mục

```
RapPhim/
│
├── 📄 README.md                          ← Tổng quan dự án
├── 📄 pom.xml                            ← Maven: quản lý thư viện
├── 📄 .gitignore                         ← File không push lên Git
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/rapphim/
│   │   │   │
│   │   │   ├── 📄 Main.java             ← ENTRY POINT - chạy ứng dụng ở đây
│   │   │   │
│   │   │   ├── 📁 model/               ← [LAYER 1] Lớp Entity (dữ liệu)
│   │   │   │   ├── Phim.java           ← Thực thể phim
│   │   │   │   ├── Ve.java             ← Thực thể vé
│   │   │   │   ├── KhachHang.java      ← Thực thể khách hàng
│   │   │   │   ├── NhanVien.java       ← Thực thể nhân viên
│   │   │   │   ├── PhongChieu.java     ← (cần tạo) Phòng chiếu
│   │   │   │   ├── Ghe.java            ← (cần tạo) Ghế ngồi
│   │   │   │   └── LichChieu.java      ← (cần tạo) Lịch chiếu
│   │   │   │
│   │   │   ├── 📁 dao/                 ← [LAYER 2] Truy xuất Database
│   │   │   │   ├── GenericDAO.java     ← Interface CRUD chung
│   │   │   │   ├── PhimDAO.java        ← CRUD cho bảng phim
│   │   │   │   ├── VeDAO.java          ← (cần tạo) CRUD cho bảng ve
│   │   │   │   ├── KhachHangDAO.java   ← (cần tạo) CRUD cho khach_hang
│   │   │   │   └── NhanVienDAO.java    ← (cần tạo) CRUD cho nhan_vien
│   │   │   │
│   │   │   ├── 📁 service/             ← [LAYER 3] Business Logic
│   │   │   │   ├── PhimService.java    ← Logic nghiệp vụ phim
│   │   │   │   ├── VeService.java      ← (cần tạo) Logic bán vé
│   │   │   │   └── BaoCaoService.java  ← (cần tạo) Logic báo cáo
│   │   │   │
│   │   │   ├── 📁 controller/          ← [LAYER 4] Kết nối View ↔ Service
│   │   │   │   ├── PhimController.java ← (cần tạo)
│   │   │   │   └── VeController.java   ← (cần tạo)
│   │   │   │
│   │   │   ├── 📁 view/                ← [LAYER 5] Giao Diện Java Swing
│   │   │   │   ├── 📁 panels/          ← Các màn hình chính
│   │   │   │   │   ├── MainFrame.java  ← (cần tạo) Cửa sổ chính
│   │   │   │   │   ├── PhimPanel.java  ← (cần tạo) Màn hình quản lý phim
│   │   │   │   │   └── BanVePanel.java ← (cần tạo) Màn hình bán vé
│   │   │   │   ├── 📁 dialogs/         ← Hộp thoại popup
│   │   │   │   └── 📁 forms/           ← Form nhập liệu
│   │   │   │
│   │   │   ├── 📁 config/              ← Cấu hình
│   │   │   │   └── DatabaseConnection.java ← Singleton kết nối MySQL
│   │   │   │
│   │   │   └── 📁 util/                ← Tiện ích dùng chung
│   │   │       └── Utils.java          ← Format tiền, ngày, validate
│   │   │
│   │   └── 📁 resources/
│   │       ├── 📁 images/              ← Hình ảnh, poster phim, icon
│   │       ├── 📁 fonts/               ← Font chữ tùy chỉnh
│   │       └── 📁 config/
│   │           └── database.properties ← ⚠️ Cần tạo thủ công (không push Git)
│   │
│   └── 📁 test/java/com/rapphim/       ← Unit Tests (JUnit 5)
│
├── 📁 database/
│   └── 📁 scripts/
│       ├── 01_create_database.sql      ← Bước 1: Tạo database
│       ├── 02_create_tables.sql        ← Bước 2: Tạo các bảng
│       └── 03_sample_data.sql          ← Bước 3: Dữ liệu mẫu
│
├── 📁 docs/
│   ├── 📁 design/
│   │   └── system_design.md            ← Kiến trúc hệ thống, ERD, phân quyền
│   ├── 📁 user-guide/
│   │   └── huong_dan_su_dung.md        ← 📍 File này
│   ├── 📁 diagrams/                    ← Đặt file ảnh sơ đồ UML, ERD vào đây
│   └── 📁 api/                         ← Tài liệu API (nếu có)
│
└── 📁 reports/
    ├── 📁 baocao/                       ← Đặt file Word/PDF báo cáo đồ án
    ├── 📁 slides/                       ← File PowerPoint thuyết trình
    └── 📁 screenshots/                  ← Ảnh chụp màn hình demo
```

---

## 4. Cài Đặt và Chạy Dự Án

### Bước 1 — Clone (hoặc Pull) code về máy

```bash
# Clone lần đầu
git clone https://github.com/vtgh04/RapPhim.git
cd RapPhim

# Hoặc pull code mới nhất (khi đã clone rồi)
git pull origin main
```

### Bước 2 — Import database

Mở MySQL Workbench hoặc terminal MySQL, chạy lần lượt 3 file SQL:

```bash
# Mở MySQL CLI
mysql -u root -p

# Chạy từng file
mysql -u root -p < database/scripts/01_create_database.sql
mysql -u root -p rapphim_db < database/scripts/02_create_tables.sql
mysql -u root -p rapphim_db < database/scripts/03_sample_data.sql
```

### Bước 3 — Tạo file cấu hình database

```bash
# Copy file mẫu và đặt tên mới
cp src/main/resources/config/database.properties.example src/main/resources/config/database.properties
```

Sau đó mở file `database.properties` và sửa thông tin đăng nhập MySQL:

```properties
db.url=jdbc:mysql://localhost:3306/rapphim_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
db.username=root
db.password=matkhau_cua_ban
```

> ⚠️ **Quan trọng:** File `database.properties` đã được thêm vào `.gitignore`. **ĐỪNG bao giờ push file này lên GitHub!**

### Bước 4 — Build dự án bằng Maven

```bash
# Tải dependencies và build
mvn clean install -DskipTests

# Kết quả: "BUILD SUCCESS" ✅
```

### Bước 5 — Chạy ứng dụng

```bash
# Cách 1: Dùng Maven
mvn exec:java -Dexec.mainClass="com.rapphim.Main"

# Cách 2: Chạy file JAR (sau khi build)
java -jar target/RapPhim-1.0.0.jar
```

---

## 5. Cấu Hình Database

### Thông tin kết nối mặc định

```
Host:     localhost
Port:     3306
Database: rapphim_db
Username: root
Password: (của bạn)
```

### Tài khoản đăng nhập demo (sau khi chạy 03_sample_data.sql)

| Vai Trò | Tên đăng nhập | Mật khẩu |
|---------|--------------|---------|
| Admin | `admin` | `Admin@123` |
| Quản lý | `quanly01` | `Quanly@123` |
| Thu ngân | `thungan01` | `Thungan@123` |

---

## 6. Hướng Dẫn Code Theo Từng Layer

### 🔷 Khi thêm một tính năng mới (ví dụ: Phòng chiếu)

**Bước 1: Tạo Model** `src/main/java/com/rapphim/model/PhongChieu.java`
```java
public class PhongChieu {
    private int maPhong;
    private String tenPhong;
    // ... getters/setters
}
```

**Bước 2: Tạo DAO** `src/main/java/com/rapphim/dao/PhongChieuDAO.java`
```java
public class PhongChieuDAO implements GenericDAO<PhongChieu, Integer> {
    // Implement: insert, update, delete, findById, findAll
}
```

**Bước 3: Tạo Service** `src/main/java/com/rapphim/service/PhongChieuService.java`
```java
public class PhongChieuService {
    private PhongChieuDAO dao = new PhongChieuDAO();
    // Business logic, validation
}
```

**Bước 4: Tạo View (Swing Panel)** `src/main/java/com/rapphim/view/panels/PhongChieuPanel.java`
```java
public class PhongChieuPanel extends JPanel {
    // JTable, JButton, JTextField...
}
```

**Bước 5: Tạo Controller** `src/main/java/com/rapphim/controller/PhongChieuController.java`
```java
public class PhongChieuController {
    private PhongChieuPanel view;
    private PhongChieuService service;
    // Kết nối sự kiện View với Service
}
```

---

## 7. Workflow Làm Việc Nhóm Với Git

### 7.1 Quy tắc đặt tên nhánh (Branch)

```
main          ← Nhánh chính, chỉ merge code hoàn chỉnh
feature/ten   ← Tính năng mới, ví dụ: feature/quan-ly-phim
bugfix/ten    ← Sửa lỗi, ví dụ: bugfix/loi-dang-nhap
```

### 7.2 Quy trình làm việc hàng ngày

```bash
# 1. Lấy code mới nhất từ GitHub
git pull origin main

# 2. Tạo nhánh riêng để làm tính năng của bạn
git checkout -b feature/quan-ly-ve

# 3. Code xong → Stage và Commit
git add .
git commit -m "feat: Thêm màn hình quản lý vé - PhongChieuPanel"

# 4. Push nhánh của bạn lên GitHub
git push origin feature/quan-ly-ve

# 5. Tạo Pull Request trên GitHub để merge vào main
```

### 7.3 Quy tắc viết commit message

```
feat:     Tính năng mới
fix:      Sửa lỗi
refactor: Cải thiện code không thay đổi chức năng
docs:     Cập nhật tài liệu
style:    Thay đổi giao diện, CSS, Swing UI
test:     Thêm/sửa test
chore:    Cài đặt, cấu hình build

Ví dụ:
git commit -m "feat: Thêm chức năng đặt vé online"
git commit -m "fix: Sửa lỗi không lưu được thông tin khách hàng"
git commit -m "docs: Cập nhật hướng dẫn cài đặt"
```

### 7.4 Phân chia công việc gợi ý

| Thành Viên | Module Phụ Trách |
|-----------|----------------|
| Thành viên 1 | Model, DAO, DatabaseConnection |
| Thành viên 2 | Service, Controller |
| Thành viên 3 | View (Swing UI), Giao diện |
| Thành viên 4 | Database SQL, Báo cáo, Docs |

---

## 8. Câu Hỏi Thường Gặp (FAQ)

### ❓ Lỗi "Cannot find symbol" khi build

**Nguyên nhân:** Chưa import đúng thư viện hoặc sai package.  
**Giải pháp:**
```bash
mvn clean install
```

### ❓ Lỗi kết nối Database "Communications link failure"

**Nguyên nhân:** MySQL chưa chạy hoặc sai host/port.  
**Giải pháp:**
1. Kiểm tra MySQL đang chạy: `Services → MySQL80 → Start`
2. Kiểm tra lại `database.properties`

### ❓ Lỗi "Table 'rapphim_db.phim' doesn't exist"

**Nguyên nhân:** Chưa chạy script tạo bảng.  
**Giải pháp:**
```bash
mysql -u root -p rapphim_db < database/scripts/02_create_tables.sql
```

### ❓ Git báo lỗi "rejected" khi push

**Nguyên nhân:** Có người khác đã push code mới.  
**Giải pháp:**
```bash
git pull origin main --rebase
git push origin main
```

### ❓ Màn hình Swing bị vỡ layout khi thay đổi kích thước

**Giải pháp:** Dùng `BorderLayout` hoặc `GridBagLayout` thay vì `AbsoluteLayout`.

---

## 📞 Liên Hệ Nhóm

> Cập nhật thông tin nhóm vào đây:

- **Nhóm:** [Tên nhóm]
- **Lớp:** [Tên lớp]
- **GVHD:** [Tên giảng viên]
- **Repository:** [https://github.com/vtgh04/RapPhim](https://github.com/vtgh04/RapPhim)

---

*Tài liệu cập nhật lần cuối: 04/03/2026*

# 📐 Tài Liệu Thiết Kế Hệ Thống

## Báo Cáo Phân Tích, Thiết Kế và Hiện Thực — Quản Lý Bán Vé Rạp Chiếu Phim

> **Ngôn ngữ:** Java (Swing/JavaFX) | **Kiến trúc:** MVC + DAO | **CSDL: SQL Server**

---

## Phần 1: Thu Thập và Phân Tích Yêu Cầu

### 1.1 Bối Cảnh & Phạm Vi Hệ Thống

Hệ thống quản lý bán vé rạp chiếu phim là **ứng dụng desktop (POS)** dành riêng cho nhân viên nội bộ, loại bỏ hoàn toàn giao diện tự phục vụ của khách hàng.

### 1.2 Phân Quyền Người Dùng

| Vai Trò                                              | Trách Nhiệm                                                                                                     |
| ----------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| **Quản trị viên**                            | Khai báo phim, thiết lập phòng chiếu, lên lịch suất chiếu, quản lý giá vé, xem báo cáo tài chính |
| **Nhân viên bán vé (Front Desk / Cashier)** | Tra cứu lịch chiếu, tư vấn chọn ghế, lập hóa đơn, thu tiền, in vé cứng                              |

### 1.3 Các Phân Hệ Chức Năng Cốt Lõi

| Phân Hệ Nghiệp Vụ                   | Khả Năng Cốt Lõi                | Yêu Cầu Kỹ Thuật Chi Tiết                                                                                                                                                |
| --------------------------------------- | ----------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Quản trị Danh mục (CRUD)**   | Thêm, Xóa, Cập nhật, Liệt kê  | Hiển thị Grid View + Detail Form. Xóa xếp tầng (Cascade Delete) cho dữ liệu phụ thuộc; Xóa mềm (Soft Delete) cho thực thể đã phát sinh giao dịch tài chính |
| **Tìm kiếm và Lọc Dữ liệu** | Tra cứu đơn giản và nâng cao  | Tìm kiếm đơn theo ID/tên/SĐT. Advanced Search: kết hợp đa tiêu chí qua Criteria Builder (thể loại, khung giờ, % ghế trống)                                    |
| **Xử lý Giao dịch (POS)**      | Đặt ghế, Lập Hóa đơn, In Vé | Sơ đồ ghế real-time. Tự động tính tổng tiền, thuế. Xuất PDF hoặc in nhiệt trực tiếp                                                                           |
| **Phân tích và Thống kê**    | Báo cáo Doanh thu, Hiệu suất    | Báo cáo theo ngày/tháng. Tỷ lệ lấp đầy phòng. Xếp hạng phim lợi nhuận cao. Biểu đồ đồ họa                                                                 |

### 1.4 Yêu Cầu Phi Chức Năng & UI/UX

**Hệ thống phím tắt (Keyboard Shortcuts):**

| Phím Tắt | Chức Năng                            |
| ---------- | -------------------------------------- |
| `F2`     | Mở nhanh cửa sổ thanh toán         |
| `Ctrl+F` | Kích hoạt thanh tìm kiếm           |
| `Alt+S`  | Xác nhận lưu dữ liệu              |
| `Escape` | Hủy giao dịch hiện tại             |
| `Tab`    | Điều hướng giữa các thành phần |
| `Alt+T`  | Nhấn nút Thanh Toán                 |

**Quy tắc nghiệp vụ bắt buộc:**

1. **Kiểm soát đồng thời (Concurrency):** Ghế khi được click → chuyển sang `HOLD` ngay trên toàn hệ thống, sau đó → `BOOKED` khi thanh toán hoàn tất
2. **Ràng buộc thời gian:** Hệ thống tự động cộng thêm **15 phút dọn dẹp** sau khi phim kết thúc trước khi cho phép lịch tiếp theo
3. **Phân quyền tài chính:** Chỉ Quản lý được phê duyệt hủy vé/hoàn tiền vượt hạn mức; biên lai chỉ xuất khi trạng thái thanh toán ghi nhận thành công

---

## Phần 2: Thiết Kế Sơ Đồ Lớp (Class Diagram)

### 2.1 Cấu Trúc Các Lớp Thực Thể Cốt Lõi

| Tên Lớp                  | Thuộc Tính Chính                                                                                 | Trách Nhiệm & Phương Thức                                                                                                                      |
| -------------------------- | --------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Movie**            | `movieId`, `title`, `genre`, `durationMins`, `language`, `releaseDate`                  | Lưu siêu dữ liệu phim.`displayMovieDetails()`. `durationMins` dùng tính toán chống chồng lấp lịch chiếu                             |
| **CinemaHall**       | `hallId`, `name`, `totalCapacity`                                                             | Đại diện không gian vật lý phòng chiếu.`getAvailableSeats()`, `displayHallLayout()`                                                     |
| **Seat**             | `seatId`, `rowNumber:char`, `seatNumber:int`, `seatType:SeatType`                           | Vị trí vật lý cố định. Enum `SeatType`: `REGULAR`, `VIP`, `ACCESSIBLE`                                                               |
| **Showtime**         | `showId`, `startTime:LocalDateTime`, `endTime:LocalDateTime`, `basePrice:double`            | Kết nối Movie ↔ CinemaHall.`calculateEndTime()` = startTime + durationMins + 15 phút                                                          |
| **ShowSeat**         | `showSeatId`, `price:double`, `status:SeatStatus`                                             | Trạng thái real-time của ghế trong suất chiếu. Enum `SeatStatus`: `AVAILABLE`, `PENDING`, `BOOKED`. `bookSeat()`, `releaseSeat()` |
| **Account/Employee** | `accountId`, `username`, `passwordHash`, `status:AccountStatus`                             | Lớp abstract quản lý định danh. Kế thừa:`Admin`, `FrontDeskOfficer`. Enum `AccountStatus`: `ACTIVE`, `CLOSED`, `BLACKLISTED`     |
| **Invoice**          | `invoiceId`, `totalAmount:double`, `issueDate:LocalDateTime`, `paymentStatus:PaymentStatus` | Trái tim luồng tài chính.`calculateTotal()`. Enum `PaymentStatus`: `PENDING`, `CONFIRMED`, `CANCELLED`                                |
| **Ticket**           | `ticketId`, `barcode:String`                                                                    | Đại diện quyền lợi vào cổng của khách.`printTicket()` → xuất PDF                                                                       |

### 2.2 Đặc Tả Mối Quan Hệ UML

| Quan Hệ                             | Cặp Lớp                        | Mô Tả                                                   |
| ------------------------------------ | -------------------------------- | --------------------------------------------------------- |
| **Composition (Thành phần)** | `CinemaHall` ◆→ `Seat`     | Phòng chiếu bị xóa → toàn bộ ghế xóa theo        |
| **Composition (Thành phần)** | `Invoice` ◆→ `Ticket`      | Vé không tồn tại độc lập ngoài hóa đơn         |
| **Aggregation (Tập hợp)**    | `TheatreSystem` ◇→ `Movie` | Phim bị gỡ, hệ thống rạp vẫn hoạt động           |
| **Aggregation (Tập hợp)**    | `CinemaHall` ◇→ `Showtime` | Hủy suất chiếu, phòng chiếu vẫn tồn tại           |
| **Association (Liên kết)**   | `Employee` → `Invoice`      | Nhân viên tạo hóa đơn (ghi employeeId vào invoice) |

**Ràng buộc đối tượng:**

- `bookSeat()` chỉ thành công khi `status == AVAILABLE`; luồng đến sau sẽ bị ném ngoại lệ
- In vé & chuyển ghế sang `BOOKED` chỉ xảy ra khi `PaymentStatus == CONFIRMED`
- Mọi sai lệch → kích hoạt **Rollback**, trả ghế về `AVAILABLE`

---

## Phần 3: Thiết Kế Cơ Sở Dữ Liệu

### 3.1 Sơ Đồ ERD

```
MOVIES (1) ──────────── (N) SHOWTIMES
HALLS (1) ───────────── (N) SHOWTIMES
HALLS (1) ───────────── (N) SEATS
SHOWTIMES (1) ────────── (N) TICKETS
SEATS (1) ─────────────── (N) TICKETS
EMPLOYEES (1) ──────────── (N) INVOICES
INVOICES (1) ──── (N) TICKETS
```

### 3.2 Từ Điển Dữ Liệu (Data Dictionary)

| Bảng               | Khóa                                                                       | Cấu Trúc Cột & Ý Nghĩa Kỹ Thuật                                                                                        |
| ------------------- | --------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| **MOVIES**    | `movie_id (PK, VARCHAR)`                                                  | Cột `duration_mins (INT) > 0`. Index trên `title`, `genre`, `release_date`                                          |
| **HALLS**     | `hall_id (PK, VARCHAR)`                                                   | `total_rows (INT)`, `total_cols (INT)` — dựng lưới tọa độ. `is_active (BOOLEAN)` — vô hiệu hóa khi bảo trì |
| **SEATS**     | `seat_id (PK)`, `hall_id (FK)`                                          | `row_char (CHAR)`, `col_number (INT)`, `seat_type (VARCHAR)`. Liên kết N-1 về HALLS                                  |
| **SHOWTIMES** | `show_id (PK)`, `movie_id (FK)`, `hall_id (FK)`                       | `start_time (DATETIME)`, `end_time (DATETIME)`, `base_price (DECIMAL 10,2)` — giá gốc                                |
| **EMPLOYEES** | `emp_id (PK)`                                                             | `password_hash (VARCHAR)` — BCrypt. `role (VARCHAR)` — phân quyền                                                     |
| **INVOICES**  | `invoice_id (PK)`, `emp_id (FK)`                                        | `created_at (DATETIME DEFAULT CURRENT_TIMESTAMP)`, `total_amount (DECIMAL 12,2)`, `status (VARCHAR)`                    |
| **TICKETS**   | `ticket_id (PK)`, `invoice_id (FK)`, `show_id (FK)`, `seat_id (FK)` | `final_price (DECIMAL)` — giá bán thực tế sau khuyến mãi (**Snapshot Data**). `UNIQUE(show_id, seat_id)`     |

### 3.3 Cơ Chế Toàn Vẹn Dữ Liệu & Cascade

**Cascade Update:** Khi `hall_id` trong HALLS đổi → tự động cập nhật `hall_id` tại SEATS và SHOWTIMES

```sql
-- Áp dụng ON UPDATE CASCADE trên FK
ALTER TABLE SEATS ADD CONSTRAINT fk_seats_hall
  FOREIGN KEY (hall_id) REFERENCES HALLS(hall_id) ON UPDATE CASCADE;
```

**Chính sách xóa:**

- `HALLS` → `SEATS`: `ON DELETE CASCADE` (xóa phòng → xóa ghế)
- `MOVIES` → `SHOWTIMES` → `TICKETS`: `ON DELETE RESTRICT` (không xóa phim đã bán vé → áp dụng **Soft Delete** qua cột `status`)

**Ràng buộc cấp thấp:**

```sql
-- Đảm bảo end_time > start_time
ALTER TABLE SHOWTIMES ADD CONSTRAINT chk_time CHECK (end_time > start_time);

-- Ràng buộc unique ngăn bán trùng ghế (xử lý Race Condition)
ALTER TABLE TICKETS ADD UNIQUE (show_id, seat_id);
```

---

## Phần 4: Hiện Thực Chương Trình & UI/UX

### 4.1 Kiến Trúc MVC + DAO

```
┌────────────────────────────────────────┐
│           VIEW LAYER                   │
│  Java Swing: JFrame, JPanel, JDialog   │
│  (hoặc JavaFX: FXML + CSS)             │
└──────────────┬─────────────────────────┘
               │ Event / Action
               ▼
┌────────────────────────────────────────┐
│         CONTROLLER LAYER               │
│  Điều phối logic, validate, routing    │
└──────────────┬─────────────────────────┘
               │ gọi
               ▼
┌────────────────────────────────────────┐
│           SERVICE LAYER                │
│  Business Logic, Business Rules        │
└──────────────┬─────────────────────────┘
               │ gọi
               ▼
┌────────────────────────────────────────┐
│          DAO LAYER (Pattern)           │
│  Đóng gói SQL — ẩn độ phức tạp CSDL   │
└──────────────┬─────────────────────────┘
               │ JDBC
               ▼
┌────────────────────────────────────────┐
│         DATABASE (MySQL 8.0)           │
└────────────────────────────────────────┘
```

### 4.2 Thiết Kế Giao Diện (Dashboard)

Màn hình chính cấu trúc **BorderPane** (hoặc `JPanel` + `CardLayout`):

- **Thanh trái (Sidebar):** Accordion Group — nhóm nghiệp vụ (Quản lý Phim, Suất chiếu, Bán vé, Báo cáo)
- **Vùng trung tâm:** `TabPane` / `JTabbedPane` — đa nhiệm, giữ trạng thái giao dịch khi chuyển tab

**Màn hình CRUD** dùng `TableView` / `JTable` với **ObservableList** / `TableModel` tự động làm mới khi dữ liệu thay đổi.

**Advanced Search Panel:** ComboBox (thể loại), DatePicker (ngày chiếu), Slider (giá vé trần/sàn)

### 4.3 Sơ Đồ Ghế Động (Seat Map)

```
[A1][A2][A3]...[A10]   ← Màu xanh: AVAILABLE
[B1][B2]...            ← Màu cam:  SELECTED (đang chọn)
[C1][C2]...            ← Màu xám:  BOOKED (đã bán, disabled)
[D1][D2]...            ← Màu tím:  VIP
```

**Thuật toán dựng ghế động:**

```java
// Duyệt qua kích thước phòng chiếu từ DB
for (int row = 0; row < totalRows; row++) {
    for (int col = 0; col < totalCols; col++) {
        JToggleButton seatBtn = new JToggleButton(row + "" + col);
        // Áp dụng màu theo SeatStatus
        seatBtn.setBackground(getSeatColor(seatStatus));
        seatBtn.setEnabled(seatStatus != SeatStatus.BOOKED);
        seatGrid.add(seatBtn, col, row);
    }
}
```

### 4.4 Xuất Hóa Đơn & In Vé PDF

**Luồng xử lý khi thanh toán:**

1. Lập hóa đơn → chèn vào INVOICES + TICKETS trong **Database Transaction**
2. Nếu `status = SUCCESS` → kích hoạt xuất báo cáo
3. Dùng **JasperReports** (thiết kế `.jrxml` qua Jasper Studio) hoặc **iText**
4. `JasperFillManager` + `JRPdfExporter` → xuất PDF → in qua máy in nhiệt 80mm

### 4.5 Thống Kê & Biểu Đồ

**SQL doanh thu hàng tháng:**

```sql
SELECT EXTRACT(MONTH FROM created_at) AS report_month,
       SUM(total_amount) AS revenue
FROM INVOICES
WHERE status = 'SUCCESS'
GROUP BY EXTRACT(MONTH FROM created_at)
ORDER BY report_month ASC;
```

**Top 10 phim ăn khách:**

```sql
SELECT m.title, SUM(t.final_price) AS revenue
FROM MOVIES m
JOIN SHOWTIMES s ON s.movie_id = m.movie_id
JOIN TICKETS t ON t.show_id = s.show_id
JOIN INVOICES i ON i.invoice_id = t.invoice_id
WHERE i.status = 'SUCCESS'
GROUP BY m.title
ORDER BY revenue DESC LIMIT 10;
```

> ⚠️ Các truy vấn nặng chạy trên **Background Thread** (JavaFX `Task` / Swing `SwingWorker`) để tránh treo UI.

---

## Phần 5: Tiêu Chuẩn Mã Hóa (Coding Conventions)

### 5.1 Quy Tắc Đặt Tên

| Cấp Độ                   | Chuẩn                                           | Ví Dụ                                                |
| --------------------------- | ------------------------------------------------ | ------------------------------------------------------ |
| **Package**           | lowercase, không dấu gạch dưới              | `com.rapphim.dao`, `com.rapphim.models`            |
| **Class / Interface** | PascalCase (danh từ)                            | `TicketManager`, `InvoiceReportViewer`             |
| **Interface**         | PascalCase + hậu tố `-able`                  | `Printable`, `Searchable`                          |
| **Method**            | camelCase (động từ, 1 hành động duy nhất) | `calculateTotalRevenue()`, `fetchAvailableSeats()` |
| **Variable**          | camelCase (danh từ mô tả nội dung)           | `customerName`, `totalPrice`, `seatIndex`        |
| **Constant**          | UPPER_SNAKE_CASE                                 | `MAX_HALL_CAPACITY`, `DEFAULT_TAX_RATE`            |

### 5.2 Định Dạng & Khoảng Trắng

- **Thụt lề:** 4 dấu cách (spaces), không mix với Tab
- **Dấu ngoặc nhọn `{}`:** Phong cách K&R — `{` cuối dòng khai báo, `}` trên dòng riêng
- **Khoảng trắng:** 1 khoảng trắng quanh toán tử (`=`, `+`, `==`), sau dấu phẩy
- **Dòng trống:** 1 dòng trống giữa các method, giữa khai báo biến và logic
- **Import:** Sắp xếp theo alphabet theo nhóm. **Cấm** `import java.util.*;` — phải import đích danh

### 5.3 Triết Lý Bình Luận & Javadoc

**Nguyên tắc:** Mã phải **tự giải thích** qua tên gọi chuẩn xác. Comment giải thích **TẠI SAO** (why), không phải **CÁI GÌ** (what).

```java
/**
 * Tính tổng doanh thu của một suất chiếu.
 * Áp dụng luật thuế VAT 10% theo Nghị định 15/2022/NĐ-CP.
 *
 * @param showId  Mã định danh của suất chiếu cần tính
 * @return        Tổng doanh thu (đã bao gồm VAT), đơn vị VND
 * @throws        ShowNotFoundException nếu showId không tồn tại
 */
public double calculateShowRevenue(String showId) throws ShowNotFoundException { ... }
```

**Cấm:**

- Bình luận hiển nhiên: `int age = 18; // khai báo tuổi bằng 18`
- Code bị comment-out bừa bãi trong source code
- Sử dụng ký tự vô nghĩa cho tên biến: `a`, `b`, `x` (trừ `i`, `j`, `k` trong vòng lặp hẹp)

---

## Danh Sách Màn Hình (Screens)

| Màn Hình        | Mô Tả                                    | Quyền Truy Cập |
| ----------------- | ------------------------------------------ | ---------------- |
| `LoginForm`     | Đăng nhập hệ thống                    | Tất cả         |
| `MainFrame`     | Khung chính, sidebar + TabPane            | Tất cả         |
| `MoviePanel`    | Quản lý danh sách phim (CRUD + Search)  | Quản lý, Admin |
| `MovieForm`     | Thêm/sửa thông tin phim                 | Quản lý, Admin |
| `ShowtimePanel` | Quản lý lịch chiếu                     | Quản lý, Admin |
| `POSPanel`      | Bán vé, chọn ghế (Seat Map Grid)       | Thu ngân        |
| `InvoicePanel`  | Xem/In hóa đơn, xuất PDF               | Thu ngân, Admin |
| `CustomerPanel` | Quản lý khách hàng                     | Thu ngân, Admin |
| `EmployeePanel` | Quản lý nhân viên                      | Admin            |
| `RevenuePanel`  | Báo cáo doanh thu + Biểu đồ           | Quản lý, Admin |
| `HallPanel`     | Quản lý phòng chiếu & sơ đồ ghếSSS | Admin            |

## Phân Quyền Hệ Thống

| Chức Năng                  | Quản Lý | Thu Ngân |
| ---------------------------- | :-------: | :-------: |
| Quản lý nhân viên        |    ✅    |    ❌    |
| Quản lý phim/phòng chiếu |    ✅    |    ❌    |
| Quản lý lịch chiếu       |    ✅    |    ❌    |
| Bán vé                     |    ✅    |    ✅    |
| Hủy vé / Hoàn tiền       |    ✅    |    ❌    |
| Xem báo cáo doanh thu      |    ✅    |    ❌    |
| Quản lý khách hàng       |    ✅    |    ✅S    |

---

*Tài liệu cập nhật: 04/03/2026 | Dựa theo báo cáo phân tích thiết kế hệ thống*

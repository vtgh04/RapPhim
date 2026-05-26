# THIẾT KẾ CƠ SỞ DỮ LIỆU & KIẾN TRÚC LỚP (DATABASE & CLASS DESIGN)
## Dự án: Hệ Thống Quản Lý Rạp Chiếu Phim & POS RapPhim

---

## 1. Sơ Đồ Thực Thể Quan Hệ (ERD)

Cơ sở dữ liệu của dự án được chuẩn hóa ở dạng **3NF (Third Normal Form)** để loại bỏ dư thừa dữ liệu, đảm bảo tính toàn vẹn và thực thi các mối quan hệ khoá ngoại (FK) chặt chẽ giữa các bảng nghiệp vụ.

![Sơ đồ ERD](../assets/diagrams/erd.svg)

---

## 2. Từ Điển Dữ Liệu Chi Tiết (Data Dictionary)

Dưới đây là đặc tả chi tiết của từng bảng dữ liệu được triển khai trên hệ thống cơ sở dữ liệu **Microsoft SQL Server**.

### 2.1 Bảng `employees` (Nhân viên)
*Lưu trữ thông tin định danh, tài khoản đăng nhập và phân quyền của nhân viên.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `employee_id` | `VARCHAR(20)` | No | **PK** | Mã nhân viên (ví dụ: EMP001, EMP002). |
| `full_name` | `NVARCHAR(100)` | No | | Họ tên đầy đủ của nhân viên. |
| `username` | `VARCHAR(50)` | No | **UQ** | Tên đăng nhập hệ thống (không trùng lặp). |
| `password` | `VARCHAR(255)` | No | | Mật khẩu đã được băm bảo mật. |
| `role` | `VARCHAR(20)` | No | | Vai trò: `MANAGER` hoặc `STAFF`. |
| `status` | `VARCHAR(20)` | No | | Trạng thái: `ACTIVE` hoặc `RETIRED` (mặc định: `ACTIVE`). |
| `phone` | `VARCHAR(20)` | Yes | | Số điện thoại liên hệ. |
| `email` | `VARCHAR(100)` | Yes | **UQ** | Thư điện tử (không trùng lặp). |

### 2.2 Bảng `movies` (Phim)
*Danh mục các bộ phim được chiếu tại rạp.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `movie_id` | `VARCHAR(20)` | No | **PK** | Mã phim (ví dụ: MV001, MV002). |
| `title` | `NVARCHAR(200)` | No | | Tên phim tiếng Việt hoặc tiếng Anh gốc. |
| `genre` | `NVARCHAR(100)` | No | | Thể loại phim (hành động, hài, kinh dị,...). |
| `duration_mins` | `INT` | No | | Thời lượng phim (phút). Phải > 0. |
| `format_movie` | `VARCHAR(20)` | No | | Định dạng phim: `2D`, `3D`, `IMAX`. |
| `rating` | `VARCHAR(10)` | No | | Phân loại độ tuổi: `T13`, `T16`, `T18`, `P`. |
| `language` | `NVARCHAR(50)` | Yes | | Ngôn ngữ phim (ví dụ: Tiếng Anh - Phụ đề Tiếng Việt). |
| `release_date` | `DATE` | Yes | | Ngày khởi chiếu chính thức. |
| `status` | `VARCHAR(20)` | No | | Trạng thái: `ACTIVE` hoặc `INACTIVE`. |
| `description` | `NVARCHAR(MAX)` | Yes | | Nội dung tóm tắt phim. |
| `poster_url` | `VARCHAR(255)` | Yes | | Đường dẫn hình ảnh poster phim. |

### 2.3 Bảng `cinema_halls` (Phòng chiếu)
*Cấu hình phòng chiếu phim.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `hall_id` | `VARCHAR(20)` | No | **PK** | Mã phòng chiếu (ví dụ: HALL01, HALL02). |
| `name` | `NVARCHAR(100)` | No | **UQ** | Tên phòng chiếu (ví dụ: Phòng Chiếu 1). |
| `hall_type` | `VARCHAR(20)` | Yes | | Loại phòng: `STANDARD`, `IMAX`, `GOLDCLASS`. |
| `total_rows` | `INT` | No | | Số lượng hàng ghế cứng (ví dụ: 8 hàng). |
| `total_cols` | `INT` | No | | Số lượng cột ghế cứng (ví dụ: 10 cột). |
| `status` | `VARCHAR(20)` | No | | Trạng thái phòng: `ACTIVE`, `INACTIVE`. |

### 2.4 Bảng `seats` (Ghế ngồi vật lý)
*Cấu hình bố cục vị trí ghế cứng thuộc về từng phòng chiếu cụ thể.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `seat_id` | `VARCHAR(20)` | No | **PK** | Mã định danh ghế (Ví dụ: HALL01_A5). |
| `hall_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `cinema_halls`. |
| `row_char` | `CHAR(1)` | No | | Ký tự hàng ghế (A, B, C, D, ...). |
| `col_number` | `INT` | No | | Số thứ tự cột ghế (1, 2, 3, ...). |
| `seat_type` | `VARCHAR(20)` | No | | Phân loại: `REGULAR`, `VIP` (mặc định: `REGULAR`). |
| `seat_factor` | `DECIMAL(4,2)` | No | | Hệ số giá ghế: VIP = `1.2`, Regular = `1.0`. |
| `is_broken` | `BIT` | Yes | | Ghế bị hỏng? 1 = Hỏng (không cho đặt), 0 = Bình thường. |

### 2.5 Bảng `showtimes` (Suất chiếu)
*Bảng lịch chiếu ghép Phim vào Phòng chiếu tại khung giờ nhất định.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `showtime_id` | `VARCHAR(20)` | No | **PK** | Mã suất chiếu (ví dụ: ST001, ST002). |
| `movie_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `movies`. |
| `hall_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `cinema_halls`. |
| `start_time` | `DATETIME2` | No | | Giờ bắt đầu chiếu phim. |
| `end_time` | `DATETIME2` | No | | Giờ kết thúc (tự động tính dựa trên thời lượng phim). |
| `base_price` | `DECIMAL(10,2)` | No | | Giá vé cơ bản cho suất chiếu (ví dụ: 80000.00). |
| `status` | `VARCHAR(20)` | No | | Trạng thái suất chiếu: `ACTIVE`, `CANCELLED`. |

### 2.6 Bảng `show_seats` (Ghế ngồi suất chiếu)
*Ghế ngồi động được sinh tự động cho từng suất chiếu cụ thể.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `show_seat_id` | `VARCHAR(50)` | No | **PK** | Mã ghế suất chiếu (ví dụ: ST001_A5). |
| `showtime_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `showtimes`. |
| `seat_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `seats`. |
| `price` | `DECIMAL(10,2)` | No | | Giá thực tế sau khi nhân hệ số ghế. |
| `status` | `VARCHAR(20)` | No | | Trạng thái đặt: `AVAILABLE`, `LOCKED`, `BOOKED`. |

### 2.7 Bảng `invoices` (Hóa đơn thanh toán)
*Hóa đơn chứa thông tin giao dịch đặt vé.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `invoice_id` | `VARCHAR(20)` | No | **PK** | Mã hóa đơn giao dịch (ví dụ: INV001, INV002). |
| `employee_id` | `VARCHAR(20)` | No | **FK** | Mã nhân viên thực hiện bán vé tại quầy POS. |
| `created_at` | `DATETIME2` | No | | Ngày giờ tạo giao dịch hóa đơn. |
| `total_amount` | `DECIMAL(10,2)` | No | | Tổng tiền thanh toán của hóa đơn. |
| `total_tickets` | `INT` | No | | Tổng số lượng vé bán trong hóa đơn. |
| `payment_method` | `VARCHAR(20)` | No | | Phương thức thanh toán: `CASH`, `CARD`, `E_WALLET`. |
| `status` | `VARCHAR(20)` | No | | Trạng thái hóa đơn: `CONFIRMED`, `REFUNDED`. |
| `note` | `NVARCHAR(1000)` | Yes | | Ghi chú thêm (thông tin khách hàng hoặc giảm giá). |

### 2.8 Bảng `tickets` (Vé xem phim)
*Vé xem phim chi tiết tương ứng với từng ghế đặt.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `ticket_id` | `VARCHAR(20)` | No | **PK** | Mã vé (ví dụ: TKT001, TKT002). |
| `invoice_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `invoices`. |
| `show_seat_id` | `VARCHAR(50)` | No | **FK** | Liên kết với bảng `show_seats`. |
| `original_price` | `DECIMAL(10,2)` | No | | Giá vé gốc của ghế trong suất chiếu. |
| `price_after_discount` | `DECIMAL(10,2)` | No | | Giá vé cuối cùng sau khi áp discount/mã giảm giá. |
| `barcode` | `VARCHAR(100)` | No | | Mã vạch dùng để quét check-in tại phòng chiếu. |
| `status` | `VARCHAR(20)` | No | | Trạng thái: `VALID`, `USED`, `REFUNDED`. |

### 2.9 Bảng `reviews` (Đánh giá phim)
*Đánh giá và bình luận phim từ khách hàng.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `review_id` | `NVARCHAR(50)` | No | **PK** | Mã đánh giá ngẫu nhiên/UUID. |
| `movie_id` | `VARCHAR(20)` | No | **FK** | Liên kết với phim được đánh giá (`movies.movie_id`). |
| `user_id` | `NVARCHAR(100)` | No | | Định danh người dùng đánh giá (Email hoặc Username). |
| `rating` | `INT` | No | | Điểm số đánh giá từ `1` đến `5` sao. |
| `comment` | `NVARCHAR(1000)` | Yes | | Nội dung bình luận của người dùng. |
| `created_at` | `DATETIME2` | No | | Thời gian tạo đánh giá. |

---

## 3. Kiến Trúc Lớp Thực Thể (Class Diagram)

Các thực thể Java Spring Boot Backend được ánh xạ trực tiếp từ các bảng cơ sở dữ liệu trên thông qua các Annotation của Hibernate/JPA. Dưới đây là sơ đồ lớp chi tiết.

![Sơ đồ Class Diagram](../assets/diagrams/class_diagram.svg)

### 3.1 Quy tắc ánh xạ và quan hệ
* **@OneToMany & @ManyToOne Relationships:** 
  * Một `CinemaHall` chứa một danh sách `Seat` vật lý.
  * Một `Showtime` chứa danh sách `ShowSeat` động được khởi tạo tự động khi tạo Suất chiếu.
  * Một `Invoice` chứa danh sách nhiều `Ticket`.
* **Trạng thái thực thể (Enums):**
  * Các trường trạng thái được cấu hình thông qua `@Enumerated(EnumType.STRING)` để lưu trữ text trong DB thay vì số Index, giúp việc truy vấn thủ công hoặc debug dữ liệu dễ dàng hơn.

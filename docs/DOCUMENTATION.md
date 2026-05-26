# TÀI LIỆU TỔNG HỢP NGHIỆP VỤ & THIẾT KẾ HỆ THỐNG (BA & SYSTEM DESIGN MASTER SPECIFICATION)
## Dự án: Hệ Thống Quản Lý Rạp Chiếu Phim & POS RapPhim

---

## 📁 1. Cấu Trúc Thư Mục Dự Án (Project Folder Structure)

Sơ đồ cấu trúc thư mục phân tách chuyên nghiệp giữa Backend (Spring Boot), Frontend (React SPA) và tài liệu đặc tả nghiệp vụ:

![Sơ đồ cấu trúc thư mục](assets/diagrams/folder_structure.svg)

---

## 🎨 2. Tổng Quan Công Nghệ & Kiến Trúc (Tech Stack & Architecture)

### 2.1 Công Nghệ Sử Dụng (Technology Stack)
Mô tả chi tiết các thư viện, framework và hệ quản trị cơ sở dữ liệu được áp dụng trong toàn bộ hệ thống.

![Sơ đồ Tech Stack](assets/diagrams/tech_stack.svg)

### 2.2 Kiến Trúc Hệ Thống (Project Architecture Overview)
Mô hình kiến trúc phân lớp, giao tiếp REST API và đồng bộ hóa Seat Map thời gian thực qua WebSocket.

![Sơ đồ kiến trúc](assets/diagrams/project_architecture.svg)

---

## 📑 3. Tài Liệu Yêu Cầu Nghiệp Vụ (Business Requirements Document - BRD)

### 3.1 Bối Cảnh Dự Án (Project Context)
Trong thời đại số hóa hiện nay, trải nghiệm khách hàng tại các hệ thống dịch vụ giải trí, đặc biệt là rạp chiếu phim, đòi hỏi sự nhanh chóng, chính xác và đồng bộ. Các phương pháp quản lý thủ công hoặc ứng dụng desktop đơn lẻ (như Java Swing cũ) bộc lộ nhiều hạn chế về khả năng mở rộng, báo cáo thời gian thực và đồng bộ hóa trạng thái ghế giữa các quầy bán vé.

Dự án **RapPhim** được thực hiện nhằm hiện đại hóa toàn bộ hệ thống quản lý rạp chiếu phim, chuyển đổi từ ứng dụng desktop Java Swing cũ sang kiến trúc **Web Application** hiện đại. Hệ thống mới phân tách rõ ràng giữa **Backend REST API (Spring Boot)** và **Frontend Client (React SPA)**, tích hợp luồng dữ liệu thời gian thực (realtime) nhằm giải quyết dứt điểm các bài toán kinh doanh thực tế.

### 3.2 Mục Tiêu Chiến Lược (Business Goals)
* **Tối ưu hóa quy trình bán vé (POS):** Giảm thời gian giao dịch tại quầy xuống dưới 30 giây cho mỗi lượt chọn ghế và thanh toán.
* **Đồng bộ hóa dữ liệu thời gian thực:** Loại bỏ hoàn toàn tình trạng trùng lặp ghế (Double Booking) khi nhiều nhân viên cùng thao tác bán vé cùng lúc tại các quầy khác nhau nhờ công nghệ WebSocket.
* **Nâng cao năng lực quản trị (Management & Analytics):** Cung cấp các biểu đồ doanh thu trực quan, biểu đồ thị phần phim bán chạy theo thời gian thực giúp quản lý rạp đưa ra quyết định phân phối suất chiếu hợp lý.
* **Hỗ trợ đa ngôn ngữ và linh hoạt thao tác:** Ứng dụng tích hợp chuyển đổi ngôn ngữ Việt - Anh linh hoạt để phù hợp với cả nhân viên người nước ngoài hoặc mở rộng chuỗi rạp.

### 3.3 Các Bên Liên Quan (Stakeholders)
| Vai trò | Mô tả nhiệm vụ & Quyền lợi | Quyền truy cập hệ thống |
| :--- | :--- | :--- |
| **Quản lý rạp (Manager)** | Theo dõi doanh thu, cấu hình danh mục phim, quản lý suất chiếu, sơ đồ phòng chiếu và cấu hình mã giảm giá. | Toàn quyền (Dashboard, Movies, Showtimes, Halls, Discounts). |
| **Nhân viên quầy vé (Staff)** | Tiếp đón khách hàng, tra cứu phim, chọn ghế trên sơ đồ trực quan, áp dụng mã giảm giá và in hóa đơn/vé cho khách. | Quyền bán vé & POS (Movies, Showtimes, Seat Grid, Checkout). |
| **Khách xem phim (End User)** | Xem lịch chiếu, chọn ghế, đánh giá và nhận xét phim (Reviews) trực tiếp trên trang chủ. | Client UI (Movie Detail, Reviews, Live seat map). |
| **Đội ngũ Vận hành / IT** | Bảo trì hệ thống, quản lý cơ sở dữ liệu, giám sát WebSocket kết nối. | Quản trị DB & Máy chủ. |

### 3.4 Phạm Vi Hệ Thống (Product Scope)

#### Trong Phạm Vi (In-Scope)
* **Cơ chế xác thực an toàn:** Đăng nhập và tự động xoay vòng token (JWT Access & Refresh Token) phân quyền chặt chẽ giữa Manager và Staff.
* **Giao diện bán vé POS trực quan:** Sơ đồ phòng chiếu tương tác vẽ động theo hàng và cột của phòng, phân biệt rõ ghế VIP (giá cao hơn), ghế Thường, ghế hỏng, ghế đã bán.
* **Đặt ghế Realtime:** Đồng bộ trạng thái ghế đang chọn (Locked) tức thời tới tất cả các quầy bán vé khác qua WebSocket.
* **Xử lý thanh toán mô phỏng (Payment Strategy):** Cho phép chọn các phương thức như Tiền mặt, Thẻ ngân hàng, Ví điện tử và tự động áp dụng chiết khấu/mã giảm giá.
* **Xuất hóa đơn & Vé chuyên nghiệp:** Tự động tạo và lưu trữ hóa đơn PDF, file vé chứa mã vạch (Barcode) định dạng PDF để phục vụ công tác kiểm vé tại cửa phòng chiếu.
* **Quản trị Suất chiếu trực quan:** Giao diện lập lịch kéo thả/chọn thẻ nhanh, kiểm tra xung đột phòng chiếu tự động (không cho phép 2 phim chiếu cùng một phòng tại cùng một khoảng thời gian).
* **Báo cáo tài chính trực quan:** Dashboard thống kê doanh thu 30 ngày gần nhất vẽ biểu đồ động SVG (Bar, Line, Area), thống kê cơ cấu vé bán theo phim (Donut Chart).

#### Ngoài Phạm Vi (Out-of-Scope trong Phase này)
* **Tích hợp cổng thanh toán trực tuyến thực tế (VNPAY, Momo, Stripe API):** Hiện tại chỉ dùng mock Payment Strategy ở phía Backend.
* **Hệ thống đặt vé trực tuyến dành cho khách hàng tự phục vụ (B2C Booking Web):** Phiên bản hiện tại tập trung làm POS bán vé tại quầy (B2B/POS).

### 3.5 Các Chỉ Số KPI Đánh Giá Hiệu Năng (Key Metrics & KPIs)
1. **Tốc độ phản hồi API:** 95% số request API hoàn tất dưới 200ms.
2. **Đồng bộ Realtime:** Độ trễ truyền phát trạng thái khóa ghế qua WebSocket dưới 50ms.
3. **Độ ổn định dữ liệu:** 100% các giao dịch thanh toán thành công phải tạo lập hóa đơn và vé tương ứng một cách toàn vẹn (ACID transaction).
4. **Hiệu suất in ấn:** Thời gian render PDF hóa đơn và vé dưới 1.5 giây sau khi checkout thành công.

---

## 📑 4. Đặc Tả Yêu Cầu Phần Mềm (Software Requirements Specification - SRS)

### 4.1 Danh Sách User Stories tiêu biểu
* **Là một Nhân viên bán vé (Staff),** tôi muốn xem sơ đồ ghế ngồi trực quan của suất chiếu theo thời gian thực để tư vấn vị trí ngồi tốt nhất cho khách hàng và tránh chọn trùng ghế với nhân viên khác.
* **Là một Quản lý (Manager),** tôi muốn tạo mới suất chiếu thông qua giao diện thẻ trực quan và được hệ thống tự động cảnh báo nếu suất chiếu đó bị trùng phòng chiếu hoặc trùng giờ chiếu với phim khác.
* **Là một Quản lý (Manager),** tôi muốn xem biểu đồ doanh thu dạng cột (Bar) và đường (Line) vẽ trực tiếp trên hệ thống để đánh giá kết quả kinh doanh hàng ngày mà không cần mở các phần mềm báo cáo bên ngoài.

### 4.2 Luật Nghiệp Vụ Hệ Thống (Business Rules)
* **Quy tắc tính giá vé:** Giá vé cuối cùng của một ghế trong suất chiếu được tính bằng công thức:
  $$\text{Giá Vé} = \text{Giá cơ bản của suất chiếu} \times \text{Hệ số loại ghế (Seat Factor)}$$
  * Ghế `REGULAR` (Thường) có hệ số mặc định là `1.0`.
  * Ghế `VIP` có hệ số mặc định là `1.2` (giá cao hơn 20%).
* **Kiểm tra trùng lịch chiếu (Conflict Detection):** Khi cấu hình suất chiếu mới, hệ thống tự động kiểm tra khoảng thời gian bắt đầu và kết thúc (thời gian bắt đầu + thời lượng phim + 15 phút dọn phòng). Nếu có bất kỳ sự giao thoa nào về mặt thời gian tại cùng một `hall_id`, hệ thống sẽ chặn thao tác lưu và báo lỗi.
* **Quy trình hủy suất chiếu (Cascade Showtime Deletion):** Khi quản lý xóa một suất chiếu, hệ thống sử dụng cơ chế transaction cascade để tự động giải phóng toàn bộ dữ liệu ghế động (`show_seats`) liên quan, tránh tình trạng rác cơ sở dữ liệu.
* **Quy tắc khóa ghế tạm thời (WebSocket Seating Lock):** Khi nhân viên quầy chọn một ghế, trạng thái ghế đó sẽ chuyển thành `LOCKED` trên tất cả các client đang kết nối đến phòng chiếu đó. Nếu giao dịch không hoàn tất trong vòng 10 phút, ghế tự động mở khóa về lại `AVAILABLE`.

### 4.3 Sơ Đồ Use Case & Đặc Tả
Hệ thống phân định rạch ròi các chức năng quản trị và bán hàng thông qua sơ đồ Use Case dưới đây.

![Sơ đồ Use Case](assets/diagrams/use_case.svg)

#### 4.3.1 Đặc tả Use Case: Bán Vé & Chọn Ghế (POS Booking)
* **Actor chính:** Staff
* **Mô tả:** Nhân viên chọn suất chiếu của khách, chọn các ghế trống trên bản đồ phòng chiếu động, áp mã giảm giá, nhận tiền thanh toán và hoàn tất giao dịch.
* **Luồng xử lý chính:**
  1. Nhân viên chọn Phim -> chọn Suất chiếu -> Hệ thống hiển thị sơ đồ ghế.
  2. Nhân viên click chọn một hoặc nhiều ghế -> Hệ thống gửi lệnh khóa ghế tạm thời lên WebSocket để cập nhật cho các quầy khác.
  3. Nhân viên chọn phương thức thanh toán (Tiền mặt / Thẻ / Ví điện tử) và nhập mã giảm giá (nếu có).
  4. Nhân viên click "Thanh toán" -> Hệ thống gọi API checkout, trừ tiền, tạo hóa đơn, đổi trạng thái ghế sang `BOOKED`.
  5. Hệ thống gọi thread ngầm tạo file hóa đơn và vé PDF có mã vạch, hiển thị hộp thoại in ra máy in nhiệt.

#### 4.3.2 Đặc tả Use Case: Lập Lịch Suất Chiếu (Manage Showtimes)
* **Actor chính:** Manager
* **Mô tả:** Quản lý cấu hình lịch chiếu phim cho các phòng.
* **Luồng xử lý chính:**
  1. Quản lý chọn một Phim có trạng thái hoạt động.
  2. Chọn Phòng chiếu (`hall_id`), thời gian bắt đầu (`start_time`), và nhập giá vé cơ bản.
  3. Hệ thống tự động cộng thời lượng phim để tính ra `end_time` dự kiến.
  4. Hệ thống kiểm tra xem phòng chiếu đó có bị trùng giờ chiếu với suất chiếu nào khác đã lên lịch hay không.
  5. Nếu không trùng, lưu suất chiếu -> Hệ thống tự động nhân bản toàn bộ danh sách ghế cứng của phòng chiếu đó thành danh sách ghế động (`show_seats`) tương ứng với suất chiếu mới này.

### 4.4 Sơ Đồ Trình Tự Thanh Toán (Sequence Diagram)
Mô tả luồng giao tiếp đồng bộ (HTTP) và bất đồng bộ (WebSocket) khi nhân viên quầy thực hiện thao tác giữ ghế và thanh toán vé.

![Sơ đồ Trình Tự đặt vé](assets/diagrams/sequence.svg)

#### 4.4.1 Giải Trình Các Bước Trong Sơ Đồ
1. **Khóa ghế tạm thời:** Khi nhân viên chọn ghế, client React gửi message websocket chứa JSON `{ seatId, status: "LOCKED" }` lên server. Server phát sóng (broadcast) message này tới tất cả các client đang đăng ký kênh `/topic/showtime/{id}/seats` để đổi màu ghế sang màu vàng (đang được quầy khác chọn).
2. **Kiểm tra trạng thái tại Backend:** Khi nhấn nút Thanh Toán, API REST nhận yêu cầu. Trong một `@Transactional` duy nhất, hệ thống kiểm tra lại trạng thái ghế trong database để ngăn ngừa hành vi gọi API checkout trực tiếp đối với ghế đã bị mua.
3. **Thanh toán & Xuất vé:** Sau khi lưu hóa đơn (`Invoice`) và vé (`Ticket`), hệ thống kích hoạt thư viện **iText** và **Apache POI** để tạo file offline, đồng thời bắn Event kết thúc giao dịch (`SeatBookedEvent`).
4. **Cập nhật trạng thái cuối cùng:** WebSocket Broker phát tín hiệu trạng thái ghế đã đổi sang `BOOKED` (màu đỏ) vĩnh viễn cho suất chiếu này.

### 4.5 Yêu Cầu Phi Chức Năng (Non-Functional Requirements)

#### 4.5.1 Bảo mật & Phân quyền (Security & Auth)
* Sử dụng thuật toán mã hóa **HMAC-SHA256** để ký số chữ ký điện tử cho JWT.
* Hệ thống quản lý phiên đăng nhập thông qua cơ chế Dual-Token: `accessToken` (thời hạn 24 giờ) và `refreshToken` (thời hạn 7 ngày, lưu trữ an toàn).
* Toàn bộ API endpoint bắt buộc phải đi qua Filter bảo mật để phân tích header `Authorization: Bearer <token>`.

#### 4.5.2 Hiệu Năng & Khả Năng Mở Rộng (Performance & Scaling)
* Thiết lập Index trên cơ sở dữ liệu cho các trường thường xuyên tìm kiếm và sắp xếp: `movies.title`, `employees.username`, `showtimes.start_time`.
* Sử dụng Connection Pool HikariCP cấu hình tối thiểu 10 kết nối đồng thời để giảm thời gian khởi tạo kết nối cơ sở dữ liệu.

#### 4.5.3 Khả năng thích ứng của Giao diện (Responsive Design)
* Giao diện POS bán vé và giao diện Dashboard quản lý được tối ưu hóa hiển thị trên các màn hình PC độ phân giải chuẩn Full HD (1920x1080) và HD (1366x768) phục vụ máy bán vé tại quầy.
* Hỗ trợ Internationalization (i18n) với cấu trúc từ điển ngôn ngữ động cho phép đổi nhanh giao diện sang tiếng Anh/tiếng Việt.

---

## 📑 5. Thiết Kế Cơ Sở Dữ Liệu & Class (Database & Class Design)

### 5.1 Sơ Đồ Thực Thể Quan Hệ (ERD)
Cơ sở dữ liệu của dự án được chuẩn hóa ở dạng **3NF (Third Normal Form)** để loại bỏ dư thừa dữ liệu, đảm bảo tính toàn vẹn và thực thi các mối quan hệ khoá ngoại (FK) chặt chẽ giữa các bảng nghiệp vụ.

![Sơ đồ ERD](assets/diagrams/database_design.svg)

*(Chi tiết ERD chuẩn thể hiện trực quan qua các bảng).*

![Sơ đồ ERD thực thể quan hệ đầy đủ](assets/diagrams/erd.svg)

### 5.2 Từ Điển Dữ Liệu Chi Tiết (Data Dictionary)
Dưới đây là đặc tả chi tiết của từng bảng dữ liệu được triển khai trên hệ thống cơ sở dữ liệu **Microsoft SQL Server**.

#### 5.2.1 Bảng `employees` (Nhân viên)
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

#### 5.2.2 Bảng `movies` (Phim)
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

#### 5.2.3 Bảng `cinema_halls` (Phòng chiếu)
*Cấu hình phòng chiếu phim.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `hall_id` | `VARCHAR(20)` | No | **PK** | Mã phòng chiếu (ví dụ: HALL01, HALL02). |
| `name` | `NVARCHAR(100)` | No | **UQ** | Tên phòng chiếu (ví dụ: Phòng Chiếu 1). |
| `hall_type` | `VARCHAR(20)` | Yes | | Loại phòng: `STANDARD`, `IMAX`, `GOLDCLASS`. |
| `total_rows` | `INT` | No | | Số lượng hàng ghế cứng (ví dụ: 8 hàng). |
| `total_cols` | `INT` | No | | Số lượng cột ghế cứng (ví dụ: 10 cột). |
| `status` | `VARCHAR(20)` | No | | Trạng thái phòng: `ACTIVE`, `INACTIVE`. |

#### 5.2.4 Bảng `seats` (Ghế ngồi vật lý)
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

#### 5.2.5 Bảng `showtimes` (Suất chiếu)
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

#### 5.2.6 Bảng `show_seats` (Ghế ngồi suất chiếu)
*Ghế ngồi động được sinh tự động cho từng suất chiếu cụ thể.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `show_seat_id` | `VARCHAR(50)` | No | **PK** | Mã ghế suất chiếu (ví dụ: ST001_A5). |
| `showtime_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `showtimes`. |
| `seat_id` | `VARCHAR(20)` | No | **FK** | Liên kết với bảng `seats`. |
| `price` | `DECIMAL(10,2)` | No | | Giá thực tế sau khi nhân hệ số ghế. |
| `status` | `VARCHAR(20)` | No | | Trạng thái đặt: `AVAILABLE`, `LOCKED`, `BOOKED`. |

#### 5.2.7 Bảng `invoices` (Hóa đơn thanh toán)
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

#### 5.2.8 Bảng `tickets` (Vé xem phim)
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

#### 5.2.9 Bảng `reviews` (Đánh giá phim)
*Đánh giá và bình luận phim từ khách hàng.*

| Tên trường (Column) | Kiểu dữ liệu | Null | Khoá | Mô tả & Ràng buộc |
| :--- | :--- | :---: | :---: | :--- |
| `review_id` | `NVARCHAR(50)` | No | **PK** | Mã đánh giá ngẫu nhiên/UUID. |
| `movie_id` | `VARCHAR(20)` | No | **FK** | Liên kết với phim được đánh giá (`movies.movie_id`). |
| `user_id` | `NVARCHAR(100)` | No | | Định danh người dùng đánh giá (Email hoặc Username). |
| `rating` | `INT` | No | | Điểm số đánh giá từ `1` đến `5` sao. |
| `comment` | `NVARCHAR(1000)` | Yes | | Nội dung bình luận của người dùng. |
| `created_at` | `DATETIME2` | No | | Thời gian tạo đánh giá. |

### 5.3 Kiến Trúc Lớp Thực Thể (Class Diagram)
Các thực thể Java Spring Boot Backend được ánh xạ trực tiếp từ các bảng cơ sở dữ liệu trên thông qua các Annotation của Hibernate/JPA.

![Sơ đồ Class Diagram](assets/diagrams/class_diagram.svg)

#### 5.3.1 Quy tắc ánh xạ và quan hệ
* **@OneToMany & @ManyToOne Relationships:**
  * Một `CinemaHall` chứa một danh sách `Seat` vật lý.
  * Một `Showtime` chứa danh sách `ShowSeat` động được khởi tạo tự động khi tạo Suất chiếu.
  * Một `Invoice` chứa danh sách nhiều `Ticket`.
* **Trạng thái thực thể (Enums):**
  * Các trường trạng thái được cấu hình thông qua `@Enumerated(EnumType.STRING)` để lưu trữ text trong DB thay vì số Index, giúp việc truy vấn thủ công hoặc debug dữ liệu dễ dàng hơn.

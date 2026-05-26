# ĐẶC TẢ YÊU CẦU PHẦN MỀM (SOFTWARE REQUIREMENTS SPECIFICATION - SRS)
## Dự án: Hệ Thống Quản Lý Rạp Chiếu Phim & POS RapPhim

---

## 1. Yêu Cầu Chức Năng (Functional Requirements)

### 1.1 Danh Sách User Stories tiêu biểu
* **Là một Nhân viên bán vé (Staff),** tôi muốn xem sơ đồ ghế ngồi trực quan của suất chiếu theo thời gian thực để tư vấn vị trí ngồi tốt nhất cho khách hàng và tránh chọn trùng ghế với nhân viên khác.
* **Là một Quản lý (Manager),** tôi muốn tạo mới suất chiếu thông qua giao diện thẻ trực quan và được hệ thống tự động cảnh báo nếu suất chiếu đó bị trùng phòng chiếu hoặc trùng giờ chiếu với phim khác.
* **Là một Quản lý (Manager),** tôi muốn xem biểu đồ doanh thu dạng cột (Bar) và đường (Line) vẽ trực tiếp trên hệ thống để đánh giá kết quả kinh doanh hàng ngày mà không cần mở các phần mềm báo cáo bên ngoài.

### 1.2 Luật Nghiệp Vụ Hệ Thống (Business Rules)
* **Quy tắc tính giá vé:** Giá vé cuối cùng của một ghế trong suất chiếu được tính bằng công thức:
  $$\text{Giá Vé} = \text{Giá cơ bản của suất chiếu} \times \text{Hệ số loại ghế (Seat Factor)}$$
  * Ghế `REGULAR` (Thường) có hệ số mặc định là `1.0`.
  * Ghế `VIP` có hệ số mặc định là `1.2` (giá cao hơn 20%).
* **Kiểm tra trùng lịch chiếu (Conflict Detection):** Khi cấu hình suất chiếu mới, hệ thống tự động kiểm tra khoảng thời gian bắt đầu và kết thúc (thời gian bắt đầu + thời lượng phim + 15 phút dọn phòng). Nếu có bất kỳ sự giao thoa nào về mặt thời gian tại cùng một `hall_id`, hệ thống sẽ chặn thao tác lưu và báo lỗi.
* **Quy trình hủy suất chiếu (Cascade Showtime Deletion):** Khi quản lý xóa một suất chiếu, hệ thống sử dụng cơ chế transaction cascade để tự động giải phóng toàn bộ dữ liệu ghế động (`show_seats`) liên quan, tránh tình trạng rác cơ sở dữ liệu.
* **Quy tắc khóa ghế tạm thời (WebSocket Seating Lock):** Khi nhân viên quầy chọn một ghế, trạng thái ghế đó sẽ chuyển thành `LOCKED` trên tất cả các client đang kết nối đến phòng chiếu đó. Nếu giao dịch không hoàn tất trong vòng 10 phút, ghế tự động mở khóa về lại `AVAILABLE`.

---

## 2. Sơ Đồ Use Case & Đặc Tả

Hệ thống phân định rạch ròi các chức năng quản trị và bán hàng thông qua sơ đồ Use Case dưới đây.

![Sơ đồ Use Case](../assets/diagrams/use_case.svg)

### 2.1 Đặc tả Use Case: Bán Vé & Chọn Ghế (POS Booking)
* **Actor chính:** Staff
* **Mô tả:** Nhân viên chọn suất chiếu của khách, chọn các ghế trống trên bản đồ phòng chiếu động, áp mã giảm giá, nhận tiền thanh toán và hoàn tất giao dịch.
* **Luồng xử lý chính:**
  1. Nhân viên chọn Phim -> chọn Suất chiếu -> Hệ thống hiển thị sơ đồ ghế.
  2. Nhân viên click chọn một hoặc nhiều ghế -> Hệ thống gửi lệnh khóa ghế tạm thời lên WebSocket để cập nhật cho các quầy khác.
  3. Nhân viên chọn phương thức thanh toán (Tiền mặt / Thẻ / Ví điện tử) và nhập mã giảm giá (nếu có).
  4. Nhân viên click "Thanh toán" -> Hệ thống gọi API checkout, trừ tiền, tạo hóa đơn, đổi trạng thái ghế sang `BOOKED`.
  5. Hệ thống gọi thread ngầm tạo file hóa đơn và vé PDF có mã vạch, hiển thị hộp thoại in ra máy in nhiệt.

### 2.2 Đặc tả Use Case: Lập Lịch Suất Chiếu (Manage Showtimes)
* **Actor chính:** Manager
* **Mô tả:** Quản lý cấu hình lịch chiếu phim cho các phòng.
* **Luồng xử lý chính:**
  1. Quản lý chọn một Phim có trạng thái hoạt động.
  2. Chọn Phòng chiếu (`hall_id`), thời gian bắt đầu (`start_time`), và nhập giá vé cơ bản.
  3. Hệ thống tự động cộng thời lượng phim để tính ra `end_time` dự kiến.
  4. Hệ thống kiểm tra xem phòng chiếu đó có bị trùng giờ chiếu với suất chiếu nào khác đã lên lịch hay không.
  5. Nếu không trùng, lưu suất chiếu -> Hệ thống tự động nhân bản toàn bộ danh sách ghế cứng của phòng chiếu đó thành danh sách ghế động (`show_seats`) tương ứng với suất chiếu mới này.

---

## 3. Sơ Đồ Trình Tự Thanh Toán (Sequence Diagram)

Mô tả luồng giao tiếp đồng bộ (HTTP) và bất đồng bộ (WebSocket) khi nhân viên quầy thực hiện thao tác giữ ghế và thanh toán vé.

![Sơ đồ Trình Tự đặt vé](../assets/diagrams/sequence.svg)

### 3.1 Giải Trình Các Bước Trong Sơ Đồ
1. **Khóa ghế tạm thời:** Khi nhân viên chọn ghế, client React gửi message websocket chứa JSON `{ seatId, status: "LOCKED" }` lên server. Server phát sóng (broadcast) message này tới tất cả các client đang đăng ký kênh `/topic/showtime/{id}/seats` để đổi màu ghế sang màu vàng (đang được quầy khác chọn).
2. **Kiểm tra trạng thái tại Backend:** Khi nhấn nút Thanh Toán, API REST nhận yêu cầu. Trong một `@Transactional` duy nhất, hệ thống kiểm tra lại trạng thái ghế trong database để ngăn ngừa hành vi gọi API checkout trực tiếp đối với ghế đã bị mua.
3. **Thanh toán & Xuất vé:** Sau khi lưu hóa đơn (`Invoice`) và vé (`Ticket`), hệ thống kích hoạt thư viện **iText** và **Apache POI** để tạo file offline, đồng thời bắn Event kết thúc giao dịch (`SeatBookedEvent`).
4. **Cập nhật trạng thái cuối cùng:** WebSocket Broker phát tín hiệu trạng thái ghế đã đổi sang `BOOKED` (màu đỏ) vĩnh viễn cho suất chiếu này.

---

## 4. Yêu Cầu Phi Chức Năng (Non-Functional Requirements)

### 4.1 Bảo mật & Phân quyền (Security & Auth)
* Sử dụng thuật toán mã hóa **HMAC-SHA256** để ký số chữ ký điện tử cho JWT.
* Hệ thống quản lý phiên đăng nhập thông qua cơ chế Dual-Token: `accessToken` (thời hạn 24 giờ) và `refreshToken` (thời hạn 7 ngày, lưu trữ an toàn).
* Toàn bộ API endpoint bắt buộc phải đi qua Filter bảo mật để phân tích header `Authorization: Bearer <token>`.

### 4.2 Hiệu Năng & Khả Năng Mở Rộng (Performance & Scaling)
* Thiết lập Index trên cơ sở dữ liệu cho các trường thường xuyên tìm kiếm và sắp xếp: `movies.title`, `employees.username`, `showtimes.start_time`.
* Sử dụng Connection Pool HikariCP cấu hình tối thiểu 10 kết nối đồng thời để giảm thời gian khởi tạo kết nối cơ sở dữ liệu.

### 4.3 Khả năng thích ứng của Giao diện (Responsive Design)
* Giao diện POS bán vé và giao diện Dashboard quản lý được tối ưu hóa hiển thị trên các màn hình PC độ phân giải chuẩn Full HD (1920x1080) và HD (1366x768) phục vụ máy bán vé tại quầy.
* Hỗ trợ Internationalization (i18n) với cấu trúc từ điển ngôn ngữ động cho phép đổi nhanh giao diện sang tiếng Anh/tiếng Việt.

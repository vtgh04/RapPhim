# Dự Án Quản Lý Rạp Chiếu Phim (RapPhim) - Phân Công Nhiệm Vụ & Tiến Độ

**Thông tin nhóm:** Nhóm gồm 4 thành viên.  
**Tóm tắt chung:** Dự án là một hệ thống ứng dụng Desktop bằng Java Swing theo mô hình kiến trúc MVC. 

---

## 1. Các hạng mục ĐÃ THỰC HIỆN 
*(Thực hiện bởi: VTGH)*

VTGH đã hoàn thiện cấu trúc cốt lõi của dự án và các tính năng quản lý cơ bản (Master Data). Chứa nền tảng để các thành viên khác phát triển tiếp.

*   **Thiết lập dự án:** Kiến trúc MVC, cấu hình Database (MySQL), build công cụ bằng Maven (làm file `pom.xml` với các thư viện cần thiết).
*   **Giao diện & Điều hướng (UI/UX):** Màn hình đăng nhập (`Login.java`), giao diện chính quản lý cho Admin (`GeneralAdmin.java`) và cho nhân viên thu ngân (`GeneralStaff.java`). Giao diện cài đặt (`SettingPanel.java`).
*   **Quản Lý Nhân Viên:** `EmployeePanel.java`, `EmployeeDAO.java`, `Employee.java`. Đã viết script SQL tạo bảng và seed dữ liệu mẫu.
*   **Quản Lý Phim:** `MoviePanel.java`, `MovieDAO.java`, `Movie.java`. Đã có script quản lý phim.
*   **Quản Lý Phòng Chiếu & Danh sách ghế:** `HallPanel.java`, `HallDao.java`, `CinemaHall.java`, `Seat.java`. Có tính năng thao tác và xem trực quan sơ đồ ghế tùy theo loại phòng chiểu (Standard, IMAX).
*   **Phân quyền hệ thống:** Có Logic xử lý đăng nhập (`LoginController.java`) để tách biệt màn hình người dùng quản lý (Admin / Manager) và nhân viên bán hàng (Staff).

---

## 2. Các chức năng CÒN THIẾU & ĐẶC TẢ CHI TIẾT
*(Dành cho 1 trong 3 thành viên còn lại hoặc do VTGH đảm nhận theo phân công của nhóm)*

Để hệ thống rạp phim hoạt động chính xác dựa trên yêu cầu từ trước, sau đây là đặc tả hệ thống và cơ sở dữ liệu các phần còn thiếu:

### 2.1. Phân hệ Quản Trị Lịch Chiếu (Showtimes & Scheduling)
*   **Mô tả/Đặc tả:** Lập lịch các suất chiếu cho từng bộ phim tại từng phòng. Kiểm tra thời gian logic, tự động báo lỗi hoặc khóa lưu nếu trong cùng một rạp lại bị xếp **trùng giờ** của 2 suất chiếu phim khác nhau. 
*   **Giao diện:** Thiết kế màn hình `SchedulePanel.java` hiển thị quản lý thông tin các suất xếp lịch chiếu. 
*   **Database (Các bảng yêu cầu mới):** 
    *   Bảng `showtimes` (Gồm các trường: `id`, `movie_id`, `hall_id`, `show_date`, `start_time`, `end_time`, `status`).
*   **Gợi ý code:** Tạo mô hình `Showtime.java` và class thao tác CSDL `ShowtimeDAO.java`.

### 2.2. Phân hệ Bán Vé & Đặt Chỗ (Ticket Booking System) 🌟
*   **Mô tả/Đặc tả:** Đây là chức năng cốt lõi nhất. Flow hoạt động: *Tìm kiếm suất chiếu -> Chọn suất phù hợp -> Hiện sơ đồ trạng thái ghế CỦA SUẤT CHIẾU ĐÓ (ghế trống, ghế đang hỏng, ghế người khác đã mua) -> Bấm chọn ghế trống -> Xác nhận thông tin và Thanh toán hóa đơn -> Xuất / In file vé.* 
*   **Giao diện:** Màn hình quy trình gồm `BookingPanel.java`, Hộp thoại `SeatSelectionDialog.java`, Phần hiển thị thông tin tiền `PaymentDialog.java`.
*   **Database (Các bảng yêu cầu mới):**
    *   Bảng `tickets` - ghi nhận vé (Gồm: `id`, `showtime_id`, `seat_id`, `price`, `status`, `seller_id`).
    *   Bảng `invoices` - ghi nhận thanh toán hóa đơn (Gồm: `id`, `ticket_ids`, `total_amount`, `payment_method`, `created_date`).
*   **Gợi ý code:** Tạo các lớp Model và DAO tương ứng cho `Ticket` và `Invoice`. 

### 2.3. Tính Năng Quản Lý Khách Hàng (Customer & Member)
*   **Mô tả/Đặc tả:** Cho phép tạo thông tin thẻ thành viên hoặc tìm lưu lịch sử khách hàng. Khi mua vé có thể tìm sđt để cộng dồn điểm (point). Điểm có thể dùng nâng hạng hoặc chiết khấu khi mua cho lần sau.
*   **Giao diện:** Cần thêm trang `CustomerPanel.java` để CRUD thao tác thêm sửa xóa tìm kiếm thành viên.
*   **Database (Các bảng yêu cầu mới):**
    *   Bảng `customers` (Gồm: `id`, `name`, `phone`, `email`, `member_level`, `points`, `registered_at`).
*   **Gợi ý code:** Các lớp `Customer.java` và `CustomerDAO.java`.

### 2.4. Tính Năng Bán Đồ Ăn Ưống (Concessions/Food & Beverage)
*   **Mô tả/Đặc tả:** Quản lý kho hàng các loại bắp, nước, snack dùng trong rạp phim. Mở rộng thêm phần mua combo bắp nước ngay tại quầy hoặc lúc đang chọn vé. Tính tổng cộng thêm vào tiền vé.
*   **Giao diện:** Giao diện cho Admin quản lý kho `FoodPanel.java`. Giao diện lúc bán vé mở phần chọn combo.
*   **Database (Các bảng yêu cầu mới):**
    *   Bảng `foods` (Gồm: `id`, `name`, `category`, `price`, `stock`).
    *   Bảng `order_details` (Lưu lượng bắp nước nếu tích hợp cùng 1 hóa đơn với vé).
*   **Gợi ý code:** Model `FoodItem.java`, logic `FoodDAO`.

### 2.5. Phân hệ Báo Cáo & Thống Kê (Report Dashboard)
*   **Mô tả/Đặc tả:** Viết các báo cáo trực quan cung cấp số liệu phân tích: Doanh thu theo thời gian, Thông số Phim nào có vé bán chạy, Nhân viên bán vé được nhiều nhất. Cung cấp nút tiện lợi Xuất (Export) biểu mẫu thông tin ra Excel hay PDF báo cáo ca quản lý.
*   **Giao diện:** Một giao diện hiện biểu đồ `ReportPanel.java` (Sử dụng các chart vẽ bar chart / pie chart).
*   **Database:** Tái sử dụng bảng `tickets`, `invoices`. Viết các lệnh `SELECT`, `JOIN`, `SUM()`, `GROUP BY` phức tạp.
*   **Gợi ý code:** Logic tập trung ở `ReportDAO.java`.

---

## 3. Ghi Chú & Thỏa Thuận Nhóm Giới Thiệu
*   **Code Pattern**: Ba thành viên còn lại khi tiếp nhận làm hãy tham khảo chuẩn viết Swing Event, thiết kế UI logic, và SQL trong các file như VTGH đã viết (`EmployeeDAO.java`, `EmployeePanel.java`) để mã nguồn được đồng bộ mô hình MVC.
*   **Về SQL**: Khi ai hoàn thiện bảng nào hay cần Database nào mới, KHÔNG nên ấn tạo thủ công trên trình MySQL Workbench. Phải code ra lệnh Sql (DDL) và lưu thành file `.sql` để chung vào thư mục `database/scripts/`. Ai pull code về chỉ cần chạy lại file sql này là đồng bộ Database liền mạch.
*   **Về Maven / Pom**: Để làm được xuất in hóa đơn vào file PDF hay excel Báo Cáo, thành viên đảm nhận cần bổ sung tag `<dependency>` vào cuối file `pom.xml` (sử dụng thư viện `iText` (in PDF), `Apache POI` (làm excel Excel)).

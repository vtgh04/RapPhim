# Tài liệu Yêu cầu Nghiệp vụ (BRD) & Chiến lược Sản phẩm
**Dự án:** Hệ thống Quản lý và Bán vé Rạp chiếu phim (CinePro)
**Trạng thái:** Đã phê duyệt | **Phiên bản:** 1.0

---

## 1. Tóm tắt Dự án
CinePro là một hệ thống ứng dụng desktop quản lý rạp chiếu phim (POS) toàn diện, được thiết kế để xử lý luồng vận hành thực tế của một rạp chiếu phim. Hệ thống tập trung vào việc quản lý lịch chiếu, trạng thái ghế ngồi, quy trình bán vé/thanh toán và cung cấp báo cáo thống kê cho ban quản lý.

## 2. Phạm vi Nghiệp vụ (System Domain)
Hệ thống tích hợp các phân hệ nghiệp vụ cốt lõi sau:
*   **Quản lý Phim:** Lưu trữ thông tin phim (Tiêu đề, Thời lượng, Xếp hạng, Poster, Trạng thái).
*   **Quản lý Lịch chiếu:** Lên lịch chiếu kết nối giữa Phim và Phòng chiếu.
*   **Sơ đồ Ghế & Bán vé:** Giao diện POS trực quan theo thời gian thực để chọn ghế và tạo vé.
*   **Hóa đơn & Thanh toán:** Lưu vết giao dịch và xuất file PDF.
*   **Hệ thống Khuyến mãi:** Xác thực mã giảm giá để trừ tiền trên tổng giỏ hàng.
*   **Quản lý Nhân viên:** Phân quyền (RBAC) giữa Nhân viên bán vé và Quản lý.
*   **Báo cáo & Thống kê:** Biểu đồ doanh thu 30 ngày và top phim bán chạy nhất.

## 3. Phân tích Các bên Liên quan (Stakeholders)
### 3.1 Vai trò & Quyền hạn
| Đối tượng (Actor) | Mô tả | Quyền hạn Hệ thống |
| :--- | :--- | :--- |
| **Quản lý (Manager)** | Người điều hành, ra quyết định chiến lược. | Toàn quyền (CRUD) Phim, Lịch chiếu, Nhân viên, Mã giảm giá. Xem Dashboard & Tất cả Hóa đơn. |
| **Nhân viên (Staff)** | Nhân viên quầy vé phục vụ khách hàng trực tiếp. | Bán vé, Áp dụng Khuyến mãi, Xem lịch sử giao dịch cá nhân. (Bị chặn quyền vào Cài đặt/Dashboard). |

### 3.2 Mục tiêu & "Nỗi đau" (Pain Points)
*   **Mục tiêu:** Tối đa hóa tốc độ bán vé trong giờ cao điểm (ví dụ: ngày công chiếu phim hot) và đảm bảo 0% tỷ lệ trùng ghế.
*   **Vấn đề vận hành:** Các hệ thống cũ thường có luồng thanh toán chậm, dễ gây lỗi trùng ghế (double-booking) khi nhiều nhân viên cùng bán. Giao diện phức tạp khiến việc đào tạo nhân viên mới mất nhiều thời gian.
*   **Mục tiêu Kinh doanh:** Cung cấp báo cáo doanh thu chính xác mỗi ngày để ngăn chặn thất thoát tài chính.

---

## 4. Yêu cầu Hệ thống (SRS Extract)

### 4.1 Yêu cầu Nghiệp vụ (Business Requirements)
*   **BR-01:** Hệ thống phải hoàn tất toàn bộ quy trình bán vé (từ lúc chọn ghế đến khi in hóa đơn) trong thời gian dưới 15 giây.
*   **BR-02:** Hệ thống phải áp dụng phân quyền nghiêm ngặt để tránh nhân viên quầy vé truy cập vào các tính năng quản trị và gian lận.

### 4.2 Yêu cầu Chức năng (Functional Requirements)
*   **FR-01 (Lịch chiếu):** Hệ thống không được phép cho lưu lịch chiếu nếu thời gian bị trùng lặp trong cùng một Phòng chiếu.
*   **FR-02 (Ghế ngồi):** Hệ thống phải tự động khóa ghế (chuyển sang màu xám/đỏ) ngay khi `status = BOOKED` và không cho phép chọn lại.
*   **FR-03 (Thanh toán):** Hệ thống phải tự động sinh `Mã Hóa Đơn` (Invoice ID), `Mã Vé` (Ticket IDs), và Mã vạch (Barcode) duy nhất trong lúc thanh toán.
*   **FR-04 (Xuất file):** Hệ thống phải hỗ trợ xuất Biên lai dạng PDF và dữ liệu giao dịch ra file Excel.

### 4.3 Yêu cầu Phi chức năng (Non-Functional Requirements)
*   **NFR-01 (Hiệu năng):** Dữ liệu Dashboard thống kê doanh thu 30 ngày phải được tính toán và tải xong trong < 2 giây.
*   **NFR-02 (Tính Toàn vẹn / ACID):** Giao dịch thanh toán phải đạt 100% atomic. Nếu in PDF lỗi, việc lưu dữ liệu vẫn phải thành công. Nếu lưu dữ liệu lỗi, toàn bộ thao tác (lưu vé, cập nhật trạng thái ghế) phải bị rollback.
*   **NFR-03 (Trải nghiệm UI):** Sơ đồ ghế ngồi phải dùng mã màu quen thuộc (Đỏ = Đã đặt, Xanh/Đang chọn = Selected, Vàng = VIP).

### 4.4 Ràng buộc & Giả định (Constraints & Assumptions)
*   **Ràng buộc:** Ứng dụng là phần mềm Desktop viết bằng Java Swing, chạy trên Windows.
*   **Giả định:** Database Server (SQL Server) nằm trên cùng mạng nội bộ (LAN) với độ trễ thấp.

---

## 5. Đề xuất Cải tiến Sản phẩm (Product Thinking)

### 5.1 Vấn đề Trải nghiệm (UX) & Điểm nghẽn
*   **Điểm nghẽn hiện tại:** Nhân viên phải cuộn tìm suất chiếu trong một danh sách khá dài.
*   **Khả năng mở rộng:** Do là app Desktop (Swing) kết nối trực tiếp JDBC, việc mở rộng cho khách hàng tự đặt vé (Mobile App/Web) sẽ gặp khó khăn.

### 5.2 Lộ trình Nâng cấp (Roadmap)
*   **Mở rộng (Pessimistic Locking):** Thêm tính năng "Giữ ghế" (Seat Hold). Khi một nhân viên click vào ghế, ghế đó bị khóa trong 3 phút trên toàn hệ thống để các máy khác không thể chọn, chống xung đột từ sớm.
*   **Trải nghiệm Khách hàng:** Hỗ trợ màn hình phụ (Dual-screen) để khách hàng thấy sơ đồ ghế khi đang mua.
*   **Thanh toán:** Tích hợp API tạo mã QR thanh toán động (VNPay/Momo) hiển thị ngay trên màn hình POS.

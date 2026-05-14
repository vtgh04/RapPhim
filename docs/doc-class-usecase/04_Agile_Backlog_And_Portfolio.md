# Product Backlog & Quản lý Dự án (Agile/Scrum)
**Dự án:** Hệ thống Quản lý và Bán vé Rạp chiếu phim (CinePro)

---

## 1. Product Backlog (Danh sách Yêu cầu Hệ thống)

### 1.1 Danh sách Epics & User Stories

#### Epic 1: Giao diện Bán vé & Thanh toán tại Quầy
**Mô tả:** Xây dựng một hệ thống POS mạnh mẽ để nhân viên bán vé cho khách hàng một cách nhanh chóng và chính xác.

| Ticket ID | Loại | Độ ưu tiên | Điểm (Story Points) | Tiêu đề (User Story) |
| :--- | :--- | :--- | :--- | :--- |
| POS-01 | Story | Rất Cao | 8 | Là một Nhân viên, tôi muốn xem sơ đồ ghế trực quan để có thể chọn ghế trống cho khách hàng. |
| POS-02 | Story | Cao | 5 | Là một Nhân viên, tôi muốn có khả năng nhập mã giảm giá vào giỏ hàng để hệ thống tự động trừ tiền thanh toán. |
| POS-03 | Story | Rất Cao | 13 | Là Hệ thống, tôi phải thực hiện thao tác Thanh toán (Checkout) dưới dạng một Transaction cơ sở dữ liệu nguyên tử (atomic) để chống lỗi trùng ghế. |

**Tiêu chí Chấp nhận (Acceptance Criteria) cho POS-03 (Transaction Thanh toán):**
*   **GIVEN (Trong điều kiện):** Nhân viên đã chọn xong ghế vào giỏ hàng.
*   **WHEN (Khi):** Nhân viên nhấn nút "Thanh toán".
*   **THEN (Thì):** Hệ thống phải tiến hành khóa ghế, sinh mã hóa đơn, sinh mã vé và lưu dữ liệu.
*   **AND (Và):** Nếu quá trình chèn dữ liệu thất bại ở bất kỳ bảng nào, toàn bộ các thao tác trước đó trong luồng thanh toán phải được rollback hoàn toàn để bảo vệ tính đúng đắn của dữ liệu.

#### Epic 2: Bảng Điều khiển Quản trị & Báo cáo Doanh thu
**Mô tả:** Cung cấp cho Quản lý các công cụ giám sát vận hành và theo dõi doanh thu.

| Ticket ID | Loại | Độ ưu tiên | Điểm (Story Points) | Tiêu đề (User Story) |
| :--- | :--- | :--- | :--- | :--- |
| MGT-01 | Story | Cao | 5 | Là một Quản lý, tôi muốn xem biểu đồ dạng đường thể hiện doanh thu trong 30 ngày gần nhất để phân tích biến động kinh doanh. |
| MGT-02 | Story | Trung bình | 3 | Là một Quản lý, tôi muốn xuất lịch sử giao dịch ra file Excel để nộp cho phòng kế toán. |
| MGT-03 | Story | Cao | 8 | Là một Quản lý, tôi muốn xếp lịch chiếu phim, và hệ thống phải cảnh báo tôi nếu thời gian suất chiếu mới bị trùng lặp với suất chiếu cũ trong cùng một phòng chiếu. |

---

## 2. Tiêu chuẩn Hoàn thành (Definition of Done)
*   **Code Quality:** Không có lỗi nghiêm trọng (Critical/Blocker). Code phải được review và tuân thủ mô hình 3 Lớp (UI -> Service -> DAO).
*   **Testing:** Vượt qua toàn bộ các Unit test cốt lõi, đặc biệt là bài test cho luồng `SaleService.processCheckout()` để đảm bảo không bị double-booking.
*   **Integration:** Các tính năng xuất PDF hóa đơn và vé phải hoạt động thành công không làm crash ứng dụng chính.
*   **UI/UX:** Giao diện đã được căn chỉnh không bị vỡ layout, các trạng thái của hệ thống (loading, success, error) đã hiển thị đầy đủ thông báo tiếng Việt cho người dùng.

# TÀI LIỆU YÊU CẦU NGHIỆP VỤ (BUSINESS REQUIREMENTS DOCUMENT - BRD)
## Dự án: Hệ Thống Quản Lý Rạp Chiếu Phim & POS RapPhim

---

## 1. Bối Cảnh Dự Án (Project Context)
Trong thời đại số hóa hiện nay, trải nghiệm khách hàng tại các hệ thống dịch vụ giải trí, đặc biệt là rạp chiếu phim, đòi hỏi sự nhanh chóng, chính xác và đồng bộ. Các phương pháp quản lý thủ công hoặc ứng dụng desktop đơn lẻ (như Java Swing cũ) bộc lộ nhiều hạn chế về khả năng mở rộng, báo cáo thời gian thực và đồng bộ hóa trạng thái ghế giữa các quầy bán vé.

Dự án **RapPhim** được thực hiện nhằm hiện đại hóa toàn bộ hệ thống quản lý rạp chiếu phim, chuyển đổi từ ứng dụng desktop Java Swing cũ sang kiến trúc **Web Application** hiện đại. Hệ thống mới phân tách rõ ràng giữa **Backend REST API (Spring Boot)** và **Frontend Client (React SPA)**, tích hợp luồng dữ liệu thời gian thực (realtime) nhằm giải quyết dứt điểm các bài toán kinh doanh thực tế.

---

## 2. Mục Tiêu Chiến Lược (Business Goals)
* **Tối ưu hóa quy trình bán vé (POS):** Giảm thời gian giao dịch tại quầy xuống dưới 30 giây cho mỗi lượt chọn ghế và thanh toán.
* **Đồng bộ hóa dữ liệu thời gian thực:** Loại bỏ hoàn toàn tình trạng trùng lặp ghế (Double Booking) khi nhiều nhân viên cùng thao tác bán vé cùng lúc tại các quầy khác nhau nhờ công nghệ WebSocket.
* **Nâng cao năng lực quản trị (Management & Analytics):** Cung cấp các biểu đồ doanh thu trực quan, biểu đồ thị phần phim bán chạy theo thời gian thực giúp quản lý rạp đưa ra quyết định phân phối suất chiếu hợp lý.
* **Hỗ trợ đa ngôn ngữ và linh hoạt thao tác:** Ứng dụng tích hợp chuyển đổi ngôn ngữ Việt - Anh linh hoạt để phù hợp với cả nhân viên người nước ngoài hoặc mở rộng chuỗi rạp.

---

## 3. Các Bên Liên Quan (Stakeholders)
| Vai trò | Mô tả nhiệm vụ & Quyền lợi | Quyền truy cập hệ thống |
| :--- | :--- | :--- |
| **Quản lý rạp (Manager)** | Theo dõi doanh thu, cấu hình danh mục phim, quản lý suất chiếu, sơ đồ phòng chiếu và cấu hình mã giảm giá. | Toàn quyền (Dashboard, Movies, Showtimes, Halls, Discounts). |
| **Nhân viên quầy vé (Staff)** | Tiếp đón khách hàng, tra cứu phim, chọn ghế trên sơ đồ trực quan, áp dụng mã giảm giá và in hóa đơn/vé cho khách. | Quyền bán vé & POS (Movies, Showtimes, Seat Grid, Checkout). |
| **Khách xem phim (End User)** | Xem lịch chiếu, chọn ghế, đánh giá và nhận xét phim (Reviews) trực tiếp trên trang chủ. | Client UI (Movie Detail, Reviews, Live seat map). |
| **Đội ngũ Vận hành / IT** | Bảo trì hệ thống, quản lý cơ sở dữ liệu, giám sát WebSocket kết nối. | Quản trị DB & Máy chủ. |

---

## 4. Phạm Vi Hệ Thống (Product Scope)

### Trong Phạm Vi (In-Scope)
* **Cơ chế xác thực an toàn:** Đăng nhập và tự động quay vòng token (JWT Access & Refresh Token) phân quyền chặt chẽ giữa Manager và Staff.
* **Giao diện bán vé POS trực quan:** Sơ đồ phòng chiếu tương tác vẽ động theo hàng và cột của phòng, phân biệt rõ ghế VIP (giá cao hơn), ghế Thường, ghế hỏng, ghế đã bán.
* **Đặt ghế Realtime:** Đồng bộ trạng thái ghế đang chọn (Locked) tức thời tới tất cả các quầy bán vé khác qua WebSocket.
* **Xử lý thanh toán mô phỏng (Payment Strategy):** Cho phép chọn các phương thức như Tiền mặt, Thẻ ngân hàng, Ví điện tử và tự động áp dụng chiết khấu/mã giảm giá.
* **Xuất hóa đơn & Vé chuyên nghiệp:** Tự động tạo và lưu trữ hóa đơn PDF, file vé chứa mã vạch (Barcode) định dạng PDF để phục vụ công tác kiểm vé tại cửa phòng chiếu.
* **Quản trị Suất chiếu trực quan:** Giao diện lập lịch kéo thả/chọn thẻ nhanh, kiểm tra xung đột phòng chiếu tự động (không cho phép 2 phim chiếu cùng một phòng tại cùng một khoảng thời gian).
* **Báo cáo tài chính trực quan:** Dashboard thống kê doanh thu 30 ngày gần nhất vẽ biểu đồ động SVG (Bar, Line, Area), thống kê cơ cấu vé bán theo phim (Donut Chart).

### Ngoài Phạm Vi (Out-of-Scope trong Phase này)
* **Tích hợp cổng thanh toán trực tuyến thực tế (VNPAY, Momo, Stripe API):** Hiện tại chỉ dùng mock Payment Strategy ở phía Backend.
* **Hệ thống đặt vé trực tuyến dành cho khách hàng tự phục vụ (B2C Booking Web):** Phiên bản hiện tại tập trung làm POS bán vé tại quầy (B2B/POS).

---

## 5. Các Chỉ Số KPI Đánh Giá Hiệu Năng (Key Metrics & KPIs)
1. **Tốc độ phản hồi API:** 95% số request API hoàn tất dưới 200ms.
2. **Đồng bộ Realtime:** Độ trễ truyền phát trạng thái khóa ghế qua WebSocket dưới 50ms.
3. **Độ ổn định dữ liệu:** 100% các giao dịch thanh toán thành công phải tạo lập hóa đơn và vé tương ứng một cách toàn vẹn (ACID transaction).
4. **Hiệu suất in ấn:** Thời gian render PDF hóa đơn và vé dưới 1.5 giây sau khi checkout thành công.

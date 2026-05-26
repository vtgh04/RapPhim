# 🍿 RapPhim - Modern Cinema POS & Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring--Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React" />
  <img src="https://img.shields.io/badge/Vite-6-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite" />
  <img src="https://img.shields.io/badge/SQL--Server-2019%2B-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white" alt="SQL Server" />
  <img src="https://img.shields.io/badge/WebSocket-STOMP-blue?style=for-the-badge&logo=socket.io&logoColor=white" alt="WebSocket" />
</p>

**Hệ thống bán vé (POS) tại quầy và quản trị rạp chiếu phim chuyên nghiệp được thiết kế theo kiến trúc Web hiện đại, phân tách rõ rệt giữa Spring Boot Backend REST API và React Client (SPA) sử dụng công nghệ WebSocket đồng bộ thời gian thực.**

---

## 📑 Thư Mục Tài Liệu Phân Tích Nghiệp Vụ & Thiết Kế (BA Docs)

Hệ thống được thiết kế và đặc tả chuyên nghiệp theo chuẩn tài liệu BA. Bạn có thể truy cập các tài liệu chi tiết tại đây:
1. **[Tài liệu Yêu cầu Nghiệp vụ (BRD)](docs/1.BRD/BRD.md):** Bối cảnh, mục tiêu chiến lược, sơ đồ phân quyền Stakeholders, phạm vi (In-scope/Out-of-scope) và các chỉ số KPIs đánh giá hiệu năng hệ thống.
2. **[Đặc tả Yêu cầu Phần mềm (SRS)](docs/2.SRS/SRS.md):** Danh sách User Stories, Luật nghiệp vụ tính giá vé & kiểm tra trùng lịch chiếu, Đặc tả Use Case và luồng đi chi tiết của hệ thống.
3. **[Thiết kế Cơ sở Dữ liệu & Class](docs/3.System_Design/Database_Design.md):** Từ điển dữ liệu chi tiết của 9 bảng, đặc tả khoá chính/ngoại, kiểu dữ liệu và cách ánh xạ đối tượng Entity JPA của Hibernate.

---

## 🎨 Sơ Đồ Hệ Thống Trực Quan (SVG Diagrams)

### 1. Kiến Trúc Tổng Quan (System Architecture)
Mô hình 4 lớp (Client SPA, Security/API Gateway, Core Service, Data Layer) cùng cơ chế giao tiếp HTTP REST và Realtime WebSocket.

![Sơ đồ tổng quan kiến trúc](docs/assets/diagrams/overview.svg)

---

### 2. Sơ đồ Use Case phân quyền người dùng (Use Case Diagram)
Phân quyền chức năng rạch ròi giữa vai trò Quản lý (Manager) và Nhân viên bán vé (Staff).

![Sơ đồ Use Case](docs/assets/diagrams/use_case.svg)

---

### 3. Sơ đồ Trình tự Đặt Vé & Thanh Toán (Sequence Diagram)
Trình tự tương tác giữa Nhân viên quầy, Client, WebSocket Broker, Server và Database trong suốt vòng đời chọn ghế, khóa ghế tạm thời và checkout in hóa đơn.

![Sơ đồ Trình tự](docs/assets/diagrams/sequence.svg)

---

### 4. Sơ đồ Thực Thể Quan Hệ (Entity Relationship Diagram - ERD)
Cơ sở dữ liệu được chuẩn hoá 3NF của hệ thống chạy trên Microsoft SQL Server.

![Sơ đồ ERD](docs/assets/diagrams/erd.svg)

---

### 5. Sơ đồ Lớp Thực Thể (Domain Class Diagram)
Cấu trúc lớp Domain JPA Entity trong Spring Boot Backend mô tả đầy đủ các thuộc tính và quan hệ liên kết.

![Sơ đồ Class Diagram](docs/assets/diagrams/class_diagram.svg)

---

## 🌟 Tính Năng Nổi Bật Đang Hoạt Động
* **Xác thực Dual-Token JWT:** Bảo mật tuyệt đối với Access Token & Refresh Token, tự động xoay vòng token ngầm ở Client để không ngắt quãng phiên làm việc.
* **Giao diện bán vé POS & Sơ đồ ghế động:** Vẽ động bản đồ phòng chiếu theo số hàng và cột của phòng, phân biệt ghế VIP/Thường, ghế hỏng, ghế đã mua.
* **Đồng bộ ghế Realtime (WebSocket/STOMP):** Ngăn chặn hoàn toàn việc mua trùng ghế (Double Booking) giữa các quầy bán vé.
* **Tự động phòng tránh xung đột lịch chiếu:** Thuật toán Backend tự động tính toán thời gian kết thúc dựa vào thời lượng phim, chặn lưu suất chiếu nếu phát hiện phòng bị trùng hoặc chồng lấn thời gian.
* **Cascade Delete Showtime:** Xóa suất chiếu tự động xóa sạch dữ liệu ghế động liên quan trong cùng một ranh giới `@Transactional` an toàn.
* **Xuất hoá đơn & Vé PDF chứa Barcode:** Sử dụng Apache POI và iText xuất hoá đơn kèm vé có mã vạch sẵn sàng phục vụ kiểm vé.
* **Dashboard Biểu đồ SVG Động:** Dashboard thống kê doanh thu, cơ cấu vé bán hiển thị bằng biểu đồ SVG do hệ thống tự tính toán toạ độ vẽ, không phụ thuộc vào thư viện bên thứ ba.
* **Đa ngôn ngữ (i18n):** Chuyển đổi ngôn ngữ Việt - Anh tức thời trên toàn bộ giao diện.

---

## 🏃 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### Điều kiện tiên quyết
* **Java JDK 17** hoặc cao hơn
* **Node.js** (v18+) & **npm**
* **Microsoft SQL Server** (đã chạy các script khởi tạo tại `backend/database/scripts/Database.sql` và `Seed.sql`)

### 1. Khởi chạy Backend API
```bash
cd backend
# Cấu hình db credentials trong src/main/resources/application.properties trước khi chạy
.\mvnw.cmd spring-boot:run
```
*Backend API sẽ chạy tại cổng `5001`.*

### 2. Khởi chạy Frontend Client
```bash
cd frontend
npm install
npm run dev
```
*Frontend sẽ chạy tại cổng `3000` (hoặc cổng được Vite cấp phát, proxy tự động trỏ về backend cổng `5001`).*

---

## 🐙 Tác Giả & Bản Quyền

* **GitHub:** [@vtgh04](https://github.com/vtgh04)
* **Email:** vtgh1602@gmail.com

*Nếu dự án này giúp ích cho bạn, hãy tặng 1 ⭐ trên repository nhé!*

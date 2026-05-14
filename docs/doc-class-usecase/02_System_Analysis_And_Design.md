# Phân tích Hệ thống & Thiết kế Kiến trúc
**Dự án:** Hệ thống Quản lý và Bán vé Rạp chiếu phim (CinePro)

---

## 1. Phân tích Kiến trúc
Dự án áp dụng chặt chẽ **Kiến trúc 3 Lớp (3-Tier Architecture)** nhằm đảm bảo tính phân tách (Separation of Concerns), dễ bảo trì và dễ mở rộng.

### 1.1 Sơ đồ Kiến trúc

```mermaid
graph TD
    subgraph ViewLayer [Lớp Giao diện]
        UI[UI Panels & Dialogs]
        Controllers[Event Controllers]
    end

    subgraph ServiceLayer [Lớp Nghiệp vụ]
        AuthSvc[AuthService]
        SaleSvc[SaleService]
        ShowSvc[ShowtimeService]
        OtherSvc[Các Service khác...]
    end

    subgraph DAOLayer [Lớp Truy cập Dữ liệu]
        SaleDAO[InvoiceDAO & TicketDao]
        ShowDAO[ShowtimeDAO]
        UserDAO[EmployeeDAO]
    end

    subgraph DBLayer [Database]
        SQL[(SQL Server)]
    end

    ViewLayer -->|DTOs / Gọi Hàm| ServiceLayer
    ServiceLayer -->|Entity Models| DAOLayer
    DAOLayer -->|JDBC/SQL| DBLayer
    
    %% Quản lý Transaction
    ServiceLayer -.->|Quản lý Commit/Rollback| DBLayer
```

### 1.2 Trách nhiệm từng Lớp
*   **Lớp Giao diện (UI/Panel):** Nhận input từ người dùng, kiểm tra định dạng giao diện, và gọi các phương thức trong Service. *Tuyệt đối không chứa logic SQL.*
*   **Lớp Nghiệp vụ (Service):** Điều phối luồng xử lý. Ví dụ: `SaleService.processCheckout()` quản lý transaction, sinh mã ID, cập nhật trạng thái ghế và lưu hóa đơn - tất cả nằm trong một chu trình nguyên tử (atomic).
*   **Lớp Dữ liệu (DAO):** Chuyên tương tác cơ sở dữ liệu. Chỉ chứa SQL thô (`INSERT`, `SELECT`, `UPDATE`), xử lý `PreparedStatement` và ánh xạ `ResultSet`.

---

## 2. Thiết kế Cơ sở Dữ liệu (ERD & Data Dictionary)

### 2.1 Sơ đồ Thực thể - Liên kết (ERD)

```mermaid
erDiagram
    employees ||--o{ invoices : creates
    invoices ||--o{ tickets : includes
    discounts ||--o{ tickets : applies_to
    show_seats ||--o{ tickets : generates
    showtimes ||--o{ show_seats : has
    seats ||--o{ show_seats : mapped_to
    cinema_halls ||--o{ seats : contains
    cinema_halls ||--o{ showtimes : hosts
    movies ||--o{ showtimes : scheduled_for

    employees {
        string employee_id PK
        string full_name
        string username
        string password
        string role
        string status
        string phone
        string email
    }
    invoices {
        string invoice_id PK
        string employee_id FK
        datetime created_at
        float total_amount
        int total_tickets
        string payment_method
        string status
        string note
    }
    tickets {
        string ticket_id PK
        string invoice_id FK
        string show_seat_id FK
        string discount_id FK
        string barcode
        float original_price
        float discount_amount
        float final_price
        datetime issued_at
    }
    discounts {
        string discount_id PK
        string discount_name
        string discount_type
        float discount_rate
        datetime valid_from
        datetime valid_to
        int min_ticket_quantity
        bit is_active
    }
    show_seats {
        string show_seat_id PK
        string showtime_id FK
        string seat_id FK
        float price
        string status
        datetime held_until
    }
    showtimes {
        string showtime_id PK
        string movie_id FK
        string hall_id FK
        datetime start_time
        datetime end_time
        float base_price
        string status
    }
    movies {
        string movie_id PK
        string title
        string genre
        int duration_mins
        string format_movie
        string rating
        string language
        date release_date
        string status
        string description
    }
    cinema_halls {
        string hall_id PK
        string name
        string hall_type
        int total_rows
        int total_cols
        string status
    }
    seats {
        string seat_id PK
        string hall_id FK
        string row_char
        int col_number
        string seat_type
        float seat_factor
        bit is_broken
    }
```

### 2.2 Từ điển Dữ liệu (Các bảng Transaction cốt lõi)
| Tên Bảng | Mô tả | Ràng buộc & Chỉ mục (Indexes) |
| :--- | :--- | :--- |
| `show_seats` | Quản lý trạng thái động của một chiếc ghế vật lý trong một suất chiếu cụ thể. | Trạng thái: `AVAILABLE`, `BOOKED`, `BROKEN`. Khuyến nghị tạo Index trên `showtime_id, seat_id` để tăng tốc độ tìm kiếm khi thanh toán. |
| `tickets` | Vé vật lý / vé điện tử cấp cho khách hàng. | FK liên kết tới `invoice_id`. Unique constrain trên cột `barcode`. |
| `showtimes` | Ngăn chặn trùng lặp lịch chiếu dựa trên thời gian. | Cần có Check constraint đảm bảo `end_time > start_time`. |

### 2.3 Quản lý Giao dịch (Transaction) & Tính toàn vẹn (ACID)
Thao tác phức tạp và nhạy cảm nhất là **Thanh toán vé (Checkout)**.
Để chống lỗi xung đột (ví dụ: 2 nhân viên bán cùng 1 ghế tại 1 giây), `SaleService` buộc phải vô hiệu hóa auto-commit (`conn.setAutoCommit(false)`). 
1. Đọc bảng `show_seats` để kiểm tra `status = 'AVAILABLE'`.
2. Cập nhật `show_seats` thành `BOOKED`.
3. Thêm mới dữ liệu vào `Invoices` và `Tickets`.
4. Hoàn tất giao dịch (`conn.commit()`).
*Chỉ cần một thao tác thất bại, toàn bộ khối lệnh sẽ bị rollback, bảo toàn dữ liệu tuyệt đối.*

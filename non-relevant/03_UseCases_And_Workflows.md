# Use Cases, Luồng Nghiệp vụ (Workflows) & Quy trình
**Dự án:** Hệ thống Quản lý và Bán vé Rạp chiếu phim (CinePro)

---

## 1. Sơ đồ Use Case Tổng thể

```mermaid
flowchart LR
    Staff([Nhân viên])
    Manager([Quản lý])

    subgraph POS System [Hệ thống Bán vé - POS]
        UC1(UC1: Đăng nhập)
        UC2(UC2: Chọn Phim & Suất chiếu)
        UC3(UC3: Chọn Ghế trên Sơ đồ)
        UC4(UC4: Áp dụng Mã giảm giá)
        UC5(UC5: Thanh toán & In Vé)
    end

    subgraph Management System [Hệ thống Quản trị]
        UC6(UC6: Quản lý Phim)
        UC7(UC7: Xếp Lịch chiếu)
        UC8(UC8: Xem Dashboard Thống kê)
        UC9(UC9: Quản lý Nhân viên)
        UC10(UC10: Quản lý Phòng chiếu)
        UC11(UC11: Quản lý Ghế ngồi)
        UC12(UC12: Quản lý Mã giảm giá)
        UC13(UC13: Xem Lịch sử Giao dịch)
        UC14(UC14: Xuất Báo cáo Excel)
        UC15(UC15: Cài đặt Hệ thống)
    end

    Staff --> UC1
    Staff --> UC2
    Staff --> UC3
    Staff --> UC4
    Staff --> UC5
    Staff --> UC13

    Manager --> UC1
    Manager --> UC6
    Manager --> UC7
    Manager --> UC8
    Manager --> UC9
    Manager --> UC10
    Manager --> UC11
    Manager --> UC12
    Manager --> UC14
    Manager --> UC15
    
    %% Quản lý kế thừa các quyền của Nhân viên
    Manager -.->|Kế thừa quyền của| Staff
```

---

## 2. Đặc tả Use Case (Use Case Specifications)

### UC5: Thanh toán & In Vé (Checkout)
| Thuộc tính | Mô tả |
| :--- | :--- |
| **Actor** | Nhân viên quầy vé (Staff) |
| **Tiền điều kiện (Pre-condition)** | Nhân viên đã đăng nhập; Có ít nhất 1 ghế trong giỏ hàng. |
| **Luồng chính (Main Flow)** | 1. Nhân viên nhấn nút 'Thanh toán'.<br>2. Hệ thống kiểm tra trạng thái ghế hiện tại.<br>3. Nhân viên chọn hình thức thanh toán.<br>4. Hệ thống tạo Mã hóa đơn và Vé.<br>5. Hệ thống đổi trạng thái ghế sang 'BOOKED'.<br>6. Hệ thống xuất file PDF và yêu cầu in. |
| **Luồng phụ (Alternative Flow)** | Nếu có nhập Mã giảm giá, hệ thống sẽ trừ tiền khuyến mãi vào tổng thanh toán ở bước 3. |
| **Luồng ngoại lệ (Exception Flow)** | Nếu ghế đã bị máy khác mua mất (Trùng ghế), hệ thống hủy thanh toán, báo lỗi cho nhân viên và làm mới lại sơ đồ ghế. |
| **Hậu điều kiện (Post-condition)** | Hóa đơn và Vé được lưu vào Database. File PDF được sinh ra. Sơ đồ ghế đã bị khóa. |

---

## 3. Phân tích Luồng Nghiệp vụ (BPMN & Flow)

### 3.1 Sơ đồ Hoạt động Bán vé (BPMN Style)

```mermaid
stateDiagram-v2
    [*] --> ChonSuatChieu
    ChonSuatChieu --> XemSoDoGhe
    
    state XemSoDoGhe {
        [*] --> ChonGhe
        ChonGhe --> KiemTraGhe
        KiemTraGhe --> ThemVaoGio: Ghế trống
        KiemTraGhe --> BaoLoi: Ghế đã đặt/Hỏng
    }
    
    XemSoDoGhe --> ApMaGiamGia
    ApMaGiamGia --> KiemTraMa
    KiemTraMa --> TinhGiamGia: Hợp lệ
    KiemTraMa --> BaoLoi: Hết hạn/Sai mã
    
    ApMaGiamGia --> ThanhToan
    XemSoDoGhe --> ThanhToan
    
    state XyLyGiaoDichThanhToan {
        [*] --> BatDauTransaction
        BatDauTransaction --> TaoMaID
        TaoMaID --> LuuHoaDon
        LuuHoaDon --> CapNhatTrangThaiGhe
        CapNhatTrangThaiGhe --> LuuVe
        LuuVe --> CommitTransaction
    }
    
    ThanhToan --> XyLyGiaoDichThanhToan
    XyLyGiaoDichThanhToan --> XuatPDF: Thành công
    XyLyGiaoDichThanhToan --> Rollback: Lỗi Database/Trùng ghế
    Rollback --> BaoLoi
    
    XuatPDF --> [*]
```

### 3.2 Sơ đồ Tuần tự (Sequence Diagram): Luồng Thanh toán Database

```mermaid
sequenceDiagram
    actor Staff as Nhân viên
    participant UI as SalePanel
    participant Service as SaleService
    participant DAO as Invoice/Ticket DAO
    participant DB as Database
    participant PDF as PDFExporter

    Staff->>UI: Nhấn nút "Thanh toán"
    UI->>Service: processCheckout(cart, total)
    
    Service->>DB: conn.setAutoCommit(false)
    activate DB
    
    Service->>DAO: getNextInvoiceId()
    DAO->>DB: SELECT MAX(invoice_id)
    DB-->>Service: Trả về ID mới
    
    Service->>DAO: insertInvoice(data)
    DAO->>DB: INSERT INTO invoices
    
    loop Xử lý từng Ghế trong Giỏ
        Service->>DAO: findShowSeatId(showtime, seat)
        DAO->>DB: SELECT show_seat_id
        DB-->>Service: Trả về show_seat_id
        
        Service->>DAO: updateShowSeatStatus('BOOKED')
        DAO->>DB: UPDATE show_seats
        
        Service->>DAO: insertTicket(data)
        DAO->>DB: INSERT INTO tickets
    end
    
    Service->>DB: conn.commit()
    deactivate DB
    
    Service->>PDF: exportTickets(invoice_id)
    PDF-->>Service: Render PDF thành công
    
    Service-->>UI: Trả về Kết quả Success(true)
    UI-->>Staff: Hiển thị "Thanh toán thành công" & Làm sạch Giỏ hàng
```

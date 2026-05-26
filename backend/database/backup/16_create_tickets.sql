-- ============================================================
--  Script : 16_create_tickets.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo bảng tickets (vé đã bán)
--
--  Nghiệp vụ:
--    - final_price = original_price - discount_amount
--    - Một show_seat chỉ có tối đa 1 vé hợp lệ (status != 'CANCELLED')
--      (đảm bảo bởi UNIQUE + kiểm tra tầng Service)
--    - Khi huỷ vé → show_seat.status = 'AVAILABLE', xoá held_until
-- ============================================================

USE RapPhim;
GO

IF OBJECT_ID(N'dbo.tickets', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.tickets;
    PRINT N'[OK] Bảng dbo.tickets cũ đã bị xoá.';
END
GO

CREATE TABLE dbo.tickets (
    ticket_id        VARCHAR(20)    NOT NULL,
    invoice_id       VARCHAR(20)    NOT NULL,
    show_seat_id     VARCHAR(20)    NOT NULL,
    discount_id      VARCHAR(20)    NULL,
    barcode          VARCHAR(100)   NULL,
    original_price   DECIMAL(12,2)  NOT NULL,
    discount_amount  DECIMAL(12,2)  NOT NULL CONSTRAINT df_tickets_discount_amount DEFAULT 0,
    final_price      DECIMAL(12,2)  NOT NULL,
    issued_at        DATETIME       NOT NULL CONSTRAINT df_tickets_issued_at DEFAULT GETDATE(),
    status           VARCHAR(20)    NOT NULL CONSTRAINT df_tickets_status DEFAULT 'VALID',

    CONSTRAINT pk_tickets              PRIMARY KEY (ticket_id),
    CONSTRAINT fk_tickets_invoice      FOREIGN KEY (invoice_id)   REFERENCES dbo.invoices(invoice_id),
    CONSTRAINT fk_tickets_show_seat    FOREIGN KEY (show_seat_id) REFERENCES dbo.show_seats(show_seat_id),
    CONSTRAINT fk_tickets_discount     FOREIGN KEY (discount_id)  REFERENCES dbo.discounts(discount_id),
    CONSTRAINT uq_tickets_barcode      UNIQUE (barcode),
    CONSTRAINT chk_tickets_orig_price  CHECK (original_price >= 0),
    CONSTRAINT chk_tickets_discount    CHECK (discount_amount >= 0),
    CONSTRAINT chk_tickets_final       CHECK (final_price >= 0),
    CONSTRAINT chk_tickets_status      CHECK (status IN ('VALID', 'USED', 'CANCELLED'))
);
GO

CREATE INDEX idx_tickets_invoice    ON dbo.tickets (invoice_id);
CREATE INDEX idx_tickets_show_seat  ON dbo.tickets (show_seat_id);
CREATE INDEX idx_tickets_status     ON dbo.tickets (status);
GO

PRINT N'[OK] Bảng dbo.tickets đã được tạo thành công.';
GO

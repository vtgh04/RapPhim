-- ============================================================
--  Script : 15_create_invoices.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo bảng invoices (hóa đơn)
--
--  Nghiệp vụ:
--    - Luồng: PENDING → (thêm tickets) → CONFIRMED | CANCELLED
--    - total_amount = SUM(final_price) của tất cả tickets thuộc invoice
--    - Huỷ invoice → huỷ toàn bộ tickets + trả show_seats về AVAILABLE
-- ============================================================

USE RapPhim;
GO

IF OBJECT_ID(N'dbo.invoices', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.invoices;
    PRINT N'[OK] Bảng dbo.invoices cũ đã bị xoá.';
END
GO

CREATE TABLE dbo.invoices (
    invoice_id      VARCHAR(20)    NOT NULL,
    employee_id     VARCHAR(20)    NOT NULL,
    created_at      DATETIME       NOT NULL CONSTRAINT df_invoices_created_at DEFAULT GETDATE(),
    total_amount    DECIMAL(12,2)  NOT NULL CONSTRAINT df_invoices_total DEFAULT 0,
    payment_method  VARCHAR(20)    NOT NULL,
    status          VARCHAR(20)    NOT NULL CONSTRAINT df_invoices_status DEFAULT 'PENDING',
    note            NTEXT          NULL,

    CONSTRAINT pk_invoices               PRIMARY KEY (invoice_id),
    CONSTRAINT fk_invoices_employee      FOREIGN KEY (employee_id) REFERENCES dbo.employees(employee_id),
    CONSTRAINT chk_invoices_total        CHECK (total_amount >= 0),
    CONSTRAINT chk_invoices_payment      CHECK (payment_method IN ('CASH', 'CARD', 'TRANSFER')),
    CONSTRAINT chk_invoices_status       CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);
GO

CREATE INDEX idx_invoices_employee  ON dbo.invoices (employee_id);
CREATE INDEX idx_invoices_status    ON dbo.invoices (status);
CREATE INDEX idx_invoices_created   ON dbo.invoices (created_at);
GO

PRINT N'[OK] Bảng dbo.invoices đã được tạo thành công.';
GO

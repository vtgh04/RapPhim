-- ============================================================
--  Script : 13_create_discounts.sql
--  DBMS   : Microsoft SQL Server 2019+
--  Mô tả  : Tạo bảng discounts (chương trình giảm giá)
--
--  Nghiệp vụ:
--    - discountRate: 0.0–1.0 (vd: 0.1 = 10%)
--    - Chỉ áp dụng khi is_active = 1 VÀ ngày hiện tại trong [valid_from, valid_to]
--    - min_ticket_quantity: kiểm tra ở tầng Service, không phải DB trigger
-- ============================================================

USE RapPhim;
GO

IF OBJECT_ID(N'dbo.discounts', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.discounts;
    PRINT N'[OK] Bảng dbo.discounts cũ đã bị xoá.';
END
GO

CREATE TABLE dbo.discounts (
    discount_id          VARCHAR(20)    NOT NULL,
    discount_name        NVARCHAR(100)  NOT NULL,
    discount_type        VARCHAR(20)    NOT NULL,
    discount_rate        DECIMAL(5,2)   NOT NULL,
    valid_from           DATE           NOT NULL,
    valid_to             DATE           NOT NULL,
    min_ticket_quantity  INT            NOT NULL CONSTRAINT df_discounts_min_qty DEFAULT 1,
    is_active            BIT            NOT NULL CONSTRAINT df_discounts_active  DEFAULT 1,
    description          NTEXT          NULL,

    CONSTRAINT pk_discounts            PRIMARY KEY (discount_id),
    CONSTRAINT chk_discounts_type      CHECK (discount_type IN ('HOLIDAY', 'GROUP', 'SPECIAL')),
    CONSTRAINT chk_discounts_rate      CHECK (discount_rate >= 0 AND discount_rate <= 1),
    CONSTRAINT chk_discounts_dates     CHECK (valid_to >= valid_from),
    CONSTRAINT chk_discounts_min_qty   CHECK (min_ticket_quantity >= 1)
);
GO

CREATE INDEX idx_discounts_type      ON dbo.discounts (discount_type);
CREATE INDEX idx_discounts_active    ON dbo.discounts (is_active);
CREATE INDEX idx_discounts_valid     ON dbo.discounts (valid_from, valid_to);
GO

PRINT N'[OK] Bảng dbo.discounts đã được tạo thành công.';
GO

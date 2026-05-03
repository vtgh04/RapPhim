IF NOT EXISTS (
    SELECT name FROM sys.databases WHERE name = N'RapPhim'
)
BEGIN
    CREATE DATABASE RapPhim
        COLLATE Vietnamese_CI_AS;
END
GO

USE RapPhim;
GO

-- Xóa các bảng cũ theo thứ tự ngược lại của Foreign Key
IF OBJECT_ID(N'dbo.tickets', N'U') IS NOT NULL DROP TABLE dbo.tickets;
IF OBJECT_ID(N'dbo.invoices', N'U') IS NOT NULL DROP TABLE dbo.invoices;
IF OBJECT_ID(N'dbo.show_seats', N'U') IS NOT NULL DROP TABLE dbo.show_seats;
IF OBJECT_ID(N'dbo.discounts', N'U') IS NOT NULL DROP TABLE dbo.discounts;
IF OBJECT_ID(N'dbo.showtimes', N'U') IS NOT NULL DROP TABLE dbo.showtimes;
IF OBJECT_ID(N'dbo.seats', N'U') IS NOT NULL DROP TABLE dbo.seats;
IF OBJECT_ID(N'dbo.cinema_halls', N'U') IS NOT NULL DROP TABLE dbo.cinema_halls;
IF OBJECT_ID(N'dbo.movies', N'U') IS NOT NULL DROP TABLE dbo.movies;
IF OBJECT_ID(N'dbo.employees', N'U') IS NOT NULL DROP TABLE dbo.employees;
GO

-- 1. employees
CREATE TABLE dbo.employees (
    employee_id   VARCHAR(20)    NOT NULL,
    full_name     NVARCHAR(100)  NOT NULL,
    username      VARCHAR(50)    NOT NULL,
    password      VARCHAR(255)   NOT NULL,
    role          VARCHAR(20)    NOT NULL,
    status        VARCHAR(20)    NOT NULL
                  CONSTRAINT df_employees_status DEFAULT 'ACTIVE',
    phone         VARCHAR(20)    NULL,
    email         VARCHAR(100)   NULL,
    CONSTRAINT pk_employees          PRIMARY KEY (employee_id),
    CONSTRAINT uq_employees_username UNIQUE (username),
    CONSTRAINT uq_employees_email    UNIQUE (email),
    CONSTRAINT chk_employees_role    CHECK (role   IN ('MANAGER', 'STAFF')),
    CONSTRAINT chk_employees_status  CHECK (status IN ('ACTIVE',  'RETIRED'))
);
GO
CREATE INDEX idx_employees_username ON dbo.employees (username);
CREATE INDEX idx_employees_status   ON dbo.employees (status);
GO

-- 2. movies
CREATE TABLE dbo.movies (
    movie_id      VARCHAR(20)    NOT NULL,
    title         NVARCHAR(200)  NOT NULL,
    genre         NVARCHAR(100)  NOT NULL,
    duration_mins INT            NOT NULL,
    format_movie  VARCHAR(20)    NOT NULL,
    rating        VARCHAR(10)    NOT NULL,
    language      NVARCHAR(50)   NULL,
    release_date  DATE           NULL,
    status        VARCHAR(20)    NOT NULL CONSTRAINT df_movies_status DEFAULT 'ACTIVE',
    description   NVARCHAR(MAX)  NULL,
    poster_url    VARCHAR(255)   NULL,
    CONSTRAINT pk_movies PRIMARY KEY (movie_id),
    CONSTRAINT chk_movies_duration CHECK (duration_mins > 0),
    CONSTRAINT chk_movies_status   CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
GO
CREATE INDEX idx_movies_title ON dbo.movies (title);
CREATE INDEX idx_movies_status ON dbo.movies (status);
GO

-- 3. cinema_halls
CREATE TABLE dbo.cinema_halls (
    hall_id    VARCHAR(20)   NOT NULL,
    name       NVARCHAR(100) NOT NULL,
    hall_type  VARCHAR(20)   NULL,
    total_rows INT           NOT NULL,
    total_cols INT           NOT NULL,
    status     VARCHAR(20)   NOT NULL CONSTRAINT df_cinema_halls_status DEFAULT 'ACTIVE',
    CONSTRAINT pk_cinema_halls        PRIMARY KEY (hall_id),
    CONSTRAINT uq_cinema_halls_name   UNIQUE (name),
    CONSTRAINT chk_cinema_halls_rows  CHECK (total_rows > 0),
    CONSTRAINT chk_cinema_halls_cols  CHECK (total_cols > 0),
    CONSTRAINT chk_cinema_halls_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
GO
CREATE INDEX idx_cinema_halls_status ON dbo.cinema_halls (status);
GO

-- 4. seats
CREATE TABLE dbo.seats (
    seat_id VARCHAR(20) PRIMARY KEY,
    hall_id VARCHAR(20) NOT NULL,
    row_char CHAR(1) NOT NULL,
    col_number INT NOT NULL,
    seat_type VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    seat_factor DECIMAL(4,2) NOT NULL DEFAULT 1.0,
    is_broken BIT DEFAULT 0,
    CONSTRAINT fk_seat_hall FOREIGN KEY (hall_id) REFERENCES dbo.cinema_halls(hall_id),
    CONSTRAINT uq_seat_hall_row_col UNIQUE (hall_id, row_char, col_number)
);
GO

-- 5. showtimes
CREATE TABLE dbo.showtimes (
    showtime_id  VARCHAR(20)    NOT NULL,
    movie_id     VARCHAR(20)    NOT NULL,
    hall_id      VARCHAR(20)    NOT NULL,
    start_time   DATETIME       NOT NULL,
    end_time     DATETIME       NOT NULL,
    base_price   DECIMAL(12,2)  NOT NULL,
    status       VARCHAR(20)    NOT NULL CONSTRAINT df_showtimes_status DEFAULT 'SCHEDULED',
    CONSTRAINT pk_showtimes           PRIMARY KEY (showtime_id),
    CONSTRAINT fk_showtimes_movie     FOREIGN KEY (movie_id) REFERENCES dbo.movies(movie_id),
    CONSTRAINT fk_showtimes_hall      FOREIGN KEY (hall_id)  REFERENCES dbo.cinema_halls(hall_id),
    CONSTRAINT chk_showtimes_endtime  CHECK (end_time > start_time),
    CONSTRAINT chk_showtimes_price    CHECK (base_price >= 0),
    CONSTRAINT chk_showtimes_status   CHECK (status IN ('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED'))
);
GO
CREATE INDEX idx_showtimes_movie_id  ON dbo.showtimes (movie_id);
CREATE INDEX idx_showtimes_hall_id   ON dbo.showtimes (hall_id);
CREATE INDEX idx_showtimes_start     ON dbo.showtimes (start_time);
CREATE INDEX idx_showtimes_status    ON dbo.showtimes (status);
GO

-- 6. discounts
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

-- 7. show_seats
CREATE TABLE dbo.show_seats (
    show_seat_id  VARCHAR(20)   NOT NULL,
    showtime_id   VARCHAR(20)   NOT NULL,
    seat_id       VARCHAR(20)   NOT NULL,
    price         DECIMAL(12,2) NOT NULL,
    status        VARCHAR(20)   NOT NULL CONSTRAINT df_show_seats_status DEFAULT 'AVAILABLE',
    held_until    DATETIME      NULL,

    CONSTRAINT pk_show_seats            PRIMARY KEY (show_seat_id),
    CONSTRAINT fk_show_seats_showtime   FOREIGN KEY (showtime_id) REFERENCES dbo.showtimes(showtime_id),
    CONSTRAINT fk_show_seats_seat       FOREIGN KEY (seat_id)     REFERENCES dbo.seats(seat_id),
    CONSTRAINT uq_show_seats_pair       UNIQUE (showtime_id, seat_id),
    CONSTRAINT chk_show_seats_price     CHECK (price >= 0),
    CONSTRAINT chk_show_seats_status    CHECK (status IN ('AVAILABLE', 'HELD', 'BOOKED'))
);
GO
CREATE INDEX idx_show_seats_showtime ON dbo.show_seats (showtime_id);
CREATE INDEX idx_show_seats_status   ON dbo.show_seats (status);
CREATE INDEX idx_show_seats_held     ON dbo.show_seats (held_until) WHERE held_until IS NOT NULL;
GO

-- 8. invoices
CREATE TABLE dbo.invoices (
    invoice_id      VARCHAR(20)    NOT NULL,
    employee_id     VARCHAR(20)    NOT NULL,
    created_at      DATETIME       NOT NULL CONSTRAINT df_invoices_created_at DEFAULT GETDATE(),
    total_amount    DECIMAL(12,2)  NOT NULL CONSTRAINT df_invoices_total DEFAULT 0,
    total_tickets   INT            NOT NULL CONSTRAINT df_invoices_tickets DEFAULT 0,
    payment_method  VARCHAR(20)    NOT NULL,
    status          VARCHAR(20)    NOT NULL CONSTRAINT df_invoices_status DEFAULT 'PENDING',
    note            NVARCHAR(MAX)  NULL,
    CONSTRAINT pk_invoices               PRIMARY KEY (invoice_id),
    CONSTRAINT fk_invoices_employee      FOREIGN KEY (employee_id) REFERENCES dbo.employees(employee_id),
    CONSTRAINT chk_invoices_total        CHECK (total_amount >= 0),
    CONSTRAINT chk_invoices_tickets      CHECK (total_tickets >= 0),
    CONSTRAINT chk_invoices_payment      CHECK (payment_method IN ('CASH', 'CARD', 'TRANSFER')),
    CONSTRAINT chk_invoices_status       CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);
GO
CREATE INDEX idx_invoices_employee  ON dbo.invoices (employee_id);
CREATE INDEX idx_invoices_status    ON dbo.invoices (status);
CREATE INDEX idx_invoices_created   ON dbo.invoices (created_at);
GO

-- 9. tickets
CREATE TABLE dbo.tickets (
    ticket_id         VARCHAR(20)    NOT NULL,
    invoice_id        VARCHAR(20)    NOT NULL,
    show_seat_id      VARCHAR(20)    NOT NULL,
    discount_id       VARCHAR(20)    NULL,   
    barcode           VARCHAR(50)    NOT NULL,
    original_price    DECIMAL(12,2)  NOT NULL,
    discount_amount   DECIMAL(12,2)  NOT NULL CONSTRAINT df_tickets_disc_amt DEFAULT 0,
    final_price       DECIMAL(12,2)  NOT NULL,
    issued_at         DATETIME       NOT NULL CONSTRAINT df_tickets_issued DEFAULT GETDATE(),
    status            VARCHAR(20)    NOT NULL CONSTRAINT df_tickets_status DEFAULT 'VALID',
    CONSTRAINT pk_tickets              PRIMARY KEY (ticket_id),
    CONSTRAINT fk_tickets_invoice      FOREIGN KEY (invoice_id)     REFERENCES dbo.invoices(invoice_id),
    CONSTRAINT fk_tickets_show_seat    FOREIGN KEY (show_seat_id)   REFERENCES dbo.show_seats(show_seat_id),
    CONSTRAINT fk_tickets_discount     FOREIGN KEY (discount_id)    REFERENCES dbo.discounts(discount_id),
    CONSTRAINT uq_tickets_barcode      UNIQUE (barcode),
    CONSTRAINT chk_tickets_price       CHECK (original_price >= 0),
    CONSTRAINT chk_tickets_disc        CHECK (discount_amount >= 0),
    CONSTRAINT chk_tickets_final       CHECK (final_price >= 0),
    CONSTRAINT chk_tickets_status      CHECK (status IN ('VALID', 'USED', 'CANCELLED'))
);
GO

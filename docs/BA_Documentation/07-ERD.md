# Entity Relationship Diagram (ERD)
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with discounts and show seat hold columns)

---

## 1. Mermaid Entity-Relationship Diagram (3NF)

```mermaid
erDiagram
    employees {
        varchar employee_id PK
        nvarchar full_name
        varchar username UQ
        varchar password
        varchar role
        varchar status
        varchar phone
        varchar email UQ
    }

    movies {
        varchar movie_id PK
        nvarchar title
        nvarchar genre
        int duration_mins
        varchar format_movie
        varchar rating
        nvarchar language
        date release_date
        varchar status
        nvarchar description
        varchar poster_url
    }

    cinema_halls {
        varchar hall_id PK
        nvarchar name UQ
        varchar hall_type
        int total_rows
        int total_cols
        varchar status
    }

    seats {
        varchar seat_id PK
        varchar hall_id FK
        char row_char
        int col_number
        varchar seat_type
        decimal seat_factor
        bit is_broken
    }

    showtimes {
        varchar showtime_id PK
        varchar movie_id FK
        varchar hall_id FK
        datetime start_time
        datetime end_time
        decimal base_price
        varchar status
    }

    show_seats {
        varchar show_seat_id PK
        varchar showtime_id FK
        varchar seat_id FK
        decimal price
        varchar status
        datetime held_until
    }

    discounts {
        varchar discount_id PK
        nvarchar discount_name
        varchar discount_type
        decimal discount_rate
        date valid_from
        date valid_to
        int min_ticket_quantity
        bit is_active
        ntext description
    }

    invoices {
        varchar invoice_id PK
        varchar employee_id FK
        datetime created_at
        decimal total_amount
        int total_tickets
        varchar payment_method
        varchar status
        nvarchar note
    }

    tickets {
        varchar ticket_id PK
        varchar invoice_id FK
        varchar show_seat_id FK
        varchar discount_id FK
        varchar barcode UQ
        decimal original_price
        decimal discount_amount
        decimal final_price
        datetime issued_at
        varchar status
    }

    cinema_halls ||--o{ seats : "has"
    employees ||--o{ invoices : "processes"
    movies ||--o{ showtimes : "scheduled in"
    cinema_halls ||--o{ showtimes : "hosts"
    showtimes ||--o{ show_seats : "maps"
    seats ||--o{ show_seats : "mapped in"
    invoices ||--o{ tickets : "contains"
    show_seats ||--|| tickets : "purchased as"
    discounts ||--o{ tickets : "applies to"
```

---

## 2. Relational Schema Compliance (3NF Analysis)

* **First Normal Form (1F):** Every column contains atomic values, and every record has a unique identifier (Primary Key).
* **Second Normal Form (2F):** All non-key fields depend entirely on the primary key, eliminating partial dependencies. (For instance, physical seats and show seats are split so that theater layout configurations do not replicate across individual film schedules).
* **Third Normal Form (3NF):** There are no transitive dependencies. (E.g. Invoice records store `employee_id`, and employee metadata remains inside the `employees` table. Showtime rates depend on base prices, and individual ticket pricing calculations do not pollute the main transaction files).

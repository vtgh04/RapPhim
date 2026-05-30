# Requirements Traceability Matrix (RTM)
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with Concurrency, Discount, and Auto-Print features)

---

## Requirements Traceability Mapping

| Bus. Req ID (BRD) | Sys. Req ID (SRS) | Description | Implementation Component / File | Database Entities | UAT Scenario |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **BR-01** (Zero Double Booking) | **FR-3.2** / **FR-3.3** | Lock seats dynamically as `HELD` during selection and broadcast `LOCKED` status. | [SeatStatusController.java](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/backend/src/main/java/rapphim/controller/SeatStatusController.java)<br>[SeatGrid.jsx](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/frontend/src/features/booking/SeatGrid.jsx) | `show_seats` | **UAT-RP-01** |
| **BR-01** | **NFR-2.2** | Serializable database transaction levels during hold/checkout. | [BookingService.java](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/backend/src/main/java/rapphim/service/BookingService.java) | `show_seats`, `tickets` | **UAT-RP-01** |
| **BR-02** (Hold Expiry) | **FR-3.5** | Automatically release seat holds after 5 minutes of inactivity. | [SeatCleanupScheduler.java](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/backend/src/main/java/rapphim/service/SeatCleanupScheduler.java) | `show_seats` | **UAT-RP-02** |
| **BR-03** (Fast POS UI) | **FR-2.1** / **FR-4.1** | Side-by-side seating matrix and quick checkout panel. | [SeatGrid.jsx](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/frontend/src/features/booking/SeatGrid.jsx)<br>[Checkout.jsx](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/frontend/src/features/booking/Checkout.jsx) | `seats`, `cinema_halls` | **UAT-RP-03** |
| **BR-04** (Auto Printing) | **FR-5.2** | Dispatch ticket/invoice to printer immediately upon checkout. | [Checkout.jsx](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/frontend/src/features/booking/Checkout.jsx) | `invoices`, `tickets` | **UAT-RP-04** |
| **BR-05** (Discounts) | **FR-4.2** / **FR-4.3** | Calculate discount codes based on rate, validity, and tickets count. | [Checkout.jsx](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/frontend/src/features/booking/Checkout.jsx) | `discounts` | **UAT-RP-03** |
| **BR-06** (Scheduler Overlaps) | **FR-2.2** | Block movie scheduling overlapping in same room. | [ShowtimeService.java](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/backend/src/main/java/rapphim/service/ShowtimeService.java) | `showtimes` | **UAT-RP-05** |
| **BR-07** (DB Integrity) | **NFR-1.3** | Relational constraints compliant with 3NF. | [Database.sql](file:///c:/Users/ADMIN/Desktop/CinemaProject/RapPhim/backend/database/scripts/Database.sql) | All 9 tables | **UAT-RP-06** |

# Business Requirements Document (BRD) & Product Strategy
**Project:** CinePro Management & POS System
**Document Owner:** Senior Business Analyst
**Status:** Approved | **Version:** 1.0

---

## 1. Executive Summary
CinePro is a comprehensive Point of Sale (POS) and Cinema Management desktop system designed to handle the end-to-end operational workflows of a modern movie theater. The system centralizes movie scheduling, seat inventory management, transaction processing, and management reporting.

## 2. Business Analysis (System Domain)
The system integrates several core operational modules:
*   **Movie Management:** Metadata repository for films (Title, Duration, Rating, Poster, Status).
*   **Showtime Management:** Operational scheduling bridging Movies and Cinema Halls.
*   **Seat Booking & Ticket Selling:** Real-time visual POS interface for allocating seats and generating tickets.
*   **Invoice & Payment:** Transaction tracking and PDF generation.
*   **Discount System:** Promotional code validation to adjust cart totals.
*   **Employee Management:** RBAC (Role-Based Access Control) for Staff and Managers.
*   **Reporting & Dashboard:** 30-day revenue analytics and top-performing movie metrics.

## 3. Stakeholder Analysis
### 3.1 Actors & Roles
| Actor | Description | System Permissions |
| :--- | :--- | :--- |
| **Cinema Manager** | High-level operations and strategic decision maker. | Full CRUD on Movies, Showtimes, Employees, Discounts. View Dashboard & All Invoices. |
| **Box Office Staff** | Frontline employee serving customers. | Sell Tickets, Apply Discounts, View Own Transactions. (Restricted from Settings/Dashboards). |
| **System Admin** | IT Support (Implicit). | Database maintenance, system configurations. |

### 3.2 Stakeholder Goals & Pain Points
*   **Goal:** Maximize ticket throughput during peak hours (blockbuster premieres) with zero double-bookings.
*   **Operational Pain Point:** Legacy systems suffer from slow checkout flows and accidental seat conflicts during concurrent sales. Staff training on complex UIs takes too long.
*   **Business Goal:** Provide accurate daily revenue reporting to prevent financial leakage.

---

## 4. Requirements Specification (SRS Extract)

### 4.1 Business Requirements (BR)
*   **BR-01:** The system must process a complete ticket sale (from seat selection to invoice printing) in under 15 seconds.
*   **BR-02:** The system must strictly enforce role segregation to prevent financial fraud by box office staff.

### 4.2 Functional Requirements (FR)
*   **FR-01 (Showtimes):** The system shall prevent scheduling overlapping showtimes within the same Cinema Hall.
*   **FR-02 (Seat Booking):** The system shall lock seats visually when `status = BOOKED` and prevent subsequent selection.
*   **FR-03 (Checkout):** The system must generate a unique `Invoice ID`, `Ticket IDs`, and Barcodes atomically during checkout.
*   **FR-04 (Export):** The system must output PDF receipts and Excel analytical reports.

### 4.3 Non-Functional Requirements (NFR)
*   **NFR-01 (Performance):** Dashboard data aggregation for a 30-day window must load in < 2 seconds.
*   **NFR-02 (Reliability/ACID):** The checkout process must be 100% atomic. If PDF generation fails, the database commit must still succeed, but if DB insert fails, nothing should be saved.
*   **NFR-03 (Usability):** The POS seat map must use universally recognized color codes (Red=Booked, Green=Selected, Yellow=VIP).

### 4.4 Constraints & Assumptions
*   **Constraint:** The application must run on existing mid-tier Windows machines using Java Swing.
*   **Assumption:** The database server (SQL Server) is hosted on a local network (LAN) with low latency.

---

## 5. Product Thinking & Strategy

### 5.1 UX & Operational Bottlenecks
*   **Current Bottleneck:** Staff have to manually hunt for showtimes in a list. 
*   **UX Problem:** If a customer changes their mind mid-checkout, rollback logic in UI can be clunky.
*   **Scalability Concern:** The Desktop/JDBC direct connection architecture limits the ability to add customer-facing mobile apps (requires an API layer).

### 5.2 Proposed Feature Improvements (Product Roadmap)
*   **Automation:** Auto-schedule showtimes based on previous week's performance data.
*   **Feature Expansion:** Introduce **"Seat Hold" (Pessimistic Locking)**. When a staff member clicks a seat, lock it for 3 minutes so other terminals cannot select it, preventing conflict at the `checkout` button.
*   **Customer Experience:** Implement a dual-screen display so customers can see the seat map in real-time.
*   **Payment Integration:** Integrate a dynamic QR code payment gateway (VNPay/Momo) directly into the POS UI.

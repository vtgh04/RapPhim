# Business Requirement Document (BRD)
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with Concurrency & POS printing features)

---

## 1. Project Context & Background
In high-volume cinema operations, ticket sales efficiency and data consistency are critical business requirements. Traditional desktop POS terminals (like older Java Swing systems) or simple non-synchronized booking systems lead to several major issues:
* **Double Booking:** Multiple cashiers selecting and booking the exact same seat simultaneously.
* **Customer Wait Times:** Cashiers taking too much time to select seats, check active promotions, confirm payments, and manually print physical tickets.
* **Management Gaps:** Lacking real-time dashboards to analyze daily sales, ticket categories, and theater capacity.

The **RapPhim** project bridges these gaps by modernizing the cinema ticketing pipeline with a Web POS client and a high-concurrency Spring Boot backend, incorporating real-time database locks and WebSocket STOMP synchronization to prevent double-booking.

---

## 2. Business Objectives & Strategic Goals
The primary business objectives for the system are:
1. **Zero Double-Bookings:** Eliminate resource conflicts completely during high-demand movie releases.
2. **Under 30-Second Transaction Time:** Optimize cashier checkout to require minimal steps (3 clicks maximum to checkout: Select Seat -> Select Payment -> Confirm).
3. **Automated POS Printer Dispatch:** Speed up counter operations by triggering ticket printer printing automatically upon successful checkout.
4. **Real-time Analytics Dashboard:** Provide managers with real-time stats and SVG-based charts to monitor sales and theater utilization immediately.

---

## 3. Stakeholder Analysis
The key stakeholders of the RapPhim system are:

| Stakeholder | Description & Goals | System Permissions / Role |
| :--- | :--- | :--- |
| **Cinema Manager (Admin)** | Oversees cinema operations, reviews revenue charts, configures movie lists, manages showtimes, layouts, and promotions. | Full system access (`MANAGER` / `ADMIN` role). |
| **Cinema Cashier (POS User)** | Processes walk-in sales quickly, selects seats for customers, applies discount codes, processes payment, and handovers printed tickets. | Ticketing and POS checkout access (`STAFF` role). |
| **Online Customers** | Access live seat mapping, view showtimes, select seats, and review movies. | Client UI access (read-only for seat layouts, write-only for reviews). |

---

## 4. Product Scope

### In-Scope (Phase 2 Upgrade)
* **Real-time Seat Lock State:** Seats clicked by a cashier turn orange immediately on other cashier screens and block selection.
* **Concurrency Locking Strategy:** Standard database constraints in 3NF layout with `SERIALIZABLE` transaction isolation when holding/booking seats to prevent race conditions.
* **Automated Expiry Cleanup:** Revert seat status from `HELD` to `AVAILABLE` automatically in the DB and UI after 5 minutes of inactivity.
* **POS Discount Recalculation:** Direct integration of promotions in the checkout summary. Supports minimum ticket requirements and valid date checks.
* **Instant Ticket Printing:** Automatic trigger of ticket printer formatting upon confirmation of invoice payment.

### Out-of-Scope (Future Phase)
* **Real Payment Gateway Integration:** Integrations with payment gateways (VNPAY, Momo, Stripe) remain mocked using a Payment Strategy Pattern.
* **Self-Service Online Booking App:** Focus is kept on cashier-facing counter POS terminals.

---

## 5. Key Metrics & KPIs

| Metric | Target / SLA | Measurement Method |
| :--- | :--- | :--- |
| **Double-booking Rate** | 0.00% | Database audit trail |
| **WebSocket Broadcast Latency** | < 200ms | Client-side performance trace |
| **POS Transaction Time** | < 30 seconds | Checkout session duration logs |
| **Print Dispatch Time** | < 1.5 seconds | Success callback execution latency |
| **Database Transaction Performance** | > 100 concurrent requests/sec | Load test under SERIALIZABLE isolation |

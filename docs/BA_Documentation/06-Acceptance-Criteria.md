# Acceptance Criteria Document
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with detailed testing scenarios)

---

## RP-101: Real-time Concurrency Seat Syncing

### AC-1: WebSocket State Propagation
* **Given** Cashier A and Cashier B are viewing the same Showtime seat map page,
* **When** Cashier A clicks seat `C3` (status is `AVAILABLE`),
* **Then** the system locks `C3` in the database,
* **And** broadcasts `{ seatId: "C3", status: "LOCKED" }` to Cashier B's screen in under **200ms**.

### AC-2: Visual Clues & Disabling Clicks
* **Given** Cashier B receives the `LOCKED` status event for `C3`,
* **When** the page renders,
* **Then** seat `C3` must turn orange (`bg-amber-500/20`),
* **And** its cursor must change to `cursor-not-allowed`,
* **And** Cashier B cannot select or click on seat `C3`.

---

## RP-102: Seat Hold Expiration

### AC-1: Automated Expiry & Release
* **Given** Cashier A selected seat `C3` (status is `HELD` in the database, `held_until` set to `now + 5 minutes`),
* **When** 5 minutes pass without Cashier A completing the checkout,
* **Then** the background scheduler database cleanup task resets `C3` to `AVAILABLE` and clears `held_until`,
* **And** broadcasts `{ seatId: "C3", status: "AVAILABLE" }` to all clients,
* **And** seat `C3` returns to standard styling (slate/VIP yellow) and becomes clickable.

---

## RP-201: Side-by-Side POS Layout

### AC-1: Responsive Grid Columns
* **Given** Cashier is logged in and opens `/booking/:showtimeId`,
* **When** page resolution is above HD (1366x768),
* **Then** the theater seat grid must display on the left (col-span 3),
* **And** the purchase selection summary (movie details, subtotal, next button) must display on the right (col-span 1).

---

## RP-202: Automatic Print Dispatch

### AC-1: Printing Dialogue Trigger
* **Given** Cashier clicks "Confirm Order" on the checkout page,
* **When** the API checkout response succeeds,
* **Then** the system must load the ticket PDF into a hidden iframe,
* **And** immediately trigger the native OS print dialogue box,
* **And** show the QR code receipt summary.

---

## RP-203: Cashier Discount Code Integration

### AC-1: Promotion Code Validation
* **Given** Cashier inputs code `DIS001` (valid for holiday discount),
* **When** they click "Apply",
* **Then** the system must check active state, date range, and ticket threshold,
* **And** calculate `subtotal - (subtotal * discountRate)`,
* **And** update the "Giảm giá" and "Tổng tiền thanh toán" instantly.

### AC-2: Validation Error Messages
* **Given** Cashier inputs code `DIS003` (requires 4 tickets),
* **When** they have only selected 2 seats,
* **Then** the system displays error `"Mã giảm giá yêu cầu tối thiểu 4 vé."`,
* **And** does not apply the discount.

---

## RP-302: Transactional Integrity (SERIALIZABLE Isolation)

### AC-1: Concurrency Conflict Resolution
* **Given** Cashier A and Cashier B click seat `C3` at the exact same millisecond,
* **When** the database transaction executes,
* **Then** the database locks the row for Cashier A first,
* **And** Cashier B's transaction throws an execution exception,
* **And** Cashier B's interface receives the updated lock state, turning seat `C3` orange/disabled.

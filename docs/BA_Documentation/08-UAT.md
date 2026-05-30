# User Acceptance Testing (UAT) Document
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with testing results for concurrency, printing, and discount features)

---

## UAT Test Scenarios & Execution Checklist

| Test ID | Test Scenario | Steps / Inputs | Expected Result | Status |
| :--- | :--- | :--- | :--- | :---: |
| **UAT-RP-01** | Concurrent Seat Selection Race Condition | 1. User A and B load the same Showtime.<br>2. Click seat `C3` at the same instant.<br>3. DB lock triggers under `SERIALIZABLE` isolation. | User A locks the seat (status `HELD` in DB, turns pink/selected in A's screen). User B's lock fails, gets error, and seat turns orange (disabled) in B's screen. | **Pass** |
| **UAT-RP-02** | Seat Hold Release on Timeout | 1. Cashier selects seat `C3`. Seat status changes to `HELD` in DB.<br>2. Cashier leaves tab idle.<br>3. Background clean-up task checks holds after 5 minutes. | Seat `C3` reverts to `AVAILABLE` in the database. Broadcast update triggers and seat becomes clickable (slate) on all active cashiers. | **Pass** |
| **UAT-RP-03** | Cashier Discount Engine Validation | 1. Select 2 seats (subtotal: 160,000đ).<br>2. Cashier applies code `DIS001` (15% rate, valid).<br>3. Try `DIS003` (requires 4 seats). | `DIS001` succeeds: discount recalculates instantly to 24,000đ, final total 136,000đ.<br>`DIS003` fails with error requiring 4 tickets. | **Pass** |
| **UAT-RP-04** | POS Automated Print Dispatch | 1. Cashier clicks "Confirm Order".<br>2. Successful response from `/api/bookings/checkout`. | Hidden iframe loads ticket PDF blob and triggers native system printer dialogue box automatically. QR Code receipt displays. | **Pass** |
| **UAT-RP-05** | Showtime Room Schedule Overlap Check | 1. Manager schedules movie A in Room 1 (9:00 - 11:26).<br>2. Schedules movie B in Room 1 (10:00 - 12:00). | System overlap detection counts active showtimes, rejects Movie B schedule, and blocks saving to prevent booking conflicts. | **Pass** |
| **UAT-RP-06** | 3NF Database Normalization Check | 1. Query database schema and audit ticket records. | Verification confirms room capacities and layouts are stored in `seats` and `cinema_halls`, rather than replicating in `tickets`. | **Pass** |

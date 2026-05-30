# Software Requirement Specification (SRS)
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with WebSocket concurrency and POS upgrades)

---

## 1. Introduction
This document defines the functional and non-functional requirements for the RapPhim Cinema POS and Ticketing system. It serves as a guide for the development team and QA engineers.

---

## 2. Functional Requirements (FR)

### FR-1: User Authentication & Role Management
* **FR-1.1:** Cashiers and managers must log in using username and password.
* **FR-1.2:** The system must issue JWT Access Tokens (valid for 24h) and Refresh Tokens (valid for 7 days) for session maintenance.
* **FR-1.3:** Endpoints must restrict access based on roles (`MANAGER`, `STAFF`).

### FR-2: Showtime Scheduling & Conflict Detection
* **FR-2.1:** Managers must be able to schedule showtimes.
* **FR-2.2:** The system must prevent scheduling showtimes that overlap in time inside the same room (`hall_id`).
* **FR-2.3:** Deleting a showtime must automatically clean up associated seat statuses (`show_seats`) in a cascade deletion transaction.

### FR-3: Seat Selection & Real-Time Sync
* **FR-3.1:** Cashiers must see a visual grid of seats for any chosen showtime.
* **FR-3.2:** When a cashier selects a seat, the system must set the status to `HELD` in the database with a 5-minute reservation timer (`held_until`) and immediately broadcast `LOCKED` via WebSockets to all other clients.
* **FR-3.3:** Other clients must display the held seat in orange and disable selection.
* **FR-3.4:** If a seat is deselected, the system must reset status to `AVAILABLE` in the database and broadcast `AVAILABLE` via WebSockets.
* **FR-3.5:** If a seat hold exceeds 5 minutes, a background task must revert the status to `AVAILABLE` in the DB and broadcast `AVAILABLE` to all clients.

### FR-4: POS Checkout & Promotion Engine
* **FR-4.1:** Cashiers can check out a transaction using three payment options: `CASH`, `CARD`, and `TRANSFER`.
* **FR-4.2:** Cashiers must be able to input discount codes (e.g. `DIS001`, `DIS003`). The system must validate:
  - If the discount is active (`isActive` is true).
  - If current time is within `validFrom` and `validTo`.
  - If ticket quantity meets `minTicketQuantity` requirements.
* **FR-4.3:** The discount value must subtract `subtotal * discountRate` immediately from the checkout invoice.
* **FR-4.4:** The system must record tickets in `BOOKED` status under a `SERIALIZABLE` database transaction.

### FR-5: Auto-Print Integration
* **FR-5.1:** Upon successful checkout, the system must generate a PDF invoice and a PDF ticket containing barcodes.
* **FR-5.2:** The POS interface must automatically trigger the standard printer dialogue using an iframe window print signal.

---

## 3. Non-Functional Requirements (NFR)

### NFR-1: Security & Compliance
* **NFR-1.1:** Passwords must be hashed.
* **NFR-1.2:** All communication between POS clients and the Spring Boot backend must be over HTTPS/WebSockets Secure.
* **NFR-1.3:** The database schema must be normalized to Third Normal Form (3NF).

### NFR-2: Performance & Concurrency
* **NFR-2.1:** WebSocket status updates must reach other terminals within **200ms** (target: < 50ms).
* **NFR-2.2:** Database updates for seat locking must execute under `SERIALIZABLE` isolation level to block phantom reads and double-bookings.
* **NFR-2.3:** Thread pools for PDF rendering must return invoice links under **1.5 seconds**.

### NFR-3: Reliability & Scalability
* **NFR-3.1:** The database must run inside Docker containers with auto-recovery health checks.
* **NFR-3.2:** If the WebSocket connection drops, the client must fallback to polling seat statuses every 15 seconds.

### NFR-4: Usability & Internationalization (i18n)
* **NFR-4.1:** Cashier screens must adapt to Full HD (1920x1080) and HD (1366x768) resolutions.
* **NFR-4.2:** The application must support seamless translation switches between English and Vietnamese.

# User Stories Document
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with POS printing and discount features)

---

## Epic 1: Real-Time Seat Synchronization (WebSocket STOMP)

### Story RP-101: Real-time Concurrency Seat Syncing
* **As a** Cashier (POS User),
* **I want** to see seat selections from other terminals update immediately on my screen without page reloads,
* **So that** I do not select a seat that another cashier is currently checking out.
* **Priority:** Critical (Must Have)
* **Estimation:** 8 Story Points
* **Business Value:** Prevents double-booking customer complaints at the box office.

### Story RP-102: Seat Hold Expiration
* **As a** Cinema Manager,
* **I want** seat holds to automatically expire after 5 minutes of cashier inactivity,
* **So that** locked seats are returned to the active pool if a transaction is abandoned.
* **Priority:** High (Should Have)
* **Estimation:** 5 Story Points
* **Business Value:** Maximizes ticket sales yield by avoiding orphan holds.

---

## Epic 2: Cinema POS Cashier Workflow

### Story RP-201: Side-by-Side POS Layout
* **As a** Cashier,
* **I want** a side-by-side view showing the theater seat matrix alongside the purchase summary panel,
* **So that** I can verify showtime details and prices in a single glance.
* **Priority:** High (Should Have)
* **Estimation:** 3 Story Points
* **Business Value:** Decreases cashier processing times and cognitive load.

### Story RP-202: Automatic Print Dispatch
* **As a** Cashier,
* **I want** the system to automatically trigger the ticket printer once payment is confirmed,
* **So that** I don't have to manually download, open, and print the PDF ticket.
* **Priority:** High (Should Have)
* **Estimation:** 5 Story Points
* **Business Value:** Decreases queue times at the cinema counter.

### Story RP-203: Cashier Discount Code Integration
* **As a** Cashier,
* **I want** to input discount codes at checkout and have the invoice total update instantly,
* **So that** I can apply promotions (holidays, group tickets, special events) accurately.
* **Priority:** High (Should Have)
* **Estimation:** 5 Story Points
* **Business Value:** Encourages promotional campaigns and group ticket sales.

---

## Epic 3: Database Integrity (3NF Schema Design)

### Story RP-301: 3NF Database Normalization
* **As a** Product Owner,
* **I want** the relational database schema to strictly conform to 3rd Normal Form (3NF),
* **So that** movie, seat, room, customer, and transaction records remain normalized with zero redundancy.
* **Priority:** Critical (Must Have)
* **Estimation:** 8 Story Points
* **Business Value:** Ensures structural integrity and prevents data anomalies.

### Story RP-302: Transactional Integrity (SERIALIZABLE Isolation)
* **As a** System Architect,
* **I want** database seat hold and booking transactions to execute under `SERIALIZABLE` isolation level,
* **So that** no phantom reads or race conditions occur during simultaneous checkouts.
* **Priority:** Critical (Must Have)
* **Estimation:** 5 Story Points
* **Business Value:** Resolves multi-user transaction conflicts at the DB level.

# Agile Backlog, Jira Setup & CV Portfolio Value
**Project:** CinePro Management & POS System

---

## 1. Agile Product Backlog (Jira Style)

### 1.1 Epics & Stories

#### Epic 1: Box Office Ticketing & Sales
**Description:** Implement a robust POS system for staff to sell tickets efficiently.

| Ticket ID | Type | Priority | Story Points | Title |
| :--- | :--- | :--- | :--- | :--- |
| POS-01 | Story | Highest | 8 | As a Staff, I want to view an interactive seat map so I can select available seats for customers. |
| POS-02 | Story | High | 5 | As a Staff, I want to apply a discount code to the cart so that promotional pricing is reflected. |
| POS-03 | Story | Highest | 13 | As the System, I must execute checkout as a single database transaction to prevent double booking. |

**Acceptance Criteria for POS-03 (Checkout Transaction):**
*   **GIVEN** a staff member has seats in the cart
*   **WHEN** they click "Checkout"
*   **THEN** the system must lock the seats, generate an invoice, and print tickets.
*   **AND** if any database insertion fails, all previous steps in the checkout must be rolled back entirely.

#### Epic 2: Management & Analytics Dashboard
**Description:** Equip managers with tools to oversee operations and revenue.

| Ticket ID | Type | Priority | Story Points | Title |
| :--- | :--- | :--- | :--- | :--- |
| MGT-01 | Story | High | 5 | As a Manager, I want to see a 30-day revenue line chart to analyze sales trends. |
| MGT-02 | Story | Medium | 3 | As a Manager, I want to export transaction history to Excel for accounting purposes. |
| MGT-03 | Story | High | 8 | As a Manager, I want to schedule showtimes, and the system must alert me if there is a time overlap in the same hall. |

---

## 2. CV / Portfolio Value (BA & System Analyst Perspective)

When presenting this project to recruiters, emphasize these key **Talking Points**:

### 2.1 The "BA Mindset" Showcased
*   **Workflow Optimization:** You didn't just build a UI; you mapped a complex user journey (Login -> Select Movie -> Pick Seat -> Validate Discount -> Checkout -> Print) and translated it into a streamlined UI.
*   **Business Rules Enforcement:** Implemented strict real-world business constraints, such as overlapping showtime prevention (Time A vs Time B logic) and role-based access control.

### 2.2 The "System Analyst / Technical BA" Edge
*   **ACID Transaction Modeling:** Understanding that "Ticket Booking" isn't just an INSERT statement. It requires transactional safety (Commit/Rollback) to prevent the "Double Booking" business nightmare. 
*   **Architecture Decoupling:** Recognizing the flaw of UI-to-Database direct calls and architecting the 3-Layer model (UI -> Service -> DAO) to ensure the system can scale to Web or Mobile APIs in the future.

### 2.3 Key Achievements for CV Bullets
> *   "Analyzed and re-engineered the ticket checkout process, establishing ACID-compliant transaction boundaries that eliminated the business risk of double-booked seats."
> *   "Defined and documented complex business rules for overlapping showtime schedules, directly preventing operational conflicts in cinema halls."
> *   "Authored comprehensive agile documentation (Epics, User Stories, Acceptance Criteria) to bridge the gap between cinema operational goals and technical implementation."

---

## 3. Gap Analysis (Level Assessment)

What level does this project put you at, and what's missing to reach Big Tech standards?

### Junior to Mid-Level BA (Current State)
*   **Achieved:** Strong grasp of functional requirements, database relationships (ERD), UI/UX workflow, and basic system architecture.
*   **Proof:** The strict 3-tier architecture and transactional safety logic.

### Senior / Big Tech BA (What to add next)
To elevate this portfolio to Big Tech standards, you should simulate/document the following:
1.  **Scalability Analysis:** Document how the system would handle 10,000 concurrent users booking *Avengers: Endgame* online (e.g., introducing Redis for seat-locking caching, Message Queues for ticket generation).
2.  **API Specifications (Swagger):** Big Tech projects are API-first. You should write a mockup OpenAPI/Swagger spec for the `POST /api/v1/checkout` endpoint.
3.  **A/B Testing & Data Analytics:** Define metrics. *How do we know the new POS UI is faster?* Define KPIs like "Average Time to Checkout" and "Error Rate during Booking".

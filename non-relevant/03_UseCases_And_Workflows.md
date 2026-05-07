# Use Cases, Workflows & Process Design

**Project:** CinePro Management & POS System

---

## 1. System Use Case Diagram

```mermaid
usecaseDiagram
    actor Staff
    actor Manager

    package "POS System" {
        usecase "UC1: Login" as UC1
        usecase "UC2: Select Movie & Showtime" as UC2
        usecase "UC3: Select Seats (Map)" as UC3
        usecase "UC4: Apply Discount" as UC4
        usecase "UC5: Checkout & Print Ticket" as UC5
    }

    package "Management System" {
        usecase "UC6: Manage Movies" as UC6
        usecase "UC7: Schedule Showtimes" as UC7
        usecase "UC8: View Analytics Dashboard" as UC8
        usecase "UC9: Manage Employees" as UC9
    }

    Staff --> UC1
    Staff --> UC2
    Staff --> UC3
    Staff --> UC4
    Staff --> UC5

    Manager --> UC1
    Manager --> UC6
    Manager --> UC7
    Manager --> UC8
    Manager --> UC9
  
    %% Manager inherits Staff abilities implicitly
    Manager --|> Staff
```

---

## 2. Use Case Specifications

### UC5: Checkout & Print Ticket

| Attribute                  | Description                                                                                                                                                                                                                                                         |
| :------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Actor**            | Box Office Staff                                                                                                                                                                                                                                                    |
| **Pre-condition**    | Staff is logged in; At least 1 seat is added to the cart.                                                                                                                                                                                                           |
| **Main Flow**        | 1. Staff clicks 'Checkout'.`<br>`2. System validates seat availability.`<br>`3. Staff selects payment method.`<br>`4. System generates Invoice and Tickets.`<br>`5. System updates seat status to 'BOOKED'.`<br>`6. System exports PDF and prompts print. |
| **Alternative Flow** | If Discount is applied, system calculates final total before step 3.                                                                                                                                                                                                |
| **Exception Flow**   | If a seat is already booked by another terminal (Race Condition), system aborts checkout, alerts Staff, and refreshes the seat map.                                                                                                                                 |
| **Post-condition**   | Invoice and Tickets saved to DB. PDF generated. Seat map locked.                                                                                                                                                                                                    |

---

## 3. Workflows & BPMN Analysis

### 3.1 Ticket Booking Activity Diagram (BPMN Style)

```mermaid
stateDiagram-v2
    [*] --> SelectShowtime
    SelectShowtime --> ViewSeatMap
  
    state ViewSeatMap {
        [*] --> SelectSeat
        SelectSeat --> ValidateSeat
        ValidateSeat --> AddToCart: Seat Available
        ValidateSeat --> AlertError: Seat Booked/Broken
    }
  
    ViewSeatMap --> ApplyDiscount
    ApplyDiscount --> ValidateDiscountCode
    ValidateDiscountCode --> ApplyDiscount: Valid
    ValidateDiscountCode --> AlertError: Invalid/Expired
  
    ApplyDiscount --> Checkout
    ViewSeatMap --> Checkout
  
    state CheckoutTransaction {
        [*] --> StartDBTransaction
        StartDBTransaction --> GenerateIDs
        GenerateIDs --> InsertInvoice
        InsertInvoice --> UpdateSeatStatus
        UpdateSeatStatus --> InsertTickets
        InsertTickets --> CommitTransaction
    }
  
    Checkout --> CheckoutTransaction
    CheckoutTransaction --> ExportPDF: Success
    CheckoutTransaction --> Rollback: Database Error / Conflict
    Rollback --> AlertError
  
    ExportPDF --> [*]
```

### 3.2 Sequence Diagram: Checkout Transaction Execution

```mermaid
sequenceDiagram
    actor Staff
    participant UI as SalePanel
    participant Service as SaleService
    participant DAO as Invoice/Ticket DAO
    participant DB as Database
    participant PDF as PDFExporter

    Staff->>UI: Click "Checkout"
    UI->>Service: processCheckout(cart, total)
  
    Service->>DB: conn.setAutoCommit(false)
    activate DB
  
    Service->>DAO: getNextInvoiceId()
    DAO->>DB: SELECT MAX(invoice_id)
    DB-->>Service: Return ID
  
    Service->>DAO: insertInvoice(data)
    DAO->>DB: INSERT INTO invoices
  
    loop Every Seat in Cart
        Service->>DAO: findShowSeatId(showtime, seat)
        DAO->>DB: SELECT show_seat_id
        DB-->>Service: Return show_seat_id
      
        Service->>DAO: updateShowSeatStatus('BOOKED')
        DAO->>DB: UPDATE show_seats
      
        Service->>DAO: insertTicket(data)
        DAO->>DB: INSERT INTO tickets
    end
  
    Service->>DB: conn.commit()
    deactivate DB
  
    Service->>PDF: exportTickets(invoice_id)
    PDF-->>Service: PDF Generated
  
    Service-->>UI: Return Success(true)
    UI-->>Staff: Show "Payment Successful" & Reset Cart
```

---

## 4. Stakeholder & Exception Thinking

* **Exception Handling:** The Sequence Diagram above clearly maps out why `setAutoCommit(false)` is crucial. If the loop fails on the 3rd seat (e.g., database timeout), the `conn.rollback()` is triggered. No phantom invoices or partial tickets will be left in the database, preventing accounting nightmares.

# User Persona Document
## Project Name: RapPhim - Modern Cinema POS & Ticket Management System
**Document Version:** 2.0 (Updated with Cashier POS and printing workflows)

---

## Persona 1: The Cinema Cashier (POS User)
*“I need to serve long queues of customers as fast as possible. Any lag or duplicate booking ruins my work day.”*

### Profile
* **Name:** Lan Anh
* **Age:** 21 (Part-time student / Cashier at Cinema)
* **Computer Skill Level:** Intermediate
* **Role:** POS Terminal Operator

### Goals & Behaviors
* Seeks a side-by-side seat grid layout to quickly map customer requests.
* Wants to apply promotion coupons instantly and verify if they are valid.
* Needs the ticket printing step automated so they don't have to navigate to another tab or click multiple print options.
* Needs immediate visual clues (orange seats) if someone at another counter selects a seat so they don't sell it twice.

### Pain Points
* Having to reload the page to see if seats are actually available.
* Invoices failing because another cashier clicked checkout a split-second earlier.
* Slow printing processes that delay lines and build up customer frustration.

---

## Persona 2: The Cinema Manager (Admin)
*“I want to schedule showtimes, manage pricing tiers, check sales reports, and create discount codes without technical overhead.”*

### Profile
* **Name:** Minh Duc
* **Age:** 35 (Cinema Hall Manager)
* **Computer Skill Level:** Advanced
* **Role:** Admin / System Configuration

### Goals & Behaviors
* Schedules the weekly movie roster across halls.
* Defines discounts (Holiday rates, group discounts) and sets conditions like minimum ticket counts and active dates.
* Wants automated checks when scheduling to avoid room overlap.
* Reviews sales charts and ticket distribution from the dashboard.

### Pain Points
* High-concurrency releases crashing the system.
* Accidental double-booking of halls because of scheduling overlaps.
* Having to rely on external software to compute monthly revenue charts.

---

## Persona 3: The Online Customer (End User)
*“I want to browse movies, look at seats, and check which seats are available for booking at the counter.”*

### Profile
* **Name:** Hoang Nam
* **Age:** 25 (Movie Enthusiast)
* **Computer Skill Level:** High
* **Role:** Movie Consumer

### Goals & Behaviors
* Browses movie lists, schedules, and reviews before heading to the cinema.
* Wants to see real-time seat states so they know which rows are open.
* Likes sharing reviews and rating movies.

### Pain Points
* Driving to the cinema only to find out the seats they saw on the screen were already booked.
* Out-of-date movie showtimes.

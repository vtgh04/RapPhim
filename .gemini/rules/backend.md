# Backend Rules & Standards

Guidelines for the Java SE/JDBC backend database queries and the Spring Boot RESTful API inside the `RapPhim/backend/` folder.

---

## 1. Database & JDBC Standards

The backend data engine runs on **Java 17**, **Microsoft SQL Server**, and **JDBC**.

### Transaction Management & ACID compliance
- **Enforced Rule**: Multi-query operations (such as checkout processes involving `Invoice` inserts, updating `show_seats` to `BOOKED`, and `Ticket` inserts) must be transactional.
- **Implementation**:
  - The JDBC connection must have Auto-Commit disabled: `connection.setAutoCommit(false);`
  - A single `Connection` object must be generated at the Service layer and passed down to each DAO method invoked within the service method.
  - Call `connection.commit()` at the end of the success path.
  - Call `connection.rollback()` in the `catch` block to abort all queries in case of failure.
  - Ensure the connection is closed in a `finally` block or via try-with-resources.

### SQL Injection Prevention
- **Enforced Rule**: Direct string concatenation (e.g. `"... WHERE username = '" + user + "'"` ) is strictly **forbidden**.
- **Implementation**: Always use `PreparedStatement` with parameterized placeholders (`?`) for inputs.

---

## 2. REST API & Spring Boot Standards

During the migration to a production-level web backend:

### Architecture & Validation
- **Clean Layers**: Direct Controller ➔ DAO flows are banned. Restructure endpoints to delegate business logic to Services, which then query Repositories/DAOs using DTOs.
- **Input Validation**: Use strict validation libraries (Jakarta Validation in Java or Zod/Joi in Node.js) to clean inputs at the Controller boundary.
- **Global Error Handling**: Implement a global exception filter/handler to intercept exceptions and map them to standard HTTP error envelopes.

### Core Security & Auth
- **Session & Identity**: Stateless JWT access/refresh token mechanism.
- **Role-Based Access Control (RBAC)**: Enforce route/method-level authorization checks based on user roles (Admin, Manager, Staff, Customer).
- **Sensitive Operations**: Encrypt passwords using BCrypt. Never store plain text passwords.

### Concurrency & Performance
- **Seat Locking (Redis)**: Lock selected seat IDs in Redis for exactly 3 minutes when starting checkout. If payment fails or times out, release the seat lock.
- **Database Optimization**: Ensure composite indexing is added for `showtime_id` and `seat_id` in `show_seats` to handle heavy seat status reads/writes. Use soft deletes (`is_active = 0` or `deleted_at`) instead of physical deletes.
- **API Documentation**: Maintain interactive API documentation via Swagger/OpenAPI.

---

## 3. Timezone Compliance (Vietnam ICT UTC+7)

- **Standard**: All showtimes, invoices, ticket generation, reports, and OTP durations must follow the Vietnam Timezone (`Asia/Ho_Chi_Minh`, UTC+7).
- **Date calculations**:
  - When filtering showtimes or transactions for a specific day, calculate absolute UTC+7 boundary limits:
    - Start boundary: `YYYY-MM-DDT00:00:00.000+07:00`
    - End boundary: `YYYY-MM-DDT23:59:59.999+07:00`
  - Safely parse date queries to avoid UTC drift on servers hosted in other regions.

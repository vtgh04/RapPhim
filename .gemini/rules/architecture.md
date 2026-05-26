# Architecture & Design Patterns

Design structures, folder layouts, and architectural patterns of the **backend** (Java Spring Boot) and **frontend** (React 19 / Vite) cinema application inside `RapPhim/`.

---

## 1. Java Backend Architecture (3-Tier & Clean)

The backend directory (`RapPhim/backend/`) contains the Java codebase.

- **View Layer (`rapphim.view` in Swing)**: 
  - Standard Java Swing components using the **FlatLaf** Look and Feel.
  - View panels capture user actions and immediately delegate execution to the Service layer. *Never call DAOs directly from the View layer.*
- **Controller Layer (`rapphim.controller`)**: 
  - Action controllers for complex authentication and UI event loops.
- **Service Layer (`rapphim.service`)**: 
  - Business logic orchestrator. Performs validation, handles ID generation, triggers helper utilities, and manages database transaction boundaries.
- **DAO Layer (`rapphim.dao`)**: 
  - Data Access Objects containing SQL statements mapping to SQL Server using raw JDBC.
- **Model Layer (`rapphim.model`)**: 
  - POJOs representing database tables (e.g., `Employee`, `Movie`, `Showtime`, `Ticket`, `Invoice`).
- **Config (`rapphim.config`)**: 
  - Database connection configurations (`DatabaseConnection.java`, `SecurityConfig.java`).

### RESTful API Clean Architecture Path
- Restructure backend layers into **Controller** (HTTP REST Endpoints), **Service** (Core Domain Business Logic), **Repository/DAO** (Database abstraction), and **DTO** (Data Transfer Objects).
- **Core Integrations**: JWT Access/Refresh tokens, Redis seat-locking (3 minutes), RabbitMQ or BullMQ asynchronous mail queues, and Swagger/OpenAPI documentation.

---

## 2. React Web Frontend Architecture (React 19 + Vite)

The frontend directory (`RapPhim/frontend/`) contains the React web client.

- **Feature-Based Architecture**:
  - `/src/features/booking/` for seat selection and checkout.
  - `/src/features/auth/` for login and registration.
  - `/src/shared/` for reusable UI components.
  - `/src/entities/` for TypeScript types and interfaces.
  - `/src/hooks/` for shared custom hooks.
- **State Management**:
  - **Client State**: Zustand / Redux Toolkit.
  - **Server State**: TanStack Query (React Query) for data caching and fetching.
- **Realtime Connection**: Socket.IO or WebSockets to dynamically push seat selection updates.

---

## 3. Design Patterns Applied

- **Singleton Pattern**: Managed connection pools (`DatabaseConnection`) to maintain a single resource endpoint safely.
- **Data Access Object (DAO) Pattern**: Decoupling database query strings from domain business calculations.
- **Observer/Callback Pattern**: Event-driven updates on Swing and React components.
- **Transaction Template**: Services manage the lifecycle of database sessions by disabling auto-commit, executing queries under a single JDBC connection, and rolling back fully on failures.

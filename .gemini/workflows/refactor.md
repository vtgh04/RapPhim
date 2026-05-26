# Refactoring Workflow

Guidelines for optimizing and restructuring existing code.

---

## 1. Scope & Alignment
- Break refactorings into isolated stages (e.g., Java Service layers first, then JDBC connection methods, then Swing View panels; or during upgrade: REST APIs first, then React features).
- Ensure existing features do not break. Take functional snapshots of components before starting.

---

## 2. Code Quality Checklist
- **SRP Check**: 
  - Swing Panels should not query database connections directly. Move SQL execution to DAOs and orchestrate business operations in Services.
  - Keep controllers, services, and models decoupled.
- **Resource Management**: 
  - Ensure all database connections, `PreparedStatement` instances, and `ResultSet` objects are closed properly (use try-with-resources where possible) to prevent memory and connection leaks in SQL Server.
- **Imports**: Clean up unused imports, dead parameters, and debug logging/console output.
- **Comments Integrity**: Retain unrelated comments and docstrings. Document new APIs and business flows clearly.

---

## 3. Post-Refactor Verification
- For Java desktop apps: Execute `.\mvnw.cmd compile` or run `.\mvnw.cmd test` inside `backend/` to verify zero build or compilation issues.
- For Web apps: Run production builds (`npm run build` or `.\mvnw.cmd package`) to confirm clean bundle/artifact generation.
- Confirm local application instances start up successfully and database handlers establish valid sessions.

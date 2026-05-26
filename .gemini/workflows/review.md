# Code Review Workflow

Standards and criteria for checking code changes before completion.

---

## 1. Safety & Credential Audits
- Check that no API tokens, local SQL Server usernames, passwords, or absolute system paths are committed to version control.
- Ensure credentials and connection configurations are resolved dynamically via configuration files (`application.properties` settings, environment variables, or `.env` files).

---

## 2. Compilation & Casing Checks
- **Java Build Verification**: Run `.\mvnw.cmd compile` inside `backend/` to check for compilation/type errors.
- **Windows Casing Check**: Confirm that all new or updated files imported into React components match exact filesystem casing to avoid Vite HMR crashes on Windows.
- **Port Checks**: Confirm Spring Boot / Express port remains `5001` or standard configured ports, and Vite remains `5173` on local runtimes.

---

## 3. Localization Verification
- Validate that all newly added user-facing copy, inputs, tooltips, or pagination elements mapped in either Swing views or React components resolve their text dynamically based on the active language setting (VI/EN).
- Verify that standard localized date formats display correctly (`DD/MM/YYYY`).

---

## 4. Database & Transaction Integrity
- Inspect database queries for SQL injection vulnerabilities. Confirm the usage of `PreparedStatement` everywhere.
- Verify that multi-statement queries run under proper transaction boundaries (auto-commit disabled, unified connection shared across calls, commit on success, rollback on catch block).
- Check that timezone offsets (+07:00) are appended to query parameters when filtering date boundaries.

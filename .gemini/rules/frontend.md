# Frontend Rules & Standards

Guidelines for the React 19/Vite web application located in the `RapPhim/frontend/` folder and legacy Swing GUI.

---

## 1. Web Frontend Guidelines (React 19 & Vite)

- **Tech Stack**: React 19, Vite, Tailwind CSS v4, Lucide React icons, and Framer Motion for animations.
- **Styling Philosophy**: Cinema-themed styling (Netflix, CGV, Galaxy Cinema vibes) with responsive layouts (Mobile-First) and full dark mode support.
- **Client State**: Manage UI interactions using **Zustand** or **Redux Toolkit**.
- **Server State**: Manage server data querying, cache invalidation, and data synchronization using **TanStack Query** (React Query).
- **Realtime Integration**: Use **Socket.IO** client bindings to update seat statuses in real time.
- **Directory Layout**: Use **Feature-Based Architecture**:
  - `/src/features/booking/` for seat selection and checkout.
  - `/src/features/auth/` for login and registration.
  - `/src/shared/` for reusable elements (buttons, modals, inputs).
- **Windows Case Casing**: Vite HMR and build pipelines ignore case mismatch issues on Windows but fail on production Linux servers. Ensure all component imports match the exact filesystem casing (e.g., `import SeatGrid from "./components/SeatGrid";`).

---

## 2. Legacy GUI Guidelines (Java Swing)

Located under `RapPhim/backend/src/main/java/rapphim/view`.

- **Look & Feel**: Enforce the **FlatLaf** modern flat theme. Avoid generic system-default frames.
- **Background Loading (`SwingWorker`)**:
  - **Enforced Rule**: Heavy processes (database reads for dashboard, transaction logs, PDF exporting, and Excel reports) must **not** run on the Event Dispatch Thread (EDT).
  - **Implementation**: Always wrap resource-intensive tasks inside a `SwingWorker` or run them on a background thread.
- **Seat Map component**: Custom visual buttons indicating seat conditions (VIP, Regular, Broken, Selected, Booked).

---

## 3. Bilingual Support (VI/EN) & Date Formats

- **Date Formatting**: Enforce standard localized date representation for displays: `DD/MM/YYYY`.
- **Bilingual Interface**:
  - The user interface must support both Vietnamese (VI) and English (EN).
  - All labels, buttons, tooltips, error boundaries, receipt templates, and validation alerts must dynamically resolve their strings based on the selected language setting.

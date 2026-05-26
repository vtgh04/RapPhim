# UI/UX & Design Guidelines

Design aesthetics, theme guidelines, layouts, and user-experience patterns for the Cinema POS and Web Client systems.

---

## 1. Cinema Aesthetic & Themes

- **Visual Tone**:
  - The design should invoke a premium cinema atmosphere (inspired by modern digital platforms like Netflix, CGV, or Galaxy Cinema).
  - Use vibrant dark modes, subtle movie poster backdrops, and deep, dark background colors paired with vibrant, HSL-tailored primary/secondary colors.
- **Harmonious Palettes**:
  - Avoid crude, default primary colors (pure red `#FF0000`, pure green `#00FF00`, pure blue `#0000FF`).
  - Use custom HSL tones and rich gradients:
    - *VIP Seat*: Soft amber/gold gradients.
    - *Available Seat*: Sleek Slate or soft Indigo.
    - *Selected Seat*: Vibrant violet or cyan highlight.
    - *Booked Seat*: Low-opacity muted gray.
    - *Broken Seat*: Faint rose accent.
- **Glassmorphism**: Combine translucent background panels (`backdrop-blur` with low-opacity borders) to simulate depth and floating card layers over cinematic backdrops.

---

## 2. Interactive Seat Map UX

- **Sizing & Grid**: The seat map must be clear, adaptive, and maintainable. Map seats with clear row letters (A, B, C...) and column numbers (1, 2, 3...).
- **States**: Provide immediate visual feedback on hover (subtle scaling, border glow) and selection.
- **Realtime seat-lock indicator**: Show countdown timers for locked seats (3-minute locking expiration) on checkout modals.

---

## 3. Telemetry & Analytics Layouts

- **Data Representation**:
  - Use clean, modern charting tools (JFreeChart for Swing, Recharts/Chart.js for Web).
  - Animate charts on load. Avoid cluttered labels; display details on tooltips or hover cards.
- **Export Formats (PDF/Excel)**:
  - **PDF Invoice**: Create a clean, grid-based receipt design. Include a unique generated barcode/QR code, professional invoice header details, and clear breakdowns of ticket items, VAT, and applied discount rates.
  - **Excel reports**: Standardize columns. Design headers with professional background fills (e.g. slate or dark navy), enable text wrapping, and autofit column widths for high legibility.

---

## 4. Micro-Animations & Transitions

- **Component Interaction**: Add brief, smooth micro-animations (e.g., button scaling, hover lift, slide-in sidebar, fade-in lists). Keep durations around 150ms-300ms.
- **Transitions**: Enable skeleton loaders during network latency or background computations (`SwingWorker` actions).
- **Responsive Layouts**: Design mobile-first on the web. Implement collapsible navigation menus, swipeable poster carousels, and grid-wrapping seat guides.

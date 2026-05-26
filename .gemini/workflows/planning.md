# Planning Workflow

Step-by-step process for planning new features, major refactorings, or modifications.

## Phase 1: Research & Discovery
- Analyze existing components, schemas, and API routers.
- Run tests or check current state behavior to ensure understanding.
- Do NOT make code modifications or write updates during this phase.

## Phase 2: Design the Implementation Plan
- Document the proposed changes in `implementation_plan.md`.
- Group planned changes logically (e.g. by component or layer, dependencies first).
- Add [NEW], [MODIFY], or [DELETE] tags next to target file basenames with active markdown links.
- List exact verification plans (automated tests, manual validation commands).

## Phase 3: Checklists & Execution
- Create a `task.md` file as a TODO task tracker.
- Use completion status:
  - `[ ]` for pending tasks
  - `[/]` for in-progress tasks
  - `[x]` for completed tasks
- Request review/approval from the developer before executing large code blocks.

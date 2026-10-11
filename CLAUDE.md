# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## MiniSpec (read first)

Before writing any code, read `.minispec/README.md` and follow its reading contract. As a minimum, always read `.minispec/core/project.md`, `.minispec/core/conventions.md` and `.minispec/core/principles.md`; read the rest of `.minispec/` only on demand (architecture, stack, glossary, the relevant feature or ADR). Keep features small and don't write redundant documentation.

The project description, architecture, stack and conventions live in `.minispec/core/`; this file only keeps what's needed to build and test.

## Build and test

- `mvn compile` builds the library (Java 17, JavaFX 21 LTS; see ADR-001 before bumping either).
- `mvn test` runs the JUnit 6 suite headlessly: surefire sets `glass.platform=Monocle`/`monocle.platform=Headless`, so no window opens and no display is needed. The "Unsupported JavaFX configuration: classes were loaded from 'unnamed module'" warning is expected (JavaFX on the classpath).
- `mvn test -Dtest=FormTest#bindsTextBidirectionally` runs a single test.
- Tests touch JavaFX through `FxThread` (`src/test/java/.../FxThread.java`): `FxThread.call(...)`/`run(...)` execute on the FX thread and wait. Controls in a built form are found by their row label (see `FormTest.control(...)`).
- `EditUserApp` and `User` are a manual visual demo; `AppTest` launches it but is `@Disabled` because it blocks until the window is closed.

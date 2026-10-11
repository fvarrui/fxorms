# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## MiniSpec (read first)

Before writing any code, read `.minispec/README.md` and follow its reading contract. As a minimum, always read `.minispec/core/project.md`, `.minispec/core/conventions.md` and `.minispec/core/principles.md`; read the rest of `.minispec/` only on demand (architecture, stack, glossary, the relevant feature or ADR). Keep features small and don't write redundant documentation.

The project description, architecture, stack and conventions live in `.minispec/core/`; this file only keeps what's needed to build and test.

## Build and test

- `mvn compile` builds the library (Java 17, JavaFX 21 LTS; see ADR-001 before bumping either).
- `mvn test` runs the JUnit 6 suite headlessly: surefire sets `glass.platform=Monocle`/`monocle.platform=Headless`, so no window opens and no display is needed. The "Unsupported JavaFX configuration: classes were loaded from 'unnamed module'" warning is expected (JavaFX on the classpath).
- `mvn test -Dtest=FormTest#bindsTextBidirectionally` runs a single test.
- `mvn javadoc:javadoc` generates the API docs in `target/reports/apidocs/`. It runs with `doclint=all` and `failOnWarnings`, so any public or protected member without complete Javadoc fails the build.
- Tests touch JavaFX through `FxThread` (`src/test/java/.../FxThread.java`): `FxThread.call(...)`/`run(...)` execute on the FX thread and wait. Controls in a built form are found by their row label (see `FormTest.control(...)`).
- `EditUserApp` and `User` are a manual visual demo; `AppTest` launches it but is `@Disabled` because it blocks until the window is closed.
- `src/test/java/.../examples/` holds the README use cases (`Examples`), checked by `ExamplesTest` and viewable with `ExamplesApp`. When an example or the look of the controls changes, regenerate the README images in `assets/examples/` with `mvn test -Dtest=ScreenshotsTest -Dscreenshots=true` (headless, US locale) and keep the README snippets in sync with the example code.
- The Javadoc of the latest release is published to GitHub Pages (https://fvarrui.github.io/fxorms/) by `.github/workflows/javadoc.yml` when a release is published; run the workflow by hand to republish a tag.

## Project skills

- `verify` runs the checks (tests, Javadoc lint and, when modules, packages or FXML change, a module-path smoke test). Use it before commits and releases.
- `add-adapter` adds support for a new property type following the project conventions.
- `release` releases a version: notes, signing, Maven Central upload, tag, GitHub release and next snapshot; it also publishes an existing tag on Central.
- `minispec-feature`, `minispec-bugfix`, `minispec-adr` and `minispec-implement` manage the specs in `.minispec/`.
